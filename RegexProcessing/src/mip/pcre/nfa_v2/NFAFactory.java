package mip.pcre.nfa_v2;


import java.util.LinkedList;


import mip.pcre.pcre_v2.*;
import mip.pcre.parsetree_v2.*;


public class NFAFactory {
	    //Parse tree it is costructed from;
	    public RegexTree tree = null;
	    // other information
	    public static String _default_folder = System.getProperty("user.dir") + System.getProperty("file.separator");
	    public static String _default_file_name = "nfa.dot";

	 
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
	    
	    /**
	     * Build Concatenation operator
	     * @param nfa1
	     * @param nfa2
	     * @return
	     */
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
	    /**
	     * Build Star Operator
	     * @param nfa
	     * @return
	     */
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

	    /**
	     * build Plust opeartor
	     * @param nfa
	     * @return
	     */
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

	    /**
	     * build question operator
	     * @param nfa
	     * @return
	     */
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

	    /**
	     * Build char and char class
	     * @param c
	     * @return
	     */
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

	    /**
	     * Build Constraint block
	     * @param c
	     * @return
	     */
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
	        nfa.insertEdge(sStart, sExit, temp);
	        return nfa;
	    }

	   
	    /**
	     *  build Nfa from regexTree
	     * @param tree
	     * @return
	     */
	    public NFA build(RegexTree tree) {
	        Node root = tree.root;
	        PCRE.Refer.println("NFAFactory.java: Begin to convert tree 2 NFA :");
	        this.tree = tree;
	        NFA ret = this.buildNFA(root);
	        ret.setRegexTree(tree);
	        ret.updateModifier();
	        return ret;
	    }
	    /**
	     *  building single NFA from list of PCRE.
	     * @param lpcre
	     */
	    public NFA buildNFA(LinkedList<String> lpcre) {
	        LinkedList<NFA> lnfa = new LinkedList<NFA>();
	        //create list of nfa
	        for (int i = 0; i < lpcre.size(); i++) {
	            String pcre = lpcre.get(i);
	            RegexTree ps = new RegexTreeBuilder().build(pcre);
	            
	            NFA nfa = this.build(ps);
	            //reduce nfa
	            nfa.reduceRedundantState();
	            lnfa.add(nfa);
	        }
	        System.out.println("buildNFA(LinkedList<String> lpcre)");
	        System.out.println("Finish create seperate nfa");
	        //create new NFA
	        NFA nfa = new NFA();
	        nfa.createStartState();
	       // link all nfa to thsi nfa.
	        for (int i =0; i<lnfa.size(); i++){
	        	NFA tmpnfa = lnfa.get(i);
	        	nfa.insertNFA(tmpnfa);
	        }
	        System.out.println("Finish create whole Nfa");
	        // reduce new Nfa
	        //nfa.reduceRedundantState();
	        //System.out.println("Finish reduce Nfa");
	        return nfa;
	    }

	    public NFA buildNFA(Node r) {
	        NFA ret;
	        switch (r.id) {
	            case Refer._op_star:
	                ret = this.buildStart(buildNFA(r.left));
	                break;
	            case Refer._op_plus:
	                ret = this.buildPlus(buildNFA(r.left));
	                break;
	            case Refer._op_ques:
	                ret = this.buildQuestion(buildNFA(r.left));
	                break;
	            case Refer._op_or:
	                ret = this.buildUnion(buildNFA(r.left), buildNFA(r.right));
	                break;
	            case Refer._op_and:
	                ret = this.buildConcat(buildNFA(r.left), buildNFA(r.right));
	                break;
	            case Refer._op_constraint:
	                ret = this.buildContraint(r);
	                break;
	            default:
	                ret = this.buildChar(r);
	        }
	        return ret;
	    }

	    public PCREPattern getRule() {
	        if (this.tree != null) {
	            return this.tree.rule;
	        }
	        return null;
	    }
}
