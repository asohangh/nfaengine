package NFA;

import java.util.LinkedList;

/**
 *
 * @author Jeffrey W Roberts & Heckarim
 */
public class NFAState {
    // List of Edges keep its next State.
    LinkedList<NFAEdge> lEdge;
    // need to define Final State, and Start State
    public boolean isFinal = false;
    public boolean isStart = false;
    // need to identify NFAState with orther State in NFAList.
    public int order; // this attribute will be updated in updateList on NFA Class.

    public NFAState(){
        lEdge = new LinkedList<NFAEdge>();
    }

    public NFAEdge getnonEpsilonEdge(){
        NFAEdge ret = null;
        for(int i =0; i<this.lEdge.size(); i++){
            if(!lEdge.get(i).isEpsilon)
                return lEdge.get(i);
        }
        return ret;
    }

    public LinkedList<NFAEdge> getallNonEpsilonEdge(){
        LinkedList<NFAEdge> ledge = new LinkedList<NFAEdge>();
        for(int i =0; i<this.lEdge.size(); i++){
            if(!lEdge.get(i).isEpsilon)
                ledge.add(lEdge.get(i));
        }
        return ledge;
    }

    /**
     *  get all nfastate having epsilon edge from this state.
     * @return
     */
    public LinkedList<NFAState> getEpsilonState(){
        LinkedList<NFAState> lstate = new LinkedList<NFAState>();
        for(int i =0; i<this.lEdge.size(); i++){
            if(lEdge.get(i).isEpsilon){
                lstate.addLast(lEdge.get(i).dstState);
            }
        }
        return lstate;
    }

}

