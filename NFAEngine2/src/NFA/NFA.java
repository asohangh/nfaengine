/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NFA;

import PCRE.PcreRule;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import ParseTree.Node;
import ParseTree.ParseTree;
import PCRE.Refer;
import java.util.LinkedList;

/**
 *
 * @author Hoang Long Le
 */
public class NFA {
    // Each NFA must have Start and End state.

    public NFAState start = null;  // remember that it also stay in lState.
    public NFAState end = null;   // ...
    //Keep all State and char Transition in LinkedList.
    public LinkedList<NFAState> lState;
    public LinkedList<NFAEdge> lEdge;
    //Parse tree it is costructed from;
    public ParseTree tree = null;
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
        if (newEdge == null) {
            return;
        }
        newEdge.isEpsilon = true;
        newEdge.dstState = to;
        newEdge.srcState = from;
        // insert edge to fromState
        from.lEdge.addLast(newEdge);
        // also insert it to current NFA
        this.lEdge.addLast(newEdge);
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
        if (newEdge == null) {
            return null;
        }
        //prepare Edge info
        newEdge.isEpsilon = false;
        newEdge.id = node.id;
        newEdge.value = node.value;
        newEdge.dstState = to;
        newEdge.srcState = from;
        //inser to fromState
        from.lEdge.addLast(newEdge);
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
        this.lEdge.addAll(startState.lEdge);
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

    /**
     *
     * @param filepath
     *
     *
     * NFAroot:stateID
    ---
    stateID0
    onEpsilon:	next_state_ID1	next_state_ID2 ....
    onChar:number	next_state_ID1	next_state_ID2 ....
    is_final:1/0
    ---
    stateID1
    onEpsilon:	next_state_ID1	next_state_ID2 ....
    onChar:number	next_state_ID1	next_state_ID2 ....
    is_final:1/0
    ---
    .....................................................
    ---
    stateIDn
    onEpsilon:	next_state_ID1	next_state_ID2 ....
    onChar:number	next_state_ID1	next_state_ID2 ....
    is_final:1/0
    ---
    endNFA
     */
    /*
    public void print2file(String filepath) {
    File f = new File(filepath);
    try {
    BufferedWriter bw = new BufferedWriter(new FileWriter(f));

    bw.write("NFAroot: 0\n");
    NFAState p = this.start;
    while (p != null) {
    bw.write(p.order + "\n");
    bw.write("onEpsilon:");
    NFAEdge q = p.edge;
    while (q != null) {
    if (q.value.compareToIgnoreCase("null") == 0) {
    bw.write(" " + q.dest.order);
    }
    q = q.nextEdge;
    }


    //Mo hinh nfa nay chi co duy nha mot nextsate for ...
    q = p.edge;
    while (q != null) {
    q.convert2Array();
    if (q.value.compareToIgnoreCase("null") != 0) {
    for (int i = 0; i < 256; i++) {
    if (q.onChar[i]) {
    bw.write("\nonChar:" + i + " " + q.dest.order);
    }
    }
    }
    q = q.nextEdge;
    }

    if (p.isFinal) {
    bw.write("\nis_final: 1");
    } else {
    bw.write("\nis_final: 0");
    }
    p = p.nextState;
    bw.write("\n");
    }

    bw.flush();
    bw.close();

    } catch (IOException ex) {
    Logger.getLogger(NFA.class.getName()).log(Level.SEVERE, null, ex);
    }
    }


    public int getSize() {
    this.updateID();
    NFAState p = start;
    while (p.nextState != null) {
    p = p.nextState;
    }
    return p.order + 1;
    }

    public void print() {
    //this.updateID();
    this.print(null);
    }

    public void print(Document doc) {
    //this.updateID();

    NFAState p = this.start;
    while (p != null) {
    PCRE.Refer.print(p.order + ":", doc);
    NFAEdge q = p.edge;
    while (q != null) {
    PCRE.Refer.print("[" + q.dest.order + "," + q.value + "," + Refer.convert[q.code_id] + "]->", doc);
    q = q.nextEdge;
    }
    if (p.isFinal) {
    PCRE.Refer.println("Is Final", doc);
    }
    p = p.nextState;
    PCRE.Refer.print("\n", doc);
    }
    }
     */
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
        for(int i =0; i<this.lState.size(); i++){
            this.lState.get(i).order = i;
        }
    }

    /**
     * This is important function,  nfa is needed to deleteRedundant before
     * further processing.
     *
     * State with all input and output edge is epsilon will be eliminated.
     */
    public void reduceRedundantState() {
        LinkedList<NFAState> lreduce = new LinkedList<NFAState>();
        for (int i = 0; i < this.lState.size(); i++) {
            NFAState state = this.lState.get(i);
            //System.out.println("State " + i);
            if (lreduce.indexOf(state) != -1) {
                continue;
            }
            for (int j = 0; j < state.lEdge.size();) {
                NFAEdge ed = state.lEdge.get(j);
                if (ed.isEpsilon && !ed.dstState.isFinal && ed.dstState.getnonEpsilonEdge() == null) {
                    //insert this state to reduce list
                    lreduce.add(ed.dstState);
                    //delete this edge
                    state.lEdge.remove(ed);
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
     *  Build operator "|"
     * @param nfa1
     * @param nfa2
     * @return
     */
    public NFA buildUnion(NFA nfa1, NFA nfa2) {
        NFA ret = new NFA();
        //create start state and end state for new NFA
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        //set it
        ret.start = sStart;
        ret.end = sExit;
        //add it to lState
        ret.lState.add(sStart);
        ret.lState.add(sExit);
        //add nfa1 and nfa2 to NFA
        ret.lState.addAll(nfa1.lState);
        ret.lState.addAll(nfa2.lState);
        ret.lEdge.addAll(nfa1.lEdge);
        ret.lEdge.addAll(nfa2.lEdge);
        // modifier start and end state each nfa
        nfa1.start.isStart = false;
        nfa1.end.isFinal = false;
        nfa2.start.isStart = false;
        nfa2.end.isFinal = false;
        // create edge from new Start and end state to old nfa
        ret.insertEdgeEpsilon(sStart, nfa1.start);
        ret.insertEdgeEpsilon(sStart, nfa2.start);
        ret.insertEdgeEpsilon(nfa1.end, sExit);
        ret.insertEdgeEpsilon(nfa2.end, sExit);
        // return
        return ret;
    }

    public NFA buildConcat(NFA nfa1, NFA nfa2) {
        NFA ret = new NFA();
        //set start, end state
        ret.start = nfa1.start;
        nfa1.end.isFinal = false;
        ret.end = nfa2.end;
        nfa2.start.isStart = false;
        //insert list of state and edge to nfa
        ret.lEdge.addAll(nfa1.lEdge);
        ret.lState.addAll(nfa1.lState);
        ret.lEdge.addAll(nfa2.lEdge);
        ret.lState.addAll(nfa2.lState);
        //insert edget between it.
        ret.insertEdgeEpsilon(nfa1.end, nfa2.start);
        // return
        return ret;
    }

    public NFA buildStart(NFA nfa) {

        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;

        nfa.insertEdgeEpsilon(nfa.end, nfa.start);
        nfa.insertStartState(sStart);
        nfa.insertEndState(sExit);
        nfa.insertEdgeEpsilon(nfa.start, nfa.end);

        return nfa;
    }

    //one or more
    public NFA buildPlus(NFA nfa) {
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        nfa.insertEdgeEpsilon(nfa.end, nfa.start);
        nfa.insertStartState(sStart);
        nfa.insertEndState(sExit);
        

        return nfa;
    }

    //zero or one
    public NFA buildQuestion(NFA nfa) {
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;

        nfa.insertStartState(sStart);
        nfa.insertEndState(sExit);
        nfa.insertEdgeEpsilon(nfa.start, nfa.end);

        return nfa;
    }

    public NFA buildChar(Node c) {

        NFA nfa = new NFA();
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        // insert start and exit state to nfa
        nfa.start = sStart;
        nfa.end = sExit;
        nfa.lState.add(sStart);
        nfa.lState.add(sExit);
        //insert edge
        nfa.insertEdge(sStart, sExit, c);
        return nfa;
    }

    public NFA buildContraint(Node c) {
        NFA nfa = new NFA();
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        // insert start and exit state to nfa
        nfa.start = sStart;
        nfa.end = sExit;
        nfa.lState.add(sStart);
        nfa.lState.add(sExit);
        //process ... todo
        Node temp = Refer.processContraint(c);
        //create edge;
        nfa.insertEdge(sStart, sExit, temp).isConRep = true;
        return nfa;
    }

    public void buildNFA(ParseTree tree) {
        NFA temp = this.tree2NFA(tree);
        //copy to this
        //todo : need start and end state to cover return nfa.
        this.tree = temp.tree;
        this.lState = temp.lState;
        this.lEdge = temp.lEdge;
        this.start = temp.start;
        this.end = temp.end;
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        this.insertStartState(sStart);
        this.insertEndState(sExit);
    }

    public NFA tree2NFA(ParseTree tree) {
        Node root = tree.root;
        PCRE.Refer.println("Begin to convert tree 2 NFA :");
        NFA ret = this.buildNFA(root);
        ret.tree = tree;
        return ret;
        /*
        //Create it own start and end state
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        NFAState sExit = new NFAState();
        sExit.isFinal = true;
        // create NFA from root node


        ret.insertStartState(sStart);
        ret.insertEndState(sExit);
        ret.insertEdgeEpsilon(ret.start, tempStart);
        ret.insertEdgeEpsilon(tempExit, ret.end);
        tempExit.isFinal = false;
        ret.end.isFinal = true;
        this.start = ret.start;
        this.end = ret.end;
         * 
         */

    }

    public NFA buildNFA(Node r) {
        if (r.id == Refer._op_star) {
            return this.buildStart(buildNFA(r.left));
        } else if (r.id == Refer._op_plus) {
            return this.buildPlus(buildNFA(r.left));
        } else if (r.id == Refer._op_ques) {
            return this.buildQuestion(buildNFA(r.left));
        } else if (r.id == Refer._op_or) {
            return this.buildUnion(buildNFA(r.left), buildNFA(r.right));
        } else if (r.id == Refer._op_and) {
            return this.buildConcat(buildNFA(r.left), buildNFA(r.right));
        } else if (r.id == Refer._op_constraint) {
            return this.buildContraint(r);
        } else {
            return this.buildChar(r);
        }
    }

    private void combineto(NFAState state, NFAState dst) {
        for (int i = 0; i < dst.lEdge.size(); i++) {
            NFAEdge etemp = dst.lEdge.get(i);
            this.insertEdgeEpsilon(state, etemp.dstState);
        }
    }

    private void reduceState(NFAState state) {
        for (int i = 0; i < state.lEdge.size(); i++) {
            this.lEdge.remove(state.lEdge.get(i));
        }
        this.lState.remove(state);
    }

    public PcreRule getRule() {
        if(this.tree != null)
            return this.tree.rule;
        return null;
    }
}


