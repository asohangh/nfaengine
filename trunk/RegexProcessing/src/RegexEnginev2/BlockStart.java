/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class BlockStart extends BlockState {

    public BlockStart() {
        super();
        this.isStart = true;
    }

    BlockStart(ReEngine aThis) {
        super();
        this.isStart = true;
        this.engine = aThis;

        //
        this.acceptChar = null;
        this.going = new LinkedList<BlockState>();
        this.comming = null;
    }

    @Override
    public void print() {
        System.out.println("BlockStart engine: " + this.engine.order + " - oder: " + order);
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
