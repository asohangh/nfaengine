package RegexEngine;

import NFA.NFAEdge;
import NFA.NFAState;
import java.util.LinkedList;
import java.io.BufferedWriter;
import PCRE.Refer;

public class BlockChar {

    public String value;
    public int id;          // indicate type of value.
    public int order;       //use for builHDL
    public ReEngine engine;
    public LinkedList<BlockState> lState;

    public BlockChar(NFAEdge edge, ReEngine engine) {
        this.engine = engine;
        this.value = edge.value;
        this.id = edge.id;
        this.lState = new LinkedList<BlockState>();
        this.order = 0;
    }

    public BlockChar(String value,int id, ReEngine engine) {
        this.engine = engine;
        this.value = value;
        this.id = id;
        this.lState = new LinkedList<BlockState>();
        this.order = 0;
    }

    BlockChar() {
        this.lState = new LinkedList<BlockState>();

    }

    /**
     * todo
     * @param temp
     * @param walk
     * @return
     */
    public boolean compareTo(BlockChar walk) {
        boolean res = false;
        if (this.id == walk.id) {
            if (this.value.compareTo(walk.value) == 0) {
                res = true;
            } else {
                if (this.engine.rule.getModifier().indexOf("i") != -1 && this.value.compareToIgnoreCase(walk.value) == 0) {// neu la case insensitive
                    res = true;
                }
            }
        }
        return res;
    }
}
