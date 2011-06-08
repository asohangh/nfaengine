/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HDL_Generator;

import NFA.NFA;
import ParseTree.ParseTree;
import RegexEngine.BlockChar;
import RegexEngine.BlockConRep;
import RegexEngine.BlockState;
import RegexEngine.ReEngine;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
/**
 *
 * @author heckarim
 */

/*
 *
 *  this fuction will generate CRB depend on its value.
 *
 * +, only one character. num of repet is small, <=8.
 *
 */
public class HDL_CRB_Generator_v2 {

    BlockConRep blockConRep;
    ReEngine ContEngine;

    public HDL_CRB_Generator_v2(BlockConRep bcr) {
        this.blockConRep = bcr;
    }

    public void genHDL(String folder) throws IOException {
        String filename = "BCR_state_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + ".v";
        BufferedWriter bw = new BufferedWriter(new FileWriter(folder + filename));

        this.genHDL_CounterBased(bw);
        //TODO
        //...

    }

    /**
     *
     * @param bw
     * @throws IOException
     *
     * module BCR_state_0_1_4(out,in_char,clk,en,rst,in0,in1);
    input in_char,clk,en,rst,in0,in1;
    output out;
    wire w_sub,w_rst;
    SubRegex_0_1_4 BCREngine (.o_sub(w_sub),.o_rst(w_rst),
    .in_6(q_out[6]),
    .in_1(q_out[1]));
    CountCompUnit_0_1_4 counter(.out(out1),
    .clk(clk),
    .inc(w_sub),
    .en_in(en),
    .rst(rst),
    .rst_inc(w_rst));
    endmodule
     */
    public void genHDL_CounterBased(BufferedWriter bw) throws IOException {
        int inputsize = this.blockConRep.comming.size();
        bw.write("module BCR_state_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + "(out,");
        for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
            bw.write("i_char_" + this.blockConRep.lChar.get(j).order + ",");
        }
        
        bw.write("i_clk,i_en,i_rst");
        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }

        bw.write(");\n");
        bw.write("\tinput i_clk,i_en,i_rst");
        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }

        for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
            bw.write(",i_char_" + this.blockConRep.lChar.get(j).order);
        }
        bw.write(";\n");
        bw.write("\toutput out;\n");
        bw.write("\twire w_sub,w_rst,w_state;\n");

        //Create engine ready for connect
        this.createBCREngine();
        //Routing for SubRegex module, this include reset module
        //or all input from test
        bw.write("\tassign w_state = in0");
        for (int i = 1; i < inputsize; i++) {
            bw.write("||in" + i);
        }
        bw.write(";\n");

        bw.write("\tSubRegex_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + " BCREngine "
                + "(\n"
                + "\t\t.o_sub(w_sub),\n"
                + "\t\t.o_rst(w_rst),\n"
                + "\t\t.i_rst(i_rst),\n"
                + "\t\t.i_clk(i_clk),\n"
                + "\t\t.i_en(i_en),\n"
                + "\t\t.i_state(w_state)");

        for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
            bw.write(",\n\t\t.i_char_" + this.blockConRep.lChar.get(j).order + "(i_char_" + this.blockConRep.lChar.get(j).order + ")");
        }

        bw.write(");\n");

        bw.write("\tCountCompUnit_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + " Counter "
                + "(\n\t\t.out(out),\n"
                + "\t\t.i_clk(i_clk),\n"
                + "\t\t.i_inc(w_sub),\n"
                + "\t\t.i_en(i_en),\n"
                + "\t\t.i_rst(i_rst),\n"
                + "\t\t.i_rst_inc(w_rst));"
                + "\n");

        bw.write("\nendmodule\n\n");

        //bulid subregexengine
        this.genHDLSubRegex(bw);
        this.genHDLCountComp(bw);


        bw.flush();
        bw.close();

    }

    /**
     *
     * module blockContraint_0_id (out, in, clk);
    //input [7:0] char;
    input clk;
    input [...]in;
    output out;

    or(w0,outinc,in);
    or(out_inc,...comingblockend);
    nor(rst_inc,..outcuablokstate)
    counter (out,...);

    charBlock_0_0_109 (char_0_0_109,char);
    charBlock_0_2_99 (char_0_2_99,char);

    //state_0_0 (w0,clk,1);
    state_0_1 (w1,char_0_0_109,clk,w0);
    state_0_2 (w2,char_0_1_null,clk,w1);
    state_0_3 (w3,char_0_2_99,clk,w2);
    //state_0_4 (out,clk,w3);
    endmodule
     */
    public void buildState(BufferedWriter bw) throws IOException {
        for (int i = 0; i < this.ContEngine.listBlockState.size(); i++) {
            if (this.ContEngine.listBlockState.get(i).isStart
                    || this.ContEngine.listBlockState.get(i).isEnd) {
                continue;
            } else if (this.ContEngine.listBlockState.get(i).isConRep) {
                //We currently don't support it, =.=".
            } else {
                BlockState bt = this.ContEngine.listBlockState.get(i);
                int inputsize = bt.comming.size();
                bw.write("module state_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + "_" + bt.order
                        + "(out1,in_char,clk,en,rst");
                for (int j = 0; j < inputsize; j++) {
                    bw.write(",in" + j);
                }

                bw.write(");\n");
                bw.write("\tinput in_char,clk,en,rst");
                for (int j = 0; j < inputsize; j++) {
                    bw.write(",in" + j);
                }
                bw.write(";\n");

                bw.write("\toutput out1;\n");
                bw.write("\twire w1,w2;\n");

                if (inputsize > 1) {
                    bw.write("\tor(w1");
                    for (int j = 0; j < inputsize; j++) {
                        bw.write(",in" + j);
                    }
                    bw.write(");\n");
                } else {
                    bw.write("\tassign w1 = in0; \n");
                }

                bw.write("\tand(w2,in_char,w1);\n");
                bw.write("\tmyDff Dff (out1,w2,clk,en,rst);\n");
                bw.write("endmodule\n\n");
            }
        }

    }

    /**
     * +, create BCRengine
     * +, Change order of each block char to order of blockchar in lchar of block Conrep
     *
     */
    private void createBCREngine() {
        String rule = this.blockConRep.pattern;
        ParseTree tTree = new ParseTree(rule);
        NFA tnfa = new NFA();
        tnfa.buildNFA(tTree);
        tnfa.reduceRedundantState();
        ReEngine tengine = new ReEngine();
        tengine.buildEngine(tnfa);

        //update order of each block char in tengine;
        for (int i = 0; i < tengine.listBlockChar.size(); i++) {
            BlockChar bc = tengine.listBlockChar.get(i);
            for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
                BlockChar nbc = this.blockConRep.lChar.get(j);
                if (nbc.id == bc.id && nbc.value.compareToIgnoreCase(bc.value) == 0) {
                    //nbc.engine = this.engine;
                    bc.order = nbc.order;
                }
            }
        }
        //update order of each blockstate
        for (int i = 0; i < tengine.listBlockState.size(); i++) {
            BlockState bc = tengine.listBlockState.get(i);
            bc.order = i;
        }
        this.ContEngine = tengine;
    }

    private void genHDLSubRegex(BufferedWriter bw) throws IOException {
        bw.write("\nmodule SubRegex_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order
                + "(o_sub,o_rst,i_clk,i_rst,i_en,i_state");
        for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
            bw.write(",i_char_" + this.blockConRep.lChar.get(j).order);
        }
        bw.write(");\n");
        bw.write("\tinput i_clk,i_rst,i_en,i_state");
        for (int j = 0; j < this.blockConRep.lChar.size(); j++) {
            bw.write(",i_char_" + this.blockConRep.lChar.get(j).order);
        }
        bw.write(";\n");
        bw.write("\toutput o_sub,o_rst;\n");
        //get last state
        BlockState blockend = null;
        for (int i = 0; i < this.ContEngine.listBlockState.size(); i++) {
            blockend = this.ContEngine.listBlockState.get(i);
            if (blockend.isEnd) {
                break;
            }
        }
        bw.write("\t wire w0");
        for (int i = 0; i < this.ContEngine.listBlockState.size(); i++) {
            BlockState bt = this.ContEngine.listBlockState.get(i);

            if (bt.isStart || bt.isEnd) {
                continue;
            }
            bw.write(",w" + bt.order);
        }
        for (int i = 0; i < blockend.comming.size(); i++) {
            bw.write(",wb_" + blockend.comming.get(i).order);
        }
        bw.write(";\n\n");

        //assign input from other block state
        bw.write("\tassign w0 = i_state");
        for (int i = 0; i < blockend.comming.size(); i++) {
            bw.write("||w" + blockend.comming.get(i).order);
        }
        bw.write(";\n");
        //assign Reset Unit
        bw.write("\tassign o_rst = (w0||w1");
        for (int i = 2; i < this.ContEngine.listBlockState.size(); i++) {
            BlockState bt = this.ContEngine.listBlockState.get(i);
            if (!(bt.isStart || bt.isEnd)) {
                bw.write("||w" + bt.order);
            }
        }
        bw.write(");\n");
        //assign output from subregexengine

        for (int i = 0; i < blockend.comming.size(); i++) {
            BlockState tbt = blockend.comming.get(i);
            bw.write("\t assign wb_" + tbt.order + " = (w" + tbt.comming.getFirst().order);
            for (int j = 1; j < tbt.comming.size(); j++) {
                bw.write("||w" + tbt.comming.get(j).order);
            }
            bw.write(")&&i_char_" + tbt.acceptChar.order + ";\n");
        }


        bw.write("\tassign o_sub = (wb_" + blockend.comming.getFirst().order);
        for (int i = 1; i < blockend.comming.size(); i++) {
            bw.write("||wb_" + blockend.comming.get(i).order);
        }
        bw.write(");\n");
        //Connect state;
        int size = this.ContEngine.listBlockState.size();
        for (int i = 1; i < size; i++) {
            BlockState bt = this.ContEngine.listBlockState.get(i);
            if (bt.isEnd) {
            } else if (bt.isStart) {
                //todo
            } else {//normal state.
                try {
                    bw.write("\tstate_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + "_" + bt.order
                            + " BlockState_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order + "_" + bt.order
                            + " (w" + bt.order
                            + ",i_char_" + bt.acceptChar.order
                            + ",i_clk,i_en,i_rst");
                } catch (NullPointerException ex) {
                    System.out.println("Nullpoint");
                    System.exit(0);
                }
                for (int j = 0; j < bt.comming.size(); j++) {
                    bw.write(",w" + bt.comming.get(j).order);
                }
                bw.write(");\n");
            }
        }
        bw.write("\nendmodule\n\n");
        this.buildState(bw);
    }

    private void genHDLCountComp(BufferedWriter bw) throws IOException {
        bw.write("\nmodule CountCompUnit_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order
                + "(out,i_rst_inc,i_inc,i_clk,i_rst,i_en);\n");
        bw.write("\t//Contraint repetition: " + this.blockConRep.pattern);
        bw.write("\n\tparameter\tK=" + this.blockConRep.k + ";\n");
        bw.write("\tparameter\tM=" + this.blockConRep.m + ";\n");
        bw.write("\tparameter\tN=" + this.blockConRep.n + ";\n");
        bw.write("\tparameter\tG=" + this.blockConRep.g + "; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;\n");

        bw.write("\n\tinput\t\ti_inc, i_clk, i_en, i_rst, i_rst_inc;\n");
        bw.write("\toutput\t\tout;\n");
        bw.write("\twire\tcompN, compM, en, mux_out;\n");
        bw.write("\twire\t[K-1:0]\tcReg;\n\n");
        bw.write("\tcounter_Kbit_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order
                + " count1 (cReg,i_clk,en,i_inc,i_rst,~i_rst_inc);\n");
        bw.write("\tassign compN = (cReg >= N);\n");
        bw.write("\tassign compM = (cReg <= M);\n");
        bw.write("\tassign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;\n");
        bw.write("\tassign out = mux_out & i_rst_inc;\n");
        bw.write("\tassign en = ((G==0)?!mux_out:1'b1) && i_en ;\n");
        bw.write("endmodule\n\n");

        bw.write("module counter_Kbit_" + this.blockConRep.engine.ram_id + "_" + this.blockConRep.engine.order + "_" + this.blockConRep.order
                + " (cReg,clk,en,inc,rst,rst_inc);\n");
        bw.write("\tparameter\tK = " + this.blockConRep.k + ";\n");
        bw.write("\tinput\tclk, inc, rst, rst_inc, en;\n");
        bw.write("\toutput\t[K-1:0] cReg;\n");
        bw.write("\treg\t\t[K-1:0] cReg;\n");
        bw.write("\n\talways @(posedge clk)\n");// or posedge rst or posedge rst_inc)\n");
        bw.write("\tbegin\n");
        bw.write("\t\tif(rst == 1'b1)\n");
        bw.write("\t\t\tcReg <= 0;\n");
        bw.write("\t\telse if(rst_inc == 1'b1)\n");
        bw.write("\t\t\tcReg <= 0;\n");
        bw.write("\t\telse if(en == 1'b0)\n");
        bw.write("\t\t\tcReg <= cReg;\n");
        bw.write("\t\telse if(inc == 1'b1)\n");
        bw.write("\t\t\tcReg <= cReg + 1;\n");
        bw.write("\tend\n");
        bw.write("endmodule\n\n");
    }
}
