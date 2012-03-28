package RegexEnginev2;

import NFA.NFAEdge;
import java.util.LinkedList;
import PCREv2.Refer;

public class BlockChar {

    public String value;
    public int id;          // indicate type of value.
    public int order;       //use for builHDL
    public String modifier;
    public ReEngine engine; //TODO    remove
    public LinkedList<BlockState> lState;
    public boolean[] value256;

    public BlockChar(NFAEdge edge, ReEngine engine) {
        this.engine = engine;
        this.value = edge.value;
        this.id = edge.id;
        this.lState = new LinkedList<BlockState>();
        this.order = 0;
        this.modifier = edge.modifier;
        this.converto256bit();

    }

    public BlockChar(String value, int id, String modifier, ReEngine engine) {
        this.engine = engine;
        this.value = value;
        this.id = id;
        this.lState = new LinkedList<BlockState>();
        this.order = 0;
        this.modifier = modifier;
        this.converto256bit();
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
        if (this.id == Refer._char_start) {
            if (walk.id == this.id) {
                if (this.modifier.indexOf('m') != -1 && walk.modifier.indexOf('m') != -1) {
                    res = true;
                } else if (this.modifier.indexOf('m') == -1 && walk.modifier.indexOf('m') == -1) {
                    res = true;
                }
            }

        } else if (this.id == Refer._char_end) {
            if (walk.id == this.id) {
                res = true;
            }
        } else {
            res = true;
            for (int i = 0; i < 256; i++) {
                if (this.value256[i] != walk.value256[i]) {
                    res = false;
                    break;
                }

            }
        }
        return res;
    }

    private void converto256bit() {
        this.value256 = Refer.convertTo256(id, value, modifier);
    }

    void addState(BlockState aThis) {
        this.lState.add(aThis);
    }

    public void print() {
        System.out.println("BlockChar " + order + " id " + id + " value " + value);
        System.out.print("Go to State: ");
        for (int i = 0; i < this.lState.size(); i++) {
            BlockState s = this.lState.get(i);
            System.out.print(" - " + s.engine.order + "." + s.order);
        }
        System.out.print("\n");
    }

    public boolean isStartChar() {
        return this.id == Refer._char_start;
    }

    public boolean isMultiline() {
        return (this.modifier.indexOf('m') != -1);
    }

    public boolean isEndChar() {
        return this.id == Refer._char_end;
    }
}
