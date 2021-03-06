/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor'  */
package mip.pcre.nfa_v2;

import mip.pcre.pcre_v2.Refer;
import ParseTree.Node;

/**
 *
 * @author Hoang Long Le & Heckarim
 */
public class NFAEdge {
    // two node of the edge

    public NFAState srcState = null;
    public NFAState dstState = null;
    // Indicate value of edge.
    public boolean isEpsilon = true;        // is epsilon ?
    public String value = null;             // accept character
    //TODO
    public String modifier = ""; //modifier is together with value.
    public boolean[] value256 = new boolean[256]; // store value position on 256 characters table.
    public int id = -1;                          // PCRE.refer
    // include for manage constraint repetiton operator.
    //public boolean isConRep = false;
    public Node crNode = null;

    NFAEdge() {
        //this.modifier = modifier;
        for (int i = 0; i < 256; i++) {
            value256[i] = false;
        }
        //todo 
    }

    public boolean isConRep() {
        if (this.id == Refer._op_atleast || this.id == Refer._op_between || this.id == Refer._op_exactly || this.id == Refer._op_atMost) {
            return true;
        }
        return false;
    }

    public boolean isPrefix() {
        if (this.id == Refer._prefix) {
            return true;
        }
        return false;
    }

    public boolean isInfix() {
        if (this.id == Refer._infix) {
            return true;
        }
        return false;
    }

    public void converto256() {
        this.value256 = Refer.convertTo256(id, value, modifier);
    }

    //check if this edge have the same src and value with edge1
	public boolean isEqualTo(NFAEdge edge1) {
		if(this.srcState != edge1.srcState){
			//if circular edge, it's possibility of reduction
			if(this.srcState != this.dstState || edge1.srcState != edge1.dstState)
				return false;
		}
		if(this.isEpsilon != edge1.isEpsilon)
			return false;
		if(this.isEpsilon)
			return true;
		//check value
		this.converto256();
		edge1.converto256();
		for(int i =0; i<256; i++){
			if(this.value256[i] != edge1.value256[i])
				return false;
		}
		return true;
	}

	public boolean isCircularEpsilonEdge() {
		
		return (this.isEpsilon && this.srcState == this.dstState);
	}

	public void remove() {
		this.srcState.removeGoingEdge(this);
		this.dstState.removeComingEdge(this);
	}
}
