/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NFAold;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import ParseTree.Node;
import ParseTree.ParseTree;
import PCRE.PcreRule;
import PCRE.Refer;



/**
 *
 * @author Hoang Long Le
 */
public class NFA {
    
    public NFAState start = null;
    public NFAState exit = null;
    //public String modifier;
    public PcreRule rule = null;
    public static String _default_folder = System.getProperty("user.dir") + System.getProperty("file.separator");
    public static String _default_file_name = "nfa.dot";
    //public int count = 0;

    
    public NFA ()
    {
        this.start = null;
        this.exit = null;
    }

    
    public NFA(NFAState start) {
        this.start = start;
    }

    public NFA(NFAState start, NFAState exit) {
        this.start = start;
        this.exit = exit;
    }

    public void insertEdgeEpsilon(NFAState from, NFAState to) {

    	//Ham insert canh vao nfastate frome, cahj epsilon nam dau tien, canh char nam cuoi cung
        NFAEdge newEdge = new NFAEdge(this.rule);
        if (newEdge == null) {
            return;
        }
        newEdge.isEpsilon = true;
        newEdge.dest = to;
        if (from.edge == null)//insert  first edge
        {
            from.edge = newEdge;
            newEdge.nextEdge = null;
        } else {
            NFAEdge edgePre = null;
            NFAEdge edgeWalk = from.edge;
            while (edgeWalk != null && edgeWalk.isEpsilon) {
                edgePre = edgeWalk;
                edgeWalk = edgeWalk.nextEdge;
            }

            if (edgePre == null) // insert before first arc
            {
                from.edge = newEdge;
            } else {
                edgePre.nextEdge = newEdge;
            }
            newEdge.nextEdge = edgeWalk;
        }
    }

        public void insertEdge(NFAState from, NFAState to, Node s) {

    	//Ham insert canh vao nfastate frome, cahj epsilon nam dau tien, canh char nam cuoi cung

        NFAEdge newEdge = new NFAEdge(this.rule);
        if (newEdge == null) {
            return;
        }
        newEdge.isEpsilon = false;
        newEdge.code_id = s.id;
        newEdge.value = s.value;

        newEdge.dest = to;
        if (from.edge == null)//insert  first edge
        {
            from.edge = newEdge;
            newEdge.nextEdge = null;
        } else {
            NFAEdge edgePre = null;
            NFAEdge edgeWalk = from.edge;
            while (edgeWalk != null && edgeWalk.isEpsilon) {
                edgePre = edgeWalk;
                edgeWalk = edgeWalk.nextEdge;
            }

            if (edgePre == null) // insert before first arc
            {
                from.edge = newEdge;
            } else {
                edgePre.nextEdge = newEdge;
            }
            newEdge.nextEdge = edgeWalk;
        }
    }

    public void insertStartState(NFAState startState) {
        NFAState temp = this.start;
        this.start = startState;
        this.start.nextState = temp;
    }

    public void insertExitState(NFAState exitState) {
        NFAState Pre = null;
        NFAState Loc = this.start;
        while (Loc != null) {
            Pre = Loc;
            Loc = Loc.nextState;
        }
        Pre.nextState = exitState;
        this.exit = exitState;
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
    public void print2file(String filepath){
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
                    if(q.value.compareToIgnoreCase("null") == 0)
                        bw.write(" " + q.dest.order);
                    q = q.nextEdge;
                }


                //Mo hinh nfa nay chi co duy nha mot nextsate for ...
                q = p.edge;
                while (q != null) {
                    q.convert2Array();
                    if(q.value.compareToIgnoreCase("null") != 0)
                        for(int i=0; i<256; i++){
                            if(q.onChar[i])
                                bw.write("\nonChar:" + i + " " + q.dest.order);
                        }
                    q = q.nextEdge;
                }

                if(p.isFinal)
                    bw.write("\nis_final: 1");
                else
                    bw.write("\nis_final: 0");
                p = p.nextState;
                bw.write("\n");
            }

            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(NFA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getSize(){
        this.updateID();
        NFAState p = start;
        while (p.nextState != null) {
            p = p.nextState;
        }
        return p.order +1;
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
            if(p.isFinal){
                PCRE.Refer.println("Is Final", doc);
            }
            p = p.nextState;
            PCRE.Refer.print("\n", doc);
        }
    }

    public void generateDotFile(String name, String folder){
        BufferedWriter bw = null;
        try {
            if (null == folder || folder.isEmpty()) {
                folder = _default_folder;
            }
            if (null == name || name.isEmpty()) {
                name = _default_file_name;
            }
            bw = new BufferedWriter(new FileWriter(folder + name));
            bw.write("digraph \"nfa path\" {" +
                    "\ngraph [ranksep=.2,rankdir=LR];" +
                    "\nnode [shape=circle,fontname=Arial,fontsize=14];" +
                    "\nnode [width=1,fixedsize=true];" +
                    "\nedge [fontname=Arial,fontsize=14];" +
                    "\n-1 [width=0.2,shape=point color=red];" +
                    "\n-1 -> 0 [ color=red];");
            NFAState p = this.start;
            while (p != null){
                if(p.isFinal)
                    bw.write("\n" + p.order + " [label=q" + p.order + " color=red];");
                else
                    bw.write("\n" + p.order + " [label=q" + p.order + " color=green];");
                p = p.nextState;
            }
            p = this.start;
            String color ="";
            while (p != null){
                NFAEdge q = p.edge;
                while (q != null){
                    if (q.isEpsilon){
                        color = "red";
                    }else
                        color = "black";
                    bw.write("\n" + p.order + " -> " + q.dest.order + "  [label=\"[" + q.value + "]\" color=" + color + "];");
                    q = q.nextEdge;
                }
                p = p.nextState;
            }
            bw.write("\n}\n");
            bw.flush();
            bw.close();
        } catch (IOException ex){
            System.err.println(ex);
        }
    }
     
    public void updateID() {
        int order = 0;
        NFAState p = start;
        while (p != null) {
            p.order = order;
            if (p.nextState == null) {
                p.isFinal = true;
            }
            p = p.nextState;
            order++;
        }
    }

    public void deleteRedundantState() {
        NFAState pWalk = this.start;
        while (pWalk != null) { //Duyet tung State mot
            NFAEdge qEdge = pWalk.edge;
            while (qEdge != null) { //Duyet tung canh cuar state do
                if (qEdge.isEpsilon) // input to state = ""
                {
					// check all output of this state
                    NFAEdge walk = qEdge.dest.edge;
                    while (walk != null && walk.isEpsilon) {
						walk = walk.nextEdge;
                    }
                    if (walk == null && qEdge.dest.isFinal == false) // need to remove due to all output = ""
                    {
                        //traversal from start
                        NFAState state = this.start;
                        NFAState preState = null;
                        while (state != null) {  //Duyet toan bo state bat dau tu start
                            if (state == qEdge.dest) // found out that redundant state
							{
                                preState.nextState = state.nextState; // remove redundant State qEdge.dest
                            }
							//now reroute all edge
                            NFAEdge preEdge = null;
                            NFAEdge curEdge = state.edge;
                            while (curEdge != null) { //quet tat ca cac canh cua state
                                if (curEdge.dest == qEdge.dest) { // trang thai nay co cac canh den trang thai da xoa bo
                                    if (curEdge.dest.edge != null) {
                                        if (qEdge.dest.edge.dest != null) {// xet all cac canh ra cua trang thai da xoa bo
                                            NFAEdge f = qEdge.dest.edge;
                                            while (f != null) // them cac canh vao
                                            {
                                                this.insertEdgeEpsilon(state, f.dest);
                                                f = f.nextEdge;
                                            }
                                        }
                                        if (preEdge == null) {
                                            state.edge = curEdge.nextEdge;//bo di canh dau tien, von dan toi mot state da bi xoa
                                        } else {
                                            preEdge.nextEdge = curEdge.nextEdge;
                                        }
                                    }
                                } else {
                                    preEdge = curEdge;
                                }
                                curEdge = curEdge.nextEdge;
                            }
                            preState = state;
                            state = state.nextState;
                        }
                    }
                }
                qEdge = qEdge.nextEdge;
            }
            pWalk = pWalk.nextState;
        }

    }

       public NFA buildUnion (NFA nfa1, NFA nfa2)
    {
        NFAState sStart = new NFAState ();
        NFAState sExit = new NFAState();
        NFAState tempStart1 = nfa1.start;
        NFAState tempExit1 = nfa1.exit;
        NFAState tempStart2 = nfa2.start;
        NFAState tempExit2 = nfa2.exit;
        nfa1.exit.isFinal = false;
        nfa2.exit.isFinal = false;
        sExit.isFinal = true;
        nfa1.insertStartState(sStart);
        nfa1.insertExitState(nfa2.start); //append list state of nfa2 into nfa1
        nfa1.insertEdgeEpsilon(nfa1.start, tempStart1);
        nfa1.insertEdgeEpsilon(nfa1.start, tempStart2);
        nfa1.insertExitState(sExit);
        nfa1.insertEdgeEpsilon(tempExit1, nfa1.exit);
        nfa1.insertEdgeEpsilon(tempExit2, nfa1.exit);
        return nfa1;
    }

    public NFA buildConcat (NFA nfa1, NFA nfa2)
    {
        nfa1.exit.isFinal = false;
        nfa2.exit.isFinal = true;
        NFAState temp = nfa1.exit;
        nfa1.insertExitState(nfa2.start);
        nfa1.insertEdgeEpsilon(temp, nfa2.start);
        nfa1.exit = nfa2.exit;
        return nfa1;
    }

    public NFA buildStart (NFA nfa)
    {
        NFAState sStart = new NFAState();
        NFAState sExit = new NFAState();
        nfa.exit.isFinal = false;
        NFAState tempStart = nfa.start;
        NFAState tempExit = nfa.exit;
        nfa.insertEdgeEpsilon(nfa.exit, nfa.start);
        nfa.insertStartState(sStart);
        nfa.insertExitState(sExit);
        nfa.insertEdgeEpsilon(tempExit, nfa.exit);
        nfa.insertEdgeEpsilon(nfa.start, tempStart);
        nfa.insertEdgeEpsilon(nfa.start, nfa.exit);
        nfa.exit.isFinal = true;
        return nfa;
    }

    //one or more
    public NFA buildPlus (NFA nfa)
    {
        NFAState sStart = new NFAState();
        NFAState sExit = new NFAState();
        nfa.exit.isFinal = false;
        NFAState tempStart = nfa.start;
        NFAState tempExit = nfa.exit;
        nfa.insertEdgeEpsilon(nfa.exit, nfa.start);
        nfa.insertStartState(sStart);
        nfa.insertExitState(sExit);
        nfa.insertEdgeEpsilon(tempExit, nfa.exit);
        nfa.insertEdgeEpsilon(nfa.start, tempStart);
        //nfa.insertEdge(nfa.start, nfa.exit, "");
        nfa.exit.isFinal = true;
        return nfa;
    }

    //zero or one
    public NFA buildQuestion (NFA nfa)
    {
        NFAState sStart = new NFAState();
        NFAState sExit = new NFAState();
        nfa.exit.isFinal = false;
        NFAState tempStart = nfa.start;
        NFAState tempExit = nfa.exit;
        //nfa.insertEdge(nfa.exit, nfa.start, "");
        nfa.insertStartState(sStart);
        nfa.insertExitState(sExit);
        nfa.insertEdgeEpsilon(tempExit, nfa.exit);
        nfa.insertEdgeEpsilon(nfa.start, tempStart);
        nfa.insertEdgeEpsilon(nfa.start, nfa.exit);
        nfa.exit.isFinal = true;
        return nfa;
    }

    public NFA buildChar (Node c)
    {
        NFAState sStart = new NFAState();
        NFAState sExit = new NFAState();
        NFA nfa = new NFA(sStart);
        nfa.insertExitState(sExit);
        nfa.insertEdge(sStart, sExit, c);
        return nfa;
    }

    public NFA buildContraint (Node c){
        NFAState sStart = new NFAState();
        NFAState sExit = new NFAState();
        NFA nfa = new NFA(sStart);

        Node temp = Refer.processContraint(c);
        

        nfa.insertExitState(sExit);
        nfa.insertEdge(sStart, sExit, temp);
        return nfa;

    }

    public void tree2NFA (ParseTree tree)
    {
        Node root = tree.root;
        this.rule = tree.rule;

        PCRE.Refer.println("Begin to convert tree 2 NFA :");

        NFAState sStart = new NFAState();
    	NFAState sExit = new NFAState();
    	NFA ret = this.builtNFA(root);
    	NFAState tempStart = ret.start;
    	NFAState tempExit = ret.exit;
    	ret.insertStartState(sStart);
    	ret.insertExitState(sExit);
    	ret.insertEdgeEpsilon(ret.start, tempStart);
    	ret.insertEdgeEpsilon(tempExit, ret.exit);
    	tempExit.isFinal=false;
    	ret.exit.isFinal=true;
        this.start = ret.start;
        this.exit  = ret.exit;

        //insert rule for every NFAEdge.
        NFAState p = this.start;
        while (p != null) {
            NFAEdge q = p.edge;
            while (q != null) {
                q.rule = this.rule;
                q = q.nextEdge;
            }
            p = p.nextState;
        }//*/
    }



    public NFA builtNFA (Node r)
    {
        if (r.id == Refer._op_star)
            return this.buildStart(builtNFA (r.left));
        else if (r.id == Refer._op_plus)
            return this.buildPlus(builtNFA (r.left));
        else if (r.id == Refer._op_ques)
            return this.buildQuestion(builtNFA (r.left));
        else if (r.id == Refer._op_or)
            return this.buildUnion(builtNFA (r.left) , builtNFA (r.right));
        else if (r.id == Refer._op_and)
            return this.buildConcat(builtNFA (r.left) , builtNFA (r.right));
        else if (r.id == Refer._op_constraint)
            return this.buildContraint(r);
        else
            return this.buildChar(r);
    }

}


