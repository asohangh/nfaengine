package RegexEngine;

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

    public LinkedList<BlockState> comming; //list cac block state toi no, day cung chinh la cac trang thai se OR lai voi nhau
    public BlockChar acceptChar; // BlockChar su dung voi AND
    public LinkedList<BlockState> going; //list cac block state no se di toi
    // type of BlockState.
    public boolean isStart;// block start ko co acceptchar va khong co comming
    public boolean isEnd; // block end ko co going va khong co acceptchar.
    public boolean isConRep; // is constraint repetition operator block.
    // orther attribute
    public ReEngine engine;
    public int order;

    public BlockState() {
        this.isStart = false;
        this.isEnd = false;
        this.acceptChar = null;
        this.comming = null;
        this.going = null;
    }

    BlockState(int type, ReEngine engine) {
        this.engine = engine;
        if (type == ReEngine._start) {
            this.isStart = true;
            this.isEnd = false;
            this.acceptChar = null;
            this.going = new LinkedList<BlockState>();
            this.comming = null;
        } else if (type == ReEngine._end) {
            this.isStart = false;
            this.isEnd = true;
            this.acceptChar = null;
            this.going = null;
            this.comming = new LinkedList<BlockState>();
        } else if (type == ReEngine._normal) {
            this.isStart = false;
            this.isEnd = false;
            this.acceptChar = null;
            this.comming = new LinkedList<BlockState>();
            this.going = new LinkedList<BlockState>();
        }
    }

    public void printTest(){
        System.out.println("this is BlockState");
    }
}
