package RegexEnginev2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import NFA.*;
import java.io.File;
import PCREv2.PcreRule;
import PCREv2.Refer;

/**
 *
 * @author heckarim
 *	This class is in charge of managing RegEx engine Structure and generating Verilog HDL code.
 */

/*
 * notes:
 *      +, Each ReEngine stands for pcre matching unit
 *      +, it can contain more than 1 pcre, and more than 1 end state.
 *      +, it has only one start state.
 */
public class ReEngine {
    //Declare constant

    public static final int _start = 0;   //BlockState Start
    public static final int _end = 1;     //Block State Exit
    public static final int _normal = 2;  //Block normal :D
    public static final int _ConRep = 3;
    public String _outputfolder = "GeneratedFiles";                    //HDL output folder.
    public static String _default_folder = System.getProperty("user.dir") + System.getProperty("file.separator");
    public static String _default_file_name = "reengine.dot";
    public LinkedList<BlockChar> listBlockChar;    // All block char in RegEx engine.
    public LinkedList<BlockState> listBlockState;  // All block State in RegEx engine.
    public PcreRule rule;                           // Need infomation about pcre Rule
    public LinkedList<BlockState> listEndState;     // it use for prefix share and generate hdl.
    //orther attribute
    public int order;                      // use in building a list of engine.
    public int groupID;                     // use in rambuilding.
    public NFA nfa;

    public ReEngine() {
        this.listBlockChar = new LinkedList<BlockChar>();
        this.listBlockState = new LinkedList<BlockState>();

    }

    /**
     * This function will build engine from given NFA
     * +, firstly: add all possible block char and block state to linkedlists
     * +, secondly : create route(incoming, outcoming) each state
     * Note:
     * +, third: update
     *
     * @param nfa
     */
    public void buildEngine(NFA nfa) {
        this.nfa = nfa;
        this.rule = nfa.getRule();
        //this variable for keep track of builded states.
        BlockState[] arr = new BlockState[nfa.lState.size()];
        //  update order in each nfastate
        nfa.updateList();
        // Step 1 : create BlockState.
        for (int i = 0; i < nfa.lEdge.size(); i++) {
            NFAEdge enfa = nfa.lEdge.get(i);
            //ignore it if epsilon edge
            if (enfa.isEpsilon) {
                continue;
            }
            //other edge
            BlockState bs;
            if (enfa.isConRep()) {
                bs = new BlockConRep(enfa, this);
                this.listBlockState.add(bs);
            } else if (enfa.isPrefix()) {
                bs = new BlockPrefix(enfa, this);
                this.listBlockState.add(bs);
            } else if (enfa.isInfix()) {
                bs = new BlockInfix(enfa, this);
                this.listBlockState.add(bs);
            } else {
                bs = new BlockState(this);
                BlockChar bc = new BlockChar(enfa, this);
                bs.acceptChar = bc;
                bc.lState.add(bs);
                this.listBlockState.add(bs);
                this.listBlockChar.add(bc);
            }
            arr[enfa.srcState.order] = bs;
            arr[enfa.dstState.order] = bs;
        }

        // insert block start and block end
        for (int i = 0; i < nfa.lState.size(); i++) {
            if (nfa.lState.get(i).isStart) {
                BlockState bs = new BlockStart(this);
                this.listBlockState.addFirst(bs);
                arr[nfa.lState.get(i).order] = bs;
                //System.out.println("insert start state" + nfa.lState.get(i).order);
            }
            if (nfa.lState.get(i).isFinal) {
                BlockState bs = new BlockEnd(this);
                this.listBlockState.addLast(bs);
                arr[nfa.lState.get(i).order] = bs;
            }
        }
        // Step 2: create route.
        for (int i = 0; i < nfa.lEdge.size(); i++) {
            NFAEdge enfa = nfa.lEdge.get(i);
            if (!enfa.isEpsilon) {
                continue;
            }
            //System.out.println(enfa.srcState.order + " " + enfa.dstState.order);
            this.addRoute(arr[enfa.srcState.order], arr[enfa.dstState.order]);
        }

        //Step 3: update order and relate value.F
        // update endstate
        this.updateBlockStateEndList();
        this.formatEngine();
        // update blockstate and blockchar order
        this.updateBlockCharOrder();
        this.updateBlockStateOrder();
    }

    public void generateDotFile(String name, String folder) {
        this.updateBlockStateOrder();
        this.updateBlockCharOrder();
        BufferedWriter bw = null;
        try {
            if (null == folder || folder.isEmpty()) {
                folder = _default_folder;
            }
            if (null == name || name.isEmpty()) {
                name = _default_file_name;
            }
            bw = new BufferedWriter(new FileWriter(folder + name));
            bw.write("digraph \"Regex Engine path\" {"
                    + "\ngraph [ranksep=.2,rankdir=LR];"
                    + "\nnode [shape=circle,fontname=Arial,fontsize=14];"
                    + "\nnode [width=1,fixedsize=true];"
                    + "\nedge [fontname=Arial,fontsize=14];"
                    + "\n-1 [width=0.2,shape=point color=red];"
                    + "\n-1 -> 0 [ color=red];");

            for (int i = 0; i < this.listBlockState.size(); i++) {
                BlockState p = this.listBlockState.get(i);
                if (p.isEnd || p.isStart) {
                    bw.write("\n" + p.order + " [label=\"" + p.order + "\" color=red];");
                } else if (p.isConRep) {
                    bw.write("\n" + p.order + " [label=\"" + p.order + ": " + ((BlockConRep) p).value + "\" color=green];");
                } else {
                    bw.write("\n" + p.order + " [label=\"" + p.order + ": " + p.acceptChar.value + "\" color=green];");
                }
            }
            for (int i = 0; i < this.listBlockState.size(); i++) {
                BlockState q = this.listBlockState.get(i);
                String color = "black";
                if (q.isEnd) {
                    continue;
                }
                for (int j = 0; j < q.going.size(); j++) {

                    bw.write("\n" + q.order + " -> " + q.going.get(j).order + "  [label=\"" + "" + "\" color=" + color + "];");
                }

            }

            bw.write("\n}\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     *  BlockState and blockChar need arrange in order, avoid name conflict.
     */
    public void updateBlockStateOrder() {
        for (int i = 0; i < this.listBlockState.size(); i++) {
            this.listBlockState.get(i).order = i;
        }
    }

    public void updateBlockCharOrder() {
        for (int i = 0; i < this.listBlockChar.size(); i++) {
            this.listBlockChar.get(i).order = i;
        }
    }
    // for support prefix, there may be two or more end state in the same engine.

    private void updateBlockStateEndList() {
        this.listEndState = new LinkedList<BlockState>();
        for (int i = 0; i < this.listBlockState.size(); i++) {
            if (this.listBlockState.get(i).isEnd) {
                this.listEndState.add(this.listBlockState.get(i));
            }
        }
    }

    /**
     * This function will reduce similar blockChar.
     * note:
     *      - depend on code_id and value.
     *     //TODO
     *
     */
    public void reduceBlockChar() {
        for (int i = 0; i < this.listBlockChar.size(); i++) {
            BlockChar temp = this.listBlockChar.get(i);
            for (int j = i + 1; j < this.listBlockChar.size(); j++) {
                BlockChar walk = this.listBlockChar.get(j);
                if (temp.compareTo(walk)) {
                    for (int k = 0; k < walk.lState.size(); k++) {
                        BlockState bs = walk.lState.get(k);
                        //there is two type of block state
                        //if it is conrep, it will have a list of block char
                        if (bs.isConRep) {
                            BlockConRep bcr = ((BlockConRep) bs);
                            bcr.lChar.remove(walk);
                            bcr.lChar.add(temp);
                        } else if (!bs.isEnd) {
                            bs.acceptChar = temp;
                        }
                        temp.lState.add(bs);
                    }
                    this.listBlockChar.remove(j);
                    j--; //just remove one char so ...
                }
            }
        }
    }

    private void addRoute(BlockState src, BlockState dst) {
        //System.out.println(src.order);
        src.going.add(dst);
        dst.comming.add(src);
    }

    public void printBlockChar() {
        this.updateBlockStateOrder();
        //this.updateBlockCharOrder();
        for (int i = 0; i < this.listBlockChar.size(); i++) {
            BlockChar bc = this.listBlockChar.get(i);
            System.out.print("[" + bc.order + ":" + bc.value + "] ");
            for (int j = 0; j < bc.lState.size(); j++) {
                System.out.print(" _ " + bc.lState.get(j).engine.order + "." + bc.lState.get(j).order);
            }
            System.out.print("\n");
        }
    }

    /**
     * This funciton will traverse listBlockState of this engine
     *   - and look for state have isStart = true,
     *  - the fist one will be return.
     *  - null is ...
     * @return
     */
    public BlockState getStartState() {
        BlockState bs = null;
        for (int i = 0; i < this.listBlockState.size(); i++) {
            bs = this.listBlockState.get(i);
            if (bs.isStart) {
                return bs;
            }
        }
        return null;
    }

    /**
     *   check state, char
     *   handle CR block, Prefix, Infix.
     */
    public void formatEngine() {
        //check prefix, repalce start state with prefix
        BlockState bs = null;
        for (int i = 0; i < this.listBlockState.size(); i++) {
            if (this.listBlockState.get(i).isPrefix) {
                bs = this.listBlockState.get(i);
                break;
            }
        }
        if (bs != null) {
            //replace bstart
            this.listBlockState.remove(bs);
            BlockState bstart = this.listBlockState.removeFirst();
            if (!bstart.isStart) {//error
                System.out.println("format engine Error: State Start");
            } else {
                this.listBlockState.addFirst(bs);
            }
        }
        // check Constraint repetition block for list char
        LinkedList<BlockConRep> lstcon = this.getConstraintRepetitionBlock();
        if (lstcon != null) {
            for (int i = 0; i < lstcon.size(); i++) {
                BlockConRep con = lstcon.get(i);
                this.listBlockChar.addAll(con.lChar);
            }
        }
    }

    private LinkedList<BlockConRep> getConstraintRepetitionBlock() {
        LinkedList<BlockConRep> ret = new LinkedList<BlockConRep>();
        for (int i = 0; i < this.listBlockState.size(); i++) {
            if (this.listBlockState.get(i).isConRep) {
                ret.add((BlockConRep) this.listBlockState.get(i));
            }
        }
        if (ret.isEmpty()) {
            return null;
        } else {
            return ret;
        }
    }

    public void print() {
        System.out.println("ReEngine " + order);
        //print blockstate
        System.out.println("ReEngine " + order + " : BlockState size" + this.listBlockState.size());
        for (int i = 0; i < this.listBlockState.size(); i++) {
            this.listBlockState.get(i).print();
        }
        //print blockchar
        System.out.println("ReEngine " + order + " : BlockChar size" + this.listBlockState.size());
        for (int i = 0; i < this.listBlockChar.size(); i++) {
            this.listBlockChar.get(i).print();
        }
    }

    public LinkedList<BlockInfix> getInfix() {
        LinkedList<BlockInfix> ret = new LinkedList<BlockInfix>();
        for (int i = 0; i < this.listBlockState.size(); i++) {
            BlockState bs = this.listBlockState.get(i);
            if (bs.isInfix) {
                ret.add((BlockInfix) bs);
            }
        }
        return ret;
    }
}
