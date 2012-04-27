/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import java.util.LinkedList;

import nfa.NFAEdge;

/**
 *
 * @author heckarim
 */
public class BlockInfix extends BlockState {

    public int infixID;

    public BlockInfix() {
        super();
        this.isInfix = true;
    }

    BlockInfix(NFAEdge enfa, ReEngine aThis) {
        super();
        this.isInfix = true;
        //
        this.comming = new LinkedList<BlockState>();
        this.going = new LinkedList<BlockState>();
        this.engine = aThis;
        this.infixID = Integer.parseInt(enfa.value);
    }

    @Override
    public void print() {
        System.out.println("BlockInfix engine: " + this.engine.order + " - oder: " + order + " - id " + this.infixID);
        //comming state:
        if (comming != null) {
            System.out.print("Coming : ");
            for (int i = 0; i < this.comming.size(); i++) {
                System.out.print(this.comming.get(i).order + " - ");
            }System.out.print("\n");
        }
        // going state:
        if (this.going != null) {
            System.out.print("Going : ");
            for (int i = 0; i < this.going.size(); i++) {
                System.out.print(this.going.get(i).order + " - ");
            }System.out.print("\n");
        }
    }
}
