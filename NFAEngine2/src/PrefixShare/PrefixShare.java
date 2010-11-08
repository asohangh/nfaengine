/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PrefixShare;

import NFA.NFA;
import NFA.NFAState;
import ParseTree.ParseTree;
import RegexEngine.ReEngine;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class PrefixShare {

    public String prepcre;
    public NFA prenfa;
    public NFA combineNFA;
    public LinkedList<String> lsubpcre;
    public LinkedList<NFA>lsubnfa;
    public ReEngine engine;

    public PrefixShare(){
       this.lsubpcre = new LinkedList<String>();
    }
    public PrefixShare(String prepcre, LinkedList<String> lsubpcre){
        this.prepcre = prepcre;
        this.lsubpcre = lsubpcre;
    }

    public void addPrefixPcre(String pcre){
        this.prepcre = pcre;
    }
    public void addSubfixPcre(String pcre){
        this.lsubpcre.add(pcre);
    }
    public ReEngine buildEngine(){
        this.buildPrefixNFA();
        this.buildPrefixEngine();
        return this.engine;
    }
    public void buildPrefixNFA(){
        //build prefix engine;
        ParseTree tree = new ParseTree(this.prepcre);
        this.prenfa = new NFA();
        this.prenfa.buildNFA(tree);
        //this.prenfa.reduceRedundantState();
        //build subfix engine
        this.lsubnfa = new LinkedList<NFA>();
        for(int i = 0; i<this.lsubpcre.size(); i++){
            ParseTree subtree = new ParseTree(this.lsubpcre.get(i));
            NFA tnfa = new NFA();
            tnfa.buildNFA(subtree);
            this.lsubnfa.add(tnfa);
        }
        // combine prefix with subfix nfa.
        this.combineNFA();
        this.combineNFA.tree = tree;
        this.combineNFA.reduceRedundantState();
        //this.combineNFA.generateDotFile("nfa_conmbine.dot", null);
        
    }

    public void buildPrefixEngine(){
        //Create ReGexEngien
        this.engine = new ReEngine();
        engine.buildEngine(this.combineNFA);
        engine.reduceBlockChar();
        engine.printBlockChar();
    }

    /**
     * this function will combinate prefix nfa and subfix nfa.
     */
    private void combineNFA() {
        //union all subfix
        NFA tnfa = this.lsubnfa.get(0);
        for(int i =1; i<this.lsubnfa.size(); i++){
            tnfa = this.unionNFA(tnfa,this.lsubnfa.get(i));
        }
        //combine with  prefix.
        this.combineNFA = this.concatNFA(this.prenfa, tnfa);
    }

    /**
     *  Build operator "|"
     * @param nfa1
     * @param nfa2
     * @return
     */
    public NFA unionNFA(NFA nfa1, NFA nfa2) {
        NFA ret = new NFA();
        //create start state and end state for new NFA
        NFAState sStart = new NFAState();
        sStart.isStart = true;
        //NFAState sExit = new NFAState();
        //sExit.isFinal = true;
        //set it
        ret.start = sStart;
        ret.end = null;
        //add it to lState
        ret.lState.add(sStart);
        //ret.lState.add(sExit);
        //add nfa1 and nfa2 to NFA
        ret.lState.addAll(nfa1.lState);
        ret.lState.addAll(nfa2.lState);
        ret.lEdge.addAll(nfa1.lEdge);
        ret.lEdge.addAll(nfa2.lEdge);
        // modifier start and end state each nfa
        nfa1.start.isStart = false;
        //nfa1.end.isFinal = false;
        nfa2.start.isStart = false;
        //nfa2.end.isFinal = false;
        // create edge from new Start and end state to old nfa
        ret.insertEdgeEpsilon(sStart, nfa1.start);
        ret.insertEdgeEpsilon(sStart, nfa2.start);
        //ret.insertEdgeEpsilon(nfa1.end, sExit);
        //ret.insertEdgeEpsilon(nfa2.end, sExit);
        // return
        return ret;
    }

    
    /**
     *
     * @param nfa1
     * @param nfa2
     * @return
     */
     public NFA concatNFA(NFA nfa1, NFA nfa2) {
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

    public void addSubfixPcre(LinkedList<String> subfix) {
        for(int i =0; i< subfix.size(); i++){
            this.lsubpcre.add(subfix.get(i));
        }
    }
}
