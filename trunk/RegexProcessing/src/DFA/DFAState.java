/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DFA;

import NFA.NFAEdge;
import NFA.NFAState;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class DFAState {
    public LinkedList<NFAState> lstNFA;
    public DFAEdge edge;
    public int order;
    public boolean isfinal;

    DFAState(LinkedList<NFAState> lstNFA) {
        this.lstNFA = lstNFA;
        this.edge = new DFAEdge();
        //check if is final
        if(this.checkFinal())
            this.isfinal = true;
    }

    /**
     *
     * @param state
     * @return
     *      1: successful
     *      0: redundant.
     */
    public int addNFAState(NFAState state){
        if(lstNFA.indexOf(state)==-1){
            lstNFA.addLast(state);
            return 1;
        }
        return 0;
    }

    /**
     * check if State is already built up
     * @param list
     * @return
     */
    public boolean matchListNFA(LinkedList<NFAState> list){
        for(int i =0; i<list.size(); i ++){
            if(this.lstNFA.indexOf(list.get(i)) == -1)
                return false;
        }
        return true;
    }

    private boolean checkFinal() {
        for(int i =0; i<this.lstNFA.size(); i++){
            if(lstNFA.get(i).isFinal)
                return true;
        }
        return false;
    }
}
