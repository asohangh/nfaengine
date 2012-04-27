/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dfa;

import java.util.LinkedList;

import nfa.NFA;
import nfa.NFAEdge;
import nfa.NFAState;

/**
 *
 * @author heckarim
 */
public class DFA {

    LinkedList<DFAState> lstState = new LinkedList<DFAState>();


    /**
     *  construct dfa from nfa
     * @param nfa
     */
    public void constructDFA(NFA nfa) {
        // convert all accept char to 256 bit format.
        nfa.convertTo256();
        // create first DFA State.
        // create listNFA
        LinkedList<NFAState> lstNFA = new LinkedList<NFAState>();
        NFAState nfastate = nfa.start;
        lstNFA.add(nfastate);
        // if nfastate have epsilon edge to others, add all.
        this.addlistNFAtolistNFA(lstNFA, nfastate.getEpsilonState());

        DFAState dfastate = new DFAState(lstNFA);
        this.lstState.add(dfastate);
        // begin algorithm to convert dfa to nfa.
        for (int i = 0; i < this.lstState.size(); i++) {
            dfastate = this.lstState.get(i);
            //check edge of this state
            for (int j = 0; j < 256; j++) {
                LinkedList<NFAState> lnfa = new LinkedList<NFAState>();
                //check all nfastate in this dfastate
                for (int k = 0; k < dfastate.lstNFA.size(); k++) {
                    nfastate = dfastate.lstNFA.get(k);
                    //scan all edges of this state.
                    LinkedList<NFAEdge> lnfaedge = nfastate.getallNonEpsilonEdge();
                    for (int l = 0; l < lnfaedge.size(); l++) {
                        //System.out.println(" posiiton " + j +  " " + lnfaedge.get(l).value);
                        if (lnfaedge.get(l).value256[j]) { //have valid bit at this char.
                            //System.out.println(" posiiton " + j +  " " + lnfaedge.get(l).value);
                            //add dest state fo this edge to list nfa
                            NFAState nstate = lnfaedge.get(l).dstState;
                            lnfa.add(nstate);
                            // if nstate have epsilon edge to others, add all.
                            this.addlistNFAtolistNFA(lnfa, nstate.getEpsilonState());
                        }
                    }
                }
                //check if this lnfa list is already identify another dfastate.
                DFAState tempdfastate = this.getDFAState(lnfa);
                if (tempdfastate == null) {
                    tempdfastate = new DFAState(lnfa);
                    //System.out.println("llll vsdjflkdsf");
                    this.lstState.add(tempdfastate);
                }
                dfastate.edge.nextState[j] = tempdfastate;
            }
        }
        // finish.
    }

    /**
     * just for test characterset:  abcd
     */
    public void printTest() {
        for (int i = 0; i < this.lstState.size(); i++) {
            this.lstState.get(i).order = i;
        }
        for (int i = 0; i < this.lstState.size(); i++) {
            DFAState state = this.lstState.get(i);
            if (state.isfinal) {
                System.out.println("StateMatch " + state.order);
            } else {
                System.out.println("State " + state.order);
            }
            char chr = 'a';
            for (chr = 'a'; chr < 'e'; chr++) {
                System.out.println("    " + chr + " : " + state.edge.nextState[chr].order);
            }
        }
    }

    /**
     * this function is for guranteing that non redundant nfastate in list
     * @param lstNFA
     * @param epsilonState
     */
    private void addlistNFAtolistNFA(LinkedList<NFAState> lstNFA, LinkedList<NFAState> tempListNFA) {
        for (int i = 0; i < tempListNFA.size(); i++) {
            if (lstNFA.indexOf(tempListNFA.get(i)) == -1) {
                lstNFA.add(tempListNFA.get(i));
            }
        }
    }

    private DFAState getDFAState(LinkedList<NFAState> lnfa) {
        for (int i = 0; i < this.lstState.size(); i++) {
            DFAState dfa = this.lstState.get(i);
            if (dfa.matchListNFA(lnfa)) {
                return dfa;
            }
        }
        return null;
    }
}
