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
public class BlockPrefix extends BlockStart {

    public int prefixId = -1;

    public BlockPrefix() {
        super();
        this.isPrefix = true;
    }

    BlockPrefix(NFAEdge enfa, ReEngine aThis) {
        super();
        this.isPrefix = true;
//
        this.comming = new LinkedList<BlockState>();
        this.going = new LinkedList<BlockState>();
        this.engine = aThis;
        this.prefixId = Integer.parseInt(enfa.value);
    }

    @Override
    public void print() {
        System.out.println("BlockPrefix engine: " + this.engine.order + " - oder: " + order + " - id " + this.prefixId);
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
