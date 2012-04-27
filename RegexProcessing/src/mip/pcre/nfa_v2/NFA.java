/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.nfa_v2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import mip.pcre.pcre_v2.*;
import mip.pcre.parsetree_v2.*;

/**
 *
 * @author Hoang Long Le & Heckarim
 */
public class NFA {
    // Each NFA must have Start and End state.

    public NFAState start = null;  // remember that it also stay in lState.
    public NFAState end = null;   // ...
    //Keep all State and char Transition in LinkedList.
    public LinkedList<NFAState> lState;
    public LinkedList<NFAEdge> lEdge;
    //Parse tree it is costructed from;
    public RegexTree tree = null;
    // other information
    public static String _default_folder = System.getProperty("user.dir") + System.getProperty("file.separator");
    public static String _default_file_name = "nfa.dot";

    public NFA() {
        this.start = null;
        this.end = null;
        this.lEdge = new LinkedList<NFAEdge>();
        this.lState = new LinkedList<NFAState>();
    }

    public NFA(NFAState start) {
        this.start = start;
        this.end = null;
        this.lEdge = new LinkedList<NFAEdge>();
        this.lState = new LinkedList<NFAState>();

    }

    public NFA(NFAState start, NFAState exit) {
        this.start = start;
        this.end = exit;
        this.lEdge = new LinkedList<NFAEdge>();
        this.lState = new LinkedList<NFAState>();
    }

    /**
     * 
     * @param from
     * @param to
     */
    public void insertEdgeEpsilon(NFAState from, NFAState to) {
        //Insert edge from from to to.
        NFAEdge newEdge = new NFAEdge();

        newEdge.isEpsilon = true;
        newEdge.dstState = to;
        newEdge.srcState = from;
        // insert edge to fromState
        from.addGoingEdge(newEdge);
        to.addComingEdge(newEdge);
        //from.lGoingEdge.addLast(newEdge);
        //to.lComingEdge.addLast(newEdge);
        // also insert it to current NFA
        this.lEdge.addLast(newEdge);
    }
    
    /**
     * Insert nfa to this NFA by connecting StartState of this NFA to startstate of nfa.
     * 
     * @param nfa
     * 
     */
    public void insertNFA(NFA nfa) {
        //Create new epsilon edge.
        NFAEdge newEdge = new NFAEdge();
        newEdge.isEpsilon = true;
        newEdge.srcState = this.start;
        newEdge.dstState = nfa.start;
        // insert new edge to StartState
        this.lEdge.addLast(newEdge);
        this.start.addGoingEdge(newEdge);
        nfa.start.addComingEdge(newEdge);
        //this.start.lGoingEdge.add(newEdge);
        
        //set start state of nfa to normal state
        nfa.start.isStart = false;
        // insert edge of nfa to this NFA
        this.lEdge.addAll(nfa.lEdge);
        // insert state of nfa to this NFA
        this.lState.addAll(nfa.lState);
    }

    /**
     *
     * @param from
     * @param to
     * @param node
     */
    public NFAEdge insertEdge(NFAState from, NFAState to, Node node) {

        //Ham insert canh vao nfastate frome, cahj epsilon nam dau tien, canh char nam cuoi cung
        NFAEdge newEdge = new NFAEdge();

        //prepare Edge info
        newEdge.isEpsilon = false;
        newEdge.id = node.id;
        newEdge.value = node.value;
        newEdge.dstState = to;
        newEdge.srcState = from;
        //inser to fromState
        //from.lGoingEdge.addLast(newEdge);
        from.addGoingEdge(newEdge);
        to.addComingEdge(newEdge);
        // also insert to curren NFA
        this.lEdge.add(newEdge);
        return newEdge;
    }


    public void insertStartState(NFAState startState) {
        NFAState temp = this.start;
        temp.isStart = false;
        this.start = startState;
        //insert to lState
        this.lState.add(startState);
        // insert edges of startState to lEdge
        this.lEdge.addAll(startState.getListGoingEdge());
        // insert edge from new startState to old startState
        this.insertEdgeEpsilon(this.start, temp);
    }

    public void insertEndState(NFAState endState) {
        NFAState temp = this.end;
        //old endState isn't endState anymore
        this.end.isFinal = false;
        // new one replace
        this.end = endState;
        // insert new State to lState
        this.lState.add(endState);
        // create new edge from old to new one
        this.insertEdgeEpsilon(temp, endState);

    }

    public void generateDotFile(String name, String folder) {
        this.updateList();
        BufferedWriter bw = null;
        try {
            if (null == folder || folder.isEmpty()) {
                folder = _default_folder;
            }
            if (null == name || name.isEmpty()) {
                name = _default_file_name;
            }
            bw = new BufferedWriter(new FileWriter(folder + name));
            bw.write("digraph \"nfa path\" {"
                    + "\ngraph [ranksep=.2,rankdir=LR];"
                    + "\nnode [shape=circle,fontname=Arial,fontsize=14];"
                    + "\nnode [width=1,fixedsize=true];"
                    + "\nedge [fontname=Arial,fontsize=14];"
                    + "\n-1 [width=0.2,shape=point color=red];"
                    + "\n-1 -> 0 [ color=red];");

            for (int i = 0; i < this.lState.size(); i++) {
                NFAState p = this.lState.get(i);
                if (p.isFinal || p.isStart) {
                    bw.write("\n" + i + " [label=q" + i + " color=red];");
                } else {
                    bw.write("\n" + i + " [label=q" + i + " color=green];");
                }
            }
            for (int i = 0; i < this.lEdge.size(); i++) {
                NFAEdge q = this.lEdge.get(i);
                String color = "";
                if (q.isEpsilon) {
                    color = "red";
                } else {
                    color = "black";
                }
                bw.write("\n" + lState.indexOf(q.srcState) + " -> " + lState.indexOf(q.dstState) + "  [label=\"[" + q.value + "]\" color=" + color + "];");
            }

            bw.write("\n}\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * This function:
     *      +, start state at first
     *      +, endstate at last
     *      +, order all state.
     */
    public void updateList() {
        if (!this.lState.getLast().isStart) {
            for (int i = 0; i < this.lState.size(); i++) {
                if (this.lState.get(i).isStart) {
                    NFAState temp = this.lState.remove(i);
                    this.lState.addFirst(temp);
                }
            }
        }
        if (!this.lState.getLast().isFinal) {
            for (int i = 0; i < this.lState.size(); i++) {
                if (this.lState.get(i).isFinal) {
                    NFAState temp = this.lState.remove(i);
                    this.lState.addLast(temp);
                }
            }
        }
        for (int i = 0; i < this.lState.size(); i++) {
            this.lState.get(i).order = i;
        }
    }

    /**
     * This is important function,  nfa is needed to deleteRedundant before
     * further processing.
     * what do it do????
     * Simply: State with all input and output edge is epsilon will be eliminated.
     */
    public void reduceRedundantState() {
        LinkedList<NFAState> lreduce = new LinkedList<NFAState>();
        //gurantee that no circular epsilon edge
        //todo
        //...//
        for (int i = 0; i < this.lState.size(); i++) {
            NFAState state = this.lState.get(i);
            //System.out.println("State " + i);
            if (lreduce.indexOf(state) != -1) {
                continue;
            }
            for (int j = 0; j < state.getListGoingEdge().size();) {
                NFAEdge ed = state.getListGoingEdge().get(j);
                if (ed.isEpsilon && !ed.dstState.isFinal && ed.dstState.getnonEpsilonEdge() == null) {
                    //insert this state to reduce list
                    lreduce.add(ed.dstState);
                    //delete this edge
                    state.removeGoingEdge(ed);
                    ed.dstState.getListComingEdge().remove(ed);
                    this.lEdge.remove(ed);
                    // combine these edges to current state
                    this.combineto(state, ed.dstState);
                } else {
                    j++;
                }
            }
        }
        //Now remove del list
        for (int i = 0; i < lreduce.size(); i++) {
            this.reduceState(lreduce.get(i));
        }
    }

  

    /**
     * in NFA full form 
     */
    /**
     * That means dst contains all epsilon edge to other State
     * this function is in charge of connect state to all going states of  dst with the main goal behind, eliminating dst.
     * @param state
     * @param dst
     */
    private void combineto(NFAState state, NFAState dst) {
        for (int i = 0; i < dst.getListGoingEdge().size(); i++) {
            NFAEdge etemp = dst.getListGoingEdge().get(i);
            if(!this.isHaveEpsilonEdge(state,etemp.dstState))
            	this.insertEdgeEpsilon(state, etemp.dstState);
        }
        //don't forget to removing all epsilon edge from dst to its going state.
        for (int i = 0; i < dst.getListGoingEdge().size(); i++) {
            NFAEdge etemp = dst.getListGoingEdge().get(i);
            etemp.dstState.removeComingEdge(etemp);
            this.lEdge.remove(etemp);
        }
    }

    private boolean isHaveEpsilonEdge(NFAState state, NFAState dstState) {
		for(int i =0; i<this.lEdge.size(); i++){
			if(this.lEdge.get(i).srcState == state && this.lEdge.get(i).dstState == dstState)
				return true;
		}
		return false;
	}

	private void reduceState(NFAState state) {
        for (int i = 0; i < state.getListGoingEdge().size(); i++) {
            this.lEdge.remove(state.getListGoingEdge().get(i));
        }
        for (int i = 0; i < state.getListComingEdge().size(); i++) {
        	if(this.lEdge.indexOf(state.getListComingEdge().get(i)) != -1)
        		this.lEdge.remove(state.getListComingEdge().get(i));
        }
        this.lState.remove(state);
    }

    public PCREPattern getRule() {
        if (this.tree != null) {
            return this.tree.rule;
        }
        return null;
    }

    /**
     * using to convert to DFA.
     */
    public void convertTo256() {
        for (int i = 0; i < this.lEdge.size(); i++) {
            this.lEdge.get(i).converto256();
        }
    }

    public void updateModifier() {
        for (int i = 0; i < this.lEdge.size(); i++) {
            this.lEdge.get(i).modifier = this.tree.rule.getModifier();
        }
    }

	public void setRegexTree(RegexTree tree) {
		this.tree = tree;
		
	}

	/**
	 * Create new Start State
	 */
	public void createStartState() {
		start = new NFAState();
		start.isStart = true;
		this.lState.add(start);
	}
	

	
	/**
	 * Show information
	 * +, no State
	 * +, no Epsilon edge
	 * +, no nonEpsilon edge
	 * +, no end state.
	 */
	public void printInfo(){
		int tmp;
		System.out.println("printInfo():  ");
		//no state
		System.out.println("No States: " + this.lState.size());
		tmp =0;
		//no endstate
		for (int i =0; i<this.lState.size(); i++){
			if(this.lState.get(i).isFinal ==true)
				tmp++;
		}
		System.out.println("No EndState: " + tmp);
		// NO Edge
		System.out.println("No Edge: " + this.lEdge.size());
		// no epsilon edge
		tmp=0;
		for(int i =0; i<this.lEdge.size(); i++){
			if(this.lEdge.get(i).isEpsilon)
				tmp ++;
		}
		System.out.println("No Epsilon Edge: " +tmp); 
		//no non Epsitlon edge
		System.out.println("No NonEpsilon Edge: " + (this.lEdge.size() - tmp));
	}
	
	/**
	 * HOw to sharing prefix???
	 * +, first, compress nfa machine in to compact form.
	 * +, perform redude 
	 * +, expand it to modular form.
	 * ....
	 */
	/**
	 * reduce all epsilon edge
	 */
	public void convert2NormalForm(){
		//this.printStructure();
		for (int i =0; i < this.lEdge.size(); i++){
			NFAEdge edge = this.lEdge.get(i);
			if(edge.isEpsilon){// remove it!!!!
				//if circular epsilon edge, simply remove and continue
				if(edge.srcState == edge.dstState){
					this.lEdge.remove(edge);
					edge.srcState.removeGoingEdge(edge);
					edge.dstState.removeComingEdge(edge);
					i--;
					continue;
				}
				//remove it from edge
				this.lEdge.remove(edge);
				i--;
				//to => from
				NFAState from = edge.srcState;
				NFAState to = edge.dstState;
				//add all outgoing edge from to to from
				for(int j =0; j< to.getListGoingEdge().size(); j++){
					to.getListGoingEdge().get(j).srcState = from;
					//if this edge is epsilon circular edge, remove it
					if(to.getListGoingEdge().get(j).isCircularEpsilonEdge()){
						to.getListGoingEdge().get(j).remove();
						this.lEdge.remove(to.getListGoingEdge().get(j));
					}
				}
				//add all Coming edge from to to from
				for(int j =0; j< to.getListComingEdge().size(); j++){
					to.getListComingEdge().get(j).dstState = from;
					//if this edge is epsilon circular edge, remove it
					if(to.getListComingEdge().get(j).isCircularEpsilonEdge()){
						to.getListComingEdge().get(j).remove();
						this.lEdge.remove(to.getListComingEdge().get(j));
					}
				}
				from.getListGoingEdge().addAll(to.getListGoingEdge());
				from.getListComingEdge().addAll(to.getListComingEdge());
				//change all incomming edge to to to from
				for(int j = 0; j<to.getListComingEdge().size(); j++){
					to.getListComingEdge().get(j).dstState = from;
				}
				//don't forget to can be final state
				if(to.isFinal)
					from.isFinal =true;
				//don't forget to remove this state but, if from == to ??_??
				if(from == to )
					;
				else
					this.lState.remove(to);
			}
			this.updateList();
			//this.printStructure();
		}
	}
	/**
	 * print structure base on state
	 */
	private void printStructure() {		
		System.out.println("printStructure(): No State " + this.lState.size() + " no edge: " + this.lEdge.size());
		for(int i =0; i<this.lState.size(); i++){
			NFAState state = this.lState.get(i);
			//print edge
			System.out.println("\t State " + state.order + " no going edge: " + state.getListGoingEdge().size() + " no coming edge: " + state.getListComingEdge().size());
			for(int j =0 ;j < state.getListGoingEdge().size(); j++){
				System.out.println(" \t\tgoto " + state.getListGoingEdge().get(j).dstState.order);
			}
			
		}
	}

	/**
	 * Sharing prefix of NFA
	 */
	public void prefixSharing(){
		for(int i =0; i<this.lState.size(); i++){
			this.lState.get(i).status = 0;
		}
		this.prefixSharingState(this.start);
	}
	
	public void prefixSharingState(NFAState state){
		//System.out.println("prefixsharing for state: " + state.order);
		if(state.status ==1 )
			return;
		else
			state.status = 1;
		for(int i =0; i<state.getListGoingEdge().size(); i++){
			NFAState tmp0 = state.getListGoingEdge().get(i).dstState;
			//System.out.println("\t checking state: " + tmp0.order);
			for(int j = i+1; j<state.getListGoingEdge().size(); j=j+1){
				NFAState tmp1 = state.getListGoingEdge().get(j).dstState;
				//System.out.println("\t\t compare with state: " + tmp1.order + " comingedge: " + tmp1.getListComingEdge().size());
				/*for(int k = 0; k<tmp1.getListComingEdge().size(); k++){
					System.out.println("\t\t\t" + tmp1.getListComingEdge().get(k).srcState.order);
				}*/
				if(tmp1 == tmp0)
					continue;
				
				if(this.isSameComingState(tmp0,tmp1)){
					//reduce tmp1 to tmp0.
					this.reduceToState(tmp0, tmp1);
					//since all edge of tmp1 is reduced 
					j=j-1;
				//	System.out.println("\t\t\t is reduce: j = " + j);
				}
				
					
			}
			
		}
		// now perform prefix sharing recursively
		for(int i =0; i<state.getListGoingEdge().size(); i++){
			NFAState tmp0 = state.getListGoingEdge().get(i).dstState;
			this.prefixSharingState(tmp0);
		}
		
	}

	/**
	 * reduce tmp1 to tmp0
	 * @param tmp0
	 * @param tmp1
	 */
	private void reduceToState(NFAState tmp0, NFAState tmp1) {
		// remove all going edge inf coming state
		// remove all edge in this.edge
		//System.out.println("Reduce to State: ");
		//System.out.println("tmp1 going list: " + tmp1.getListGoingEdge().size()+ " coming: " + tmp1.getListComingEdge().size());
		//System.out.println("tmp0 going list: " + tmp0.getListGoingEdge().size()+ " coming: " + tmp0.getListComingEdge().size());
		for (int i =0; i<tmp1.getListComingEdge().size(); i++){
			NFAEdge edge = tmp1.getListComingEdge().get(i);
			edge.srcState.removeGoingEdge(edge);
			this.lEdge.remove(edge);
		}
		//System.out.println( " xxx  ");
		// combine all going edge
		for (int i =0; i<tmp1.getListGoingEdge().size(); i++){
			NFAEdge edge = tmp1.getListGoingEdge().get(i);
			edge.srcState = tmp0;
			tmp0.getListGoingEdge().add(edge);
			//System.out.print( " 1 ");
		}
		// remove tmp1
		this.lState.remove(tmp1);
		
		//System.out.println("tmp0 going list: " + tmp0.getListGoingEdge().size()+ " coming: " + tmp0.getListComingEdge().size());
		
	}

	//check if is the same
	private boolean isSameComingState(NFAState tmp0, NFAState tmp1) {
		if(!this.isPartOfCommingState(tmp0, tmp1))
			return false;
		//System.out.println("\t\t\t tmp0 is part of tmp1: ");
		if(!this.isPartOfCommingState(tmp1, tmp0))
			return false;
		//System.out.println("\t\t\t tmp1 is part of tmp0: ");
		return true;
	}
	
	//check if comming edge of tmp0 is part of tmp1's
	private boolean isPartOfCommingState(NFAState tmp0, NFAState tmp1){
		//System.out.println("tmp0: " + tmp0.order + "  tmp1 " + tmp1.order);
		for(int i =0; i<tmp0.getListComingEdge().size(); i++){
			NFAEdge edge0 = tmp0.getListComingEdge().get(i);
			boolean isHave = false;
			//System.out.println("tmp0: edge " + edge0.value );
			for(int j =0; j<tmp1.getListComingEdge().size(); j++){
				
				NFAEdge edge1 = tmp1.getListComingEdge().get(j);
				//System.out.println("\t tmp1: edge " + edge1.value );
				if(edge0.isEqualTo(edge1)){
					isHave = true;
					break;
				}
			}
			if(!isHave)
				return false;
		}
		return true;
	}
}


