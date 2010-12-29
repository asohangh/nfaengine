package RegexEngine;

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


    /* public void print() {
    this.print(null);
    }

    /**
     * Print RegEx Engine Structure on Documnent object.
     * if doc = null, it just print on console.
     * @param doc
     *
    public void print(Document doc) {
    PCRE.Refer.println("List of blockState", doc);
    for (int i = 0; i < this.listBlockState.size(); i++) {
    BlockState temp = this.listBlockState.get(i);
    PCRE.Refer.println(temp.id + ": ", doc);

    if (temp.acceptChar != null) {
    switch (temp.acceptChar.code_id) {
    case Refer._class:
    PCRE.Refer.println("\tAcceptchar: [" + temp.acceptChar.value + "]", doc);
    break;
    case Refer._neg_class:
    PCRE.Refer.println("\tAcceptchar: [^" + temp.acceptChar.value + "]", doc);
    break;
    default:
    PCRE.Refer.println("\tAcceptchar: " + temp.acceptChar.value, doc);
    }
    }
    if (temp.isStart) {
    PCRE.Refer.println("\tIS START", doc);
    }
    if (temp.isEnd) {
    PCRE.Refer.println("\tIS END", doc);
    }
    PCRE.Refer.print("\tComming: ", doc);
    if (temp.comming != null) {
    for (int j = 0; j < temp.comming.size(); j++) {
    PCRE.Refer.print(temp.comming.get(j).id + " - ", doc);
    }
    }

    PCRE.Refer.print("\n\tGoing: ", doc);
    if (temp.going != null) {
    for (int j = 0; j < temp.going.size(); j++) {
    PCRE.Refer.print(temp.going.get(j).id + " - ", doc);
    }
    }
    PCRE.Refer.print("\n", doc);
    }
    PCRE.Refer.print("\nList BlockChar: ", doc);
    for (int i = 0; i < this.listBlockChar.size(); i++) {
    PCRE.Refer.print(this.listBlockChar.get(i).value + " - ", doc);
    }
    PCRE.Refer.println("\nTotal: " + this.listBlockState.size() + " block State " + this.listBlockChar.size() + " blockChar", doc);
    }
     *

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
//====================================================================================

    /**
     * module myDff (q_o,d_i, clk,en,rst);
    input d_i,clk,en,rst;
    output reg q_o;
    always @ (posedge clk)
    begin
     *      if(rst == 1'b1)
     *          q_o <= 1'b0;
     *      else if(!en)
     *          q_o <= q_o;
     *      else
    q_o <= d_i;
    end
    endmodule

     */
    public void buildDflipflop() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this._outputfolder + File.separator + "myDff.v"));
            bw.write("module myDff (q_o,d_i, clk,en,rst);\n "
                    + "\t input d_i,clk,en,rst;\n"
                    + "\t output reg q_o;\n"
                    + "\t always @ (posedge clk)\n"
                    + "\t\t begin\n"
                    + "\t\tif (rst == 1'b1)\n"
                    + "\t\t\tq_o <= 1'b0;\n"
                    + "\t\telse if (en)\n"
                    + "\t\t\tq_o <= d_i;\n"
                    + "\t\telse\n"
                    + "\t\t\tq_o <= q_o;\n"
                    + "\t\t end\n"
                    + "endmodule\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void buildHDL(String folder) {
        this._outputfolder = folder;
        this.buildDflipflop(); // build DFF
        System.out.println(" buluild HDL Engine " + this.ram_id + ": " + this.listBlockChar.size());
        for (int i = 0; i < this.listBlockChar.size(); i++) {
            BlockChar bc = this.listBlockChar.get(i);
            System.out.print(bc.value + "[" + bc.id + "] ");
        }
        System.out.println();
        //Create top module HDL code.
        //this.updateBlockStateOrder();//update oder of blockstate;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this._outputfolder + "engine_" + this.ram_id + "_" + this.order + ".v"));
            //get number of endstate
            int noEndState = this.listEndState.size();
            bw.write("module engine_" + this.ram_id + "_" + this.order + "(out,clk,sod,en");
            for (int i = 0; i < this.listBlockChar.size(); i++) {
                //this.listBlockChar.get(i).id = i; //update id for block char
                bw.write(", in_" + this.listBlockChar.get(i).order);
            }

            bw.write(");\n");
            bw.write("//pcre: " + this.rule.toString() + "\n");
            bw.write("//block char: ");
            for (int i = 0; i < this.listBlockChar.size(); i++) {
                bw.write(this.listBlockChar.get(i).value + "[" + this.listBlockChar.get(i).id + "], ");
            }
            bw.write("\n");
            // declare parameter, variable verilog
            bw.write("\n\tinput clk,sod,en;\n");
            bw.write("\n\tinput ");
            for (int i = 0; i < this.listBlockChar.size(); i++) {
                if (i == 0) {
                    bw.write("in_" + this.listBlockChar.get(i).order);
                } else {
                    bw.write(", in_" + this.listBlockChar.get(i).order);
                }
            }
            bw.write(";\n");
            if (noEndState == 1) {
                bw.write("\toutput out;\n\n");
            } else {
                bw.write("\toutput [" + (noEndState - 1) + ":0] out;\n\n");
            }

            //routing here
            //net connect block State
            //START STATE
            boolean start_n = false;
            if (this.rule.getModifier().contains("t")) { // this pcre contain ^
                start_n = true;
                if (this.rule.getModifier().contains("m")) {
                    //todo: if have m that mean start state will have incoming char is \n;
                    bw.write("\n\tstate_" + this.ram_id + "_" + this.order + "_" + "0 St_0 (y1," + "~in_" + this.getStartState().acceptChar.order + ",clk,en,sod);\n");
                    //start_n = true;
                    //bw.write("\t charBlock_" + this.id_num + "_" + "100000" + " cB (y3,char);\n");
                } else {
                    //TODO: need to replace it to new structure
                    // bw.write("\tassign w0 = ~y1;");
                    // bw.write ("\tmyDff D_" + this.id_num + " (y,1'b1,clk,en,sod);\n");
                    // bw.write("\tassign y1=~y;\n");
                    bw.write("\n\tstate_" + this.ram_id + "_" + this.order + "_" + "0 St_0 (y1,1'b1,clk,en,sod);\n");
                }
            } else {
                //bw.write("\n\tstate_" + this.id_ram + "_" + this.id_num + "_" + "0 St_0 (y1,1'b0,clk,en,sod);\n");
                bw.write("\n\t assign  y1 = 0;\n");
            }
            bw.write("\tassign w0 = ~y1;\n");

            //route other blockState
            int size = this.listBlockState.size();
            for (int i = 1; i < size; i++) {
                BlockState bt = this.listBlockState.get(i);
                if (bt.isEnd) {
                    if (noEndState == 1) {
                        bw.write("\tstate_" + this.ram_id + "_" + this.order + "_" + bt.order + " BlockState_" + this.ram_id + "_" + this.order + "_" + bt.order + " (out,clk,en,sod");
                    } else {
                        //get index on listBlockend
                        int index = this.listEndState.indexOf(bt);
                        bw.write("\tstate_" + this.ram_id + "_" + this.order + "_" + bt.order + " BlockState_" + this.ram_id + "_" + this.order + "_" + bt.order + " (out[" + index + "]" + ",clk,en,sod");
                    }
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",w" + bt.comming.get(j).order);
                    }
                    bw.write(");\n");
                } else if (bt.isConRep) {
                    //TODO
                    //module blockContraint_0_id (out, in, clk);
                    BlockConRep btc = (BlockConRep) bt;
                    //(out,i_char_1,i_char_2,i_clk,i_en,i_rst,in0);
                    bw.write("\tBCR_state_" + this.ram_id + "_" + this.order + "_" + bt.order + " BlockState_ConRep_" + this.ram_id + "_" + this.order + "_" + bt.order +
                            "(\n" +
                            "\t\t.out(w" + bt.order +"),\n");

                    // need insert character
                    for (int j = 0; j < btc.lChar.size(); j++) {
                        bw.write("\t\t.i_char_" + btc.lChar.get(j).order +"(in_" + btc.lChar.get(j).order + "),\n");
                    }

                    bw.write("\t\t.i_clk(clk),\n" +
                            "\t\t.i_en(en),\n" +
                            "\t\t.i_rst(sod)");
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",\n\t\t.in" + j +  "(w" + bt.comming.get(j).order +")");
                    }
                    bw.write(");\n");
                } else {//normal state.
                    bw.write("\tstate_" + this.ram_id + "_" + this.order + "_" + bt.order + 
                            " BlockState_" + this.ram_id + "_" + this.order + "_" + bt.order +
                            " (w" + bt.order +
                            ",in_" + bt.acceptChar.order +
                            ",clk,en,sod");
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",w" + bt.comming.get(j).order);
                    }
                    bw.write(");\n");
                }
            }

            bw.write("endmodule\n\n");

            //Build hdl block State
            for (int i = 0; i < this.listBlockState.size(); i++) {
                //if not have ^ operator, first state is not need.
                if(this.listBlockState.get(i).isStart){
                    if(!start_n)
                        continue;
                }

                if (this.listBlockState.get(i).isConRep) {
                    ((BlockConRep)this.listBlockState.get(i)).buildHDL(folder);
                } else {
                    this.listBlockState.get(i).buildHDL(bw);
                }
            }
            bw.flush();
            bw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * This funciton will traverse listBlockState of this engine
     *   - and look for state have isStart = true,
     *  - the fist one will be return.
     *  - null is ...
     * @return
     */
    private BlockState getStartState() {
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
