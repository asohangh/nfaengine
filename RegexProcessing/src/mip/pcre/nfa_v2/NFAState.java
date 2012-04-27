package mip.pcre.nfa_v2;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Jeffrey W Roberts & Heckarim
 */

public class NFAState {
    // List of Edges keep its next State.
    private LinkedList<NFAEdge> lGoingEdge;
    // List of Edges keep its Comming State
    private LinkedList<NFAEdge> lComingEdge;
    // need to define Final State, and Start State
    public boolean isFinal = false;
    public boolean isStart = false;
    public int status = 0; //for prefix sharing status
    // need to identify NFAState with orther State in NFAList.
    public int order; // this attribute will be updated in updateList on NFA Class.

    public NFAState(){
        lGoingEdge = new LinkedList<NFAEdge>();
        lComingEdge = new LinkedList<NFAEdge>();
    }

    //Get non epsilon edge from going edge list
    public NFAEdge getnonEpsilonEdge(){
        NFAEdge ret = null;
        for(int i =0; i<this.lGoingEdge.size(); i++){
            if(!lGoingEdge.get(i).isEpsilon)
                return lGoingEdge.get(i);
        }
        return ret;
    }
    //Get all non epsilon edge from going edge list
    public LinkedList<NFAEdge> getallNonEpsilonEdge(){
        LinkedList<NFAEdge> ledge = new LinkedList<NFAEdge>();
        for(int i =0; i<this.lGoingEdge.size(); i++){
            if(!lGoingEdge.get(i).isEpsilon)
                ledge.add(lGoingEdge.get(i));
        }
        return ledge;
    }

    /**
     *  get all nfastate having epsilon edge from this state.
     * @return
     */
    public LinkedList<NFAState> getEpsilonState(){
        LinkedList<NFAState> lstate = new LinkedList<NFAState>();
        for(int i =0; i<this.lGoingEdge.size(); i++){
            if(lGoingEdge.get(i).isEpsilon){
                lstate.addLast(lGoingEdge.get(i).dstState);
            }
        }
        return lstate;
    }

    
	public void addGoingEdge(NFAEdge newEdge) {
		this.lGoingEdge.addLast(newEdge);
		
	}

	public void removeComingEdge(NFAEdge deposingEdge){
		this.lComingEdge.remove(deposingEdge);
	}
	
	public void removeGoingEdge(NFAEdge deposingEdge){
		this.lGoingEdge.remove(deposingEdge);
	}
	
	public void addComingEdge(NFAEdge newEdge) {
		this.lComingEdge.addLast(newEdge);
		
	}

	public LinkedList<NFAEdge> getListGoingEdge() {
		
		return this.lGoingEdge;
	}
	
public LinkedList<NFAEdge> getListComingEdge() {
		
		return this.lComingEdge;
	}

}

