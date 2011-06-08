package RegexEnginev2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import NFA.*;
import java.io.File;
import PCRE.PcreRule;

/**
 *
 * @author heckarim
 *	This class is in charge of managing RegEx engine Structure and generating Verilog HDL code.
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
    public int ram_id;                     // use in rambuilding.
   public  NFA nfa;

    public ReEngine() {
        this.listBlockChar = new LinkedList<BlockChar>();
        this.listBlockState = new LinkedList<BlockState>();

    }

    /**
     * This function will build engine from given NFA
     * +, firstly: add all possible block char and block state to linkedlists
     * +, secondly : create route(incoming, outcoming) each state
     * Note:
     * +, \n character will be insert if modifier t is seen.
     *
     * @param nfa
     */
    public void buildEngine(NFA nfa) {
        this.nfa = nfa;
        this.rule = nfa.getRule();
        //this variable for keep track of builded state.
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
            if (enfa.isConRep) {
                bs = new BlockConRep(enfa, this);
                this.listBlockState.add(bs);
            } else {
                bs = new BlockState(ReEngine._normal, this);
                BlockChar bc = new BlockChar(enfa, this);
                bs.acceptChar = bc;
                bc.lState.add(bs);
                this.listBlockState.add(bs);
                this.listBlockChar.add(bc);
            }
            arr[enfa.srcState.order] = bs;
            arr[enfa.dstState.order] = bs;
        }
        
        //cause first edge and last edge is epsion so seperately need to add
        //block start and block end 
        for (int i = 0; i < nfa.lState.size(); i++) {
            if (nfa.lState.get(i).isStart) {
                BlockState bs = new BlockState(ReEngine._start, this);
                //if there is operator ^ and modifier m
                if(nfa.tree.rule.getModifier().contains("t") &&
                        nfa.tree.rule.getModifier().contains("m")){
                    BlockChar bc = new BlockChar("\\x0A", PCRE.Refer._ascii_hex, this);
                    bs.acceptChar = bc;
                    bc.lState.add(bs);
                    this.listBlockChar.add(bc);
                }
                this.listBlockState.addFirst(bs);
                arr[nfa.lState.get(i).order] = bs;
            }
            if (nfa.lState.get(i).isFinal) {
                BlockState bs = new BlockState(ReEngine._end, this);
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
            this.addRoute(arr[enfa.srcState.order], arr[enfa.dstState.order]);
        }
        // update blockstate and blockchar order
        this.updateBlockCharOrder();
        this.updateBlockStateOrder();
        // update endstate
        this.updateBlockStateEndList();
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
}
