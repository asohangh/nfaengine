package RegexEnginev2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 *	Class cac doi tuong dai dien cho trang thai theo mo hinh Compact Architecture for High-Throughput
 *   yeyang, weirongj, prasanna
 */
public class BlockState {
    // essential attribute

    public LinkedList<BlockState> comming; // blockstates which reach by this state
    public BlockChar acceptChar; // each state has an accepted char
    public LinkedList<BlockState> going; // blockstates which is reached by this state.
    // type of BlockState.
    //Since we handle many types of blockstate via extends mechanism
    public boolean isStart = false;// for Block Start
    public boolean isEnd = false; // for Block End
    public boolean isConRep = false; // for block constraint repetition handling
    public boolean isPrefix = false; // for Prefix block handling
    public boolean isInfix = false; // for Infix block handling.
    
    // orther attribute
    public ReEngine engine;
    public int order;

    public BlockState() {
        this.engine = null;
        this.order = 0;
        this.acceptChar = null;
        this.comming = null;
        this.going = null;
    }

    BlockState(ReEngine engine) {
        this.engine = engine;
//
        this.acceptChar = null;
        this.comming = new LinkedList<BlockState>();
        this.going = new LinkedList<BlockState>();

    }

    public void printTest() {
        System.out.println("this is BlockState");
    }

    /**
     * 
     * @param old   : to be replaced
     * @param mew   : replace
     */
    public void replaceChar(BlockChar old, BlockChar mew) {
        if (this.acceptChar != old) {
            System.out.println("BlockState: Error replace char ");
            return;
        }
        this.acceptChar = mew;
        mew.addState(this);

    }

    public void print() {
        System.out.println("BlockState engine: " + this.engine.order + " - oder: " + order);
        //accpethar
        if (this.acceptChar == null) {
            System.out.println("Accept Char: null");
        } else {
            System.out.println("Accept Char: " + this.acceptChar.order + " " + this.acceptChar.value + " - modifier: " + this.acceptChar.modifier);
        }
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
