/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NFA;
//import engineRe.BlockState;

import engineRe.BlockState;


/**
 *
 * @author Jeffrey W Roberts
 */
public class NFAState {
    public NFAState nextState = null; //point to nextState in linkedlist.
    public int order;                 //easy to identify
    public boolean isFinal = false;
    public NFAEdge edge = null;

    //engineRe Them mot so cai dung cho ReEngine
    public boolean isVisited=false;
    public BlockState returnBlock; // neu really visited, returnBlock se tro toi mot blockState  nao do.

    //end engineRe



    //engineRe them mot so ham dung cho viec xay dung regular exp engine

    public NFAEdge getCharEdge(){
    	//remember that have maximum only one charEdge in any NFAState
    	NFAEdge ret = null;
    	NFAEdge pre = null;
    	NFAEdge walk = this.edge;
    	while(walk != null && !walk.isEpsilon ){
    		pre = walk;
    		walk = walk.nextEdge;
    	}
    	if(walk == null && pre != null)
    		ret = pre;
    	return ret;
    }
}

