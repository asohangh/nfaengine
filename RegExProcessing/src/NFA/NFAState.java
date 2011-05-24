package NFA;

import java.util.LinkedList;

/**
 *
 * @author Jeffrey W Roberts
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

}

