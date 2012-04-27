/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hdl_generator;

import Builder.MzInstructionBuilder_v1;
import RTL_Creator.RTL_Creator_v1;
import RegexEngine.BlockChar;
import RegexEngine.BlockConRep;
import RegexEngine.BlockState;
import RegexEngine.ReEngine;
import TestPattern.PCRETestCase;
import TestPattern.Pattern;
import TestPattern.TestCase;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import bram.BRAM;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author heckarim
 */
public class HDL_Generator_v1 {

    public String genfolder;
    private RTL_Creator_v1 rtlCreator;
    Random ran = new Random();
    private String genfolder_IPCore;
    private String genfolder_verilog;
    private String genfolder_testbench;
    private String genfolder_packet;
    public LinkedList<TestCase> listTestCase = new LinkedList<TestCase>();

    public void setGenerateFolder(String genfolder) {
        this.genfolder = genfolder;
        
    }
    public void setGenerateFolderDefault(String genfolder) {
        this.genfolder = genfolder;
        this.genfolder_IPCore = genfolder;
        this.genfolder_packet = genfolder;
        this.genfolder_testbench = genfolder;
        this.genfolder_verilog = genfolder;
    }

    public void setRTLCreator(RTL_Creator_v1 rtlCreator) {
        this.rtlCreator = rtlCreator;
    }

    public void genHDL() {
        //Create folder tree
        this.createFolderTree();
        //Generate Each BRAM
        for (int i = 0; i < this.rtlCreator.arrayBRam.length; i++) {
            System.out.println("\n\n HDL_Generator_v1: GenHDL BRAM " + i + "\n");
            this.genHDLBRam(i);
        }
        //Generate top Module
       // this.genSimpleTopModule();
    }

    public void genTestBench() {
        //Generate Each BRAM TestBench
        for (int i = 0; i < this.rtlCreator.arrayBRam.length; i++) {
            this.bram_buildTestBench(this.rtlCreator.arrayBRam[i]);
        }
    }

    private void genHDLBRam(int i) {
        BRAM bram = this.rtlCreator.arrayBRam[i];
        //generate each engien
        for (int j = 0; j < bram.engineList.size(); j++) {
            this.engine_buildHDL(bram.engineList.get(j));
        }
        // build up Block BRam memory
        bram.fillEntryValue();
        //generate BRam verilog file and all content
        //bram.buildHDL();
        this.bram_buildHDL(bram);
        this.bram_buildCOE(bram);
        this.bram_buildXCO(bram);
    }


    /*Example:
    module BRAM_0(out,clk,sod,en,char);
    input clk, sod, en;
    input [7:0] char;
    output [2:0] out;
    wire [12:0] q_out;
    ////BRAM declare
    bram_entity_0 ram (
    .addr(char),
    .clk(clk),
    .dout(q_out),
    engine_0 en_0(out[0], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[4], q_out[5]);
    engine_1 en_1(out[1], clk, sod, en, q_out[1], q_out[2], q_out[3], q_out[6], q_out[7], q_out[8], q_out[9], q_out[10]);
    engine_2 en_2(out[2], clk, sod, en, q_out[1], q_out[6], q_out[11], q_out[12]);
    endmodule
     */
    public void bram_buildHDL(BRAM bram) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_verilog + "BRAM_" + bram.ID + ".v")));
            bw.write("module BRAM_" + bram.ID + "(out,clk,sod,en,char);\n");
            bw.write("\tinput clk, sod, en;\n");
            bw.write("\tinput [7:0] char;\n");

            //get real number of pcre on Bram
            int noPcre = 0;
            for (int i = 0; i < bram.engineList.size(); i++) {
                noPcre += bram.engineList.get(i).listEndState.size();
            }
            //

            bw.write("\toutput [" + (noPcre - 1) + ":0] out;\n");
            bw.write("\twire [" + (bram.width - 1) + ":0] q_out;\n");
            //BRAM declare
            //assume that output of single port ram is q_out[width - 1 : 0]
            bw.write("\n//BRAM declare \n");
            bw.write("\tbram_entity_" + bram.ID + " ram (.addr(char),\n\t\t.clk(clk),\n\t\t.dout(q_out),\n\t\t.en(en));\n");
            //end of bram
            //declare engine
            int index = 0;
            for (int i = 0; i < bram.engineList.size(); i++) {
                //currently, bram support engine with multi end state, so need to
                //sperate upper index and lower index of ouput of each engine.
                int lindex = index;
                int uindex = index + bram.engineList.get(i).listEndState.size() - 1;
                index = uindex + 1;
                if (lindex == uindex) {
                    bw.write("\tengine_" + bram.ID + "_" + bram.engineList.get(i).order + " engine_" + bram.ID + "_" + bram.engineList.get(i).order + "(.out(out[" + uindex + "]), \n\t\t.clk(clk), .sod(sod), \n\t\t.en(en)"); // thieu char
                } else {
                    bw.write("\tengine_" + bram.ID + "_" + bram.engineList.get(i).order + " engine_" + bram.ID + "_" + bram.engineList.get(i).order + "(.out(out[" + uindex + ":" + lindex + "]), \n\t\t.clk(clk), .sod(sod), \n\t\t.en(en)"); // thieu char
                }
                //routing to each engine
                ReEngine te = bram.engineList.get(i);
                //routing to each engine
                for (int j = 0; j < te.listBlockChar.size(); j++) {
                    bw.write(",\n\t\t .in_" + te.listBlockChar.get(j).order + "(q_out[" + te.listBlockChar.get(j).order + "])");
                }

                bw.write(");\n");
            }

            bw.write("\n");
            bw.write("endmodule\n");
            bw.flush();
            bw.close();



        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void bram_buildCOE(BRAM bram) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_IPCore + "BRAM_" + bram.ID + ".coe"));
            bw.write(";");
            for (int i = bram.listBlockChar.size() - 1; i >= 0; i--) {
                bw.write(bram.listBlockChar.get(i).value + " ");
            }
            bw.write("\n");
            bw.write(";Block memory of depth=256, and width=" + bram.width + "\n"
                    + "MEMORY_INITIALIZATION_RADIX=2;\n"
                    + "MEMORY_INITIALIZATION_VECTOR=\n");
            for (int i = 0; i < 256; i++) {
                for (int j = bram.width - 1; j >= 0; j--) {
                    bw.write(bram.BRam[i][j]);
                }
                if (i != 255) {
                    bw.write(",\n");
                } else if (i == 255) {
                    bw.write(";\n");
                }
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bram_buildXCO(BRAM bram) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_IPCore + "bram_entity_" + bram.ID + ".xco")));
            bw.write("# BEGIN Project Options\n"
                    + "SET flowvendor = Foundation_iSE\n"
                    + "SET vhdlsim = True\n"
                    + "SET verilogsim = True\n"
                    + "SET workingdirectory = " + "." + "\n" + //working dir
                    "SET speedgrade = -7\n"
                    + "SET simulationfiles = Behavioral\n"
                    + "SET asysymbol = True\n"
                    + "SET addpads = False\n"
                    + "SET device = xc2vp50\n"
                    + "SET implementationfiletype = Edif\n"
                    + "SET busformat = BusFormatAngleBracketNotRipped\n"
                    + "SET foundationsym = False\n"
                    + "SET package = ff1148\n"
                    + "SET createndf = False\n"
                    + "SET designentry = VHDL\n"
                    + "SET devicefamily = virtex2p\n"
                    + "SET formalverification = False\n"
                    + "SET removerpms = False\n"
                    + "# END Project Options\n"
                    + "# BEGIN Select\n"
                    + "SELECT Single_Port_Block_Memory family Xilinx,_Inc. 6.2\n"
                    + "# END Select\n"
                    + "# BEGIN Parameters\n"
                    + "CSET handshaking_pins=false\n"
                    + "CSET init_value=0\n"
                    + "CSET coefficient_file=" + "BRAM_" + bram.ID + ".coe\n"
                    + "CSET select_primitive=512x36\n"
                    + "CSET initialization_pin_polarity=Active_High\n"
                    + "CSET global_init_value=0\n"
                    + "CSET depth=256\n"
                    + "CSET write_enable_polarity=Active_High\n"
                    + "CSET port_configuration=Read_Only\n"
                    + "CSET enable_pin_polarity=Active_High\n"
                    + "CSET component_name=" + "bram_entity_" + bram.ID + "\n"
                    + "CSET active_clock_edge=Rising_Edge_Triggered\n"
                    + "CSET additional_output_pipe_stages=0\n"
                    + "CSET disable_warning_messages=true\n"
                    + "CSET limit_data_pitch=18\n"
                    + "CSET primitive_selection=Select_Primitive\n"
                    + "CSET enable_pin=true\n"
                    + "CSET init_pin=false\n"
                    + "CSET write_mode=Read_After_Write\n"
                    + "CSET has_limit_data_pitch=false\n"
                    + "CSET load_init_file=true\n"
                    + "CSET width=" + bram.width + "\n"
                    + "CSET register_inputs=false\n"
                    + "# END Parameters\n"
                    + "GENERATE");

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void engine_buildHDL(ReEngine engine) {
        this.buildDflipflop(); // build DFF
        System.out.println(" Builder_v1: engine_buildHDL " + engine.ram_id + ":" + engine.listBlockChar.size());
        System.out.println(engine.rule.getRule());
        for (int i = 0; i < engine.listBlockChar.size(); i++) {
            BlockChar bc = engine.listBlockChar.get(i);
            System.out.print(bc.value + "[" + bc.id + "] ");
        }
        System.out.println();
        //Create top module HDL code.
        //this.updateBlockStateOrder();//update oder of blockstate;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "engine_" + engine.ram_id + "_" + engine.order + ".v"));
            //get number of endstate
            int noEndState = engine.listEndState.size();
            bw.write("module engine_" + engine.ram_id + "_" + engine.order + "(out,clk,sod,en");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                //this.listBlockChar.get(i).id = i; //update id for block char
                bw.write(", in_" + engine.listBlockChar.get(i).order);
            }

            bw.write(");\n");
            bw.write("//pcre: " + engine.rule.toString() + "\n");
            bw.write("//block char: ");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                bw.write(engine.listBlockChar.get(i).value + "[" + engine.listBlockChar.get(i).id + "], ");
            }
            bw.write("\n");
            // declare parameter, variable verilog
            bw.write("\n\tinput clk,sod,en;\n");
            bw.write("\n\tinput ");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                if (i == 0) {
                    bw.write("in_" + engine.listBlockChar.get(i).order);
                } else {
                    bw.write(", in_" + engine.listBlockChar.get(i).order);
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
            if (engine.rule.getModifier().contains("t")) { // this pcre contain ^
                start_n = true;
                if (engine.rule.getModifier().contains("m")) {
                    //todo: if have m that mean start state will have incoming char is \n;
                    bw.write("\n\tstate_" + engine.ram_id + "_" + engine.order + "_" + "0 St_0 (y1," + "~in_" + engine.getStartState().acceptChar.order + ",clk,en,sod);\n");
                    //start_n = true;
                    //bw.write("\t charBlock_" + this.id_num + "_" + "100000" + " cB (y3,char);\n");
                } else {
                    //TODO: need to replace it to new structure
                    // bw.write("\tassign w0 = ~y1;");
                    // bw.write ("\tmyDff D_" + this.id_num + " (y,1'b1,clk,en,sod);\n");
                    // bw.write("\tassign y1=~y;\n");
                    bw.write("\n\tstate_" + engine.ram_id + "_" + engine.order + "_" + "0 St_0 (y1,1'b1,clk,en,sod);\n");
                }
            } else {
                //bw.write("\n\tstate_" + this.id_ram + "_" + this.id_num + "_" + "0 St_0 (y1,1'b0,clk,en,sod);\n");
                bw.write("\n\t assign  y1 = 0;\n");
            }
            bw.write("\tassign w0 = ~y1;\n");

            //route other blockState
            int size = engine.listBlockState.size();
            for (int i = 1; i < size; i++) {
                BlockState bt = engine.listBlockState.get(i);
                if (bt.isEnd) {
                    if (noEndState == 1) {
                        bw.write("\tstate_" + engine.ram_id + "_" + engine.order + "_" + bt.order + " BlockState_" + engine.ram_id + "_" + engine.order + "_" + bt.order + " (out,clk,en,sod");
                    } else {
                        //get index on listBlockend
                        int index = engine.listEndState.indexOf(bt);
                        bw.write("\tstate_" + engine.ram_id + "_" + engine.order + "_" + bt.order + " BlockState_" + engine.ram_id + "_" + engine.order + "_" + bt.order + " (out[" + index + "]" + ",clk,en,sod");
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
                    bw.write("\tBCR_state_" + engine.ram_id + "_" + engine.order + "_" + bt.order + " BlockState_ConRep_" + engine.ram_id + "_" + engine.order + "_" + bt.order
                            + "(\n"
                            + "\t\t.out(w" + bt.order + "),\n");

                    // need insert character
                    for (int j = 0; j < btc.lChar.size(); j++) {
                        bw.write("\t\t.i_char_" + btc.lChar.get(j).order + "(in_" + btc.lChar.get(j).order + "),\n");
                    }

                    bw.write("\t\t.i_clk(clk),\n"
                            + "\t\t.i_en(en),\n"
                            + "\t\t.i_rst(sod)");
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",\n\t\t.in" + j + "(w" + bt.comming.get(j).order + ")");
                    }
                    bw.write(");\n");
                } else {//normal state.
                    bw.write("\tstate_" + engine.ram_id + "_" + engine.order + "_" + bt.order
                            + " BlockState_" + engine.ram_id + "_" + engine.order + "_" + bt.order
                            + " (w" + bt.order
                            + ",in_" + bt.acceptChar.order
                            + ",clk,en,sod");
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",w" + bt.comming.get(j).order);
                    }
                    bw.write(");\n");
                }
            }

            bw.write("endmodule\n\n");

            //Build hdl block State
            for (int i = 0; i < engine.listBlockState.size(); i++) {
                //if not have ^ operator, first state is not need.
                if (engine.listBlockState.get(i).isStart) {
                    if (!start_n) {
                        continue;
                    }
                }
                if (engine.listBlockState.get(i).isConRep) {
                    this.crb_buildHDL((BlockConRep) engine.listBlockState.get(i));
                } else {
                    this.state_buildHDL(bw, engine.listBlockState.get(i));
                }
            }
            bw.flush();
            bw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + File.separator + "myDff.v"));
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

    public void bram_buildTestBench(BRAM bram) {
        //firstly, create testcase
        PCRETestCase pcretestcase = new PCRETestCase();
        for (int i = 0; i < bram.engineList.size(); i++) {
            pcretestcase.addPCRE(bram.engineList.get(i).rule.getRule(), i);
        }
        pcretestcase.generateSimpleTestcase(2);
        //generate two testcase, remember that each testcase contain n pattern corresponding to size of data.
        TestCase tc = pcretestcase.listTestCase.getFirst();
        
        this.listTestCase.add(tc);
            

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_testbench + File.separator + "BRAM_" + bram.ID + "_tb.v")));
            bw.write("`timescale 1ns/1ps\n"
                    + "module bram_test_" + bram.ID + "_tb_v;\n"
                    + "// Inputs\n"
                    + "\treg clk;\n"
                    + "\treg sod;\n"
                    + "\treg en;\n"
                    + "\treg [7:0] char;\n"
                    + "// Outputs\n"
                    + "\twire [" + (bram.engineList.size() - 1) + ":0] out;\n"
                    + "\tinteger fd,i,index;\n"
                    + "// Instantiate the Unit Under Test (UUT)\n"
                    + "\tBRAM_" + bram.ID + " uut (\n"
                    + "\t\t.out(out),\n"
                    + "\t\t.clk(clk),\n"
                    + "\t\t.sod(sod),\n"
                    + "\t\t.en(en),\n"
                    + "\t\t.char(char)\n"
                    + "\t\t);\n"
                    + "\talways @(out) begin\n"
                    + "\t\tindex = 0;\n"
                    + "\t\tfor(i = 0; i <= " + (bram.engineList.size() - 1) + "; i=i+1 )\n"
                    + "\t\t\tif (out[i] == 1)\n"
                    + "\t\t\tbegin\n"
                    + "\t\t\t\tindex = i + 1;\n"
                    + "\t\t\t\t$display(\"%d\",index);\n"
                    + "\t\t\tend\n"
                    + "\t\tif (index == 0)\n"
                    + "\t\t\t$display(\"%d\",index);\n"
                    + "\tend\n"
                    + "\tinitial begin\n"
                    + "\t\tfd = $fopen(\"bram_test_" + bram.ID + "_tb.out\",\"w\");\n"
                    + "\t\t$fmonitor(fd,\"%g %b\", $time, out);\n"
                    + "\tend\n"
                    + "\tinitial begin\n"
                    + "       // Initialize Inputs\n"
                    + "\t\tclk = 0;\n"
                    + "\t\tsod = 1;\n"
                    + "\t\ten = 1;\n"
                    + "\t\tchar = 0;"
                    + "\t// Wait 100 ns for global reset to finish\n"
                    + "\t\t#100;\n"
                    + "\t// Add stimulus here\n"
                    + "\t\tend\n"
                    + "\tinitial begin\n"
                    + "\t\t#100;\n");

            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                bw.write("//" + (i + 1) + "." + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
            }

            bw.write("//---------------------------------------------------\n");
            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                Pattern pt = tc.listPattern.get(i);
                System.out.println("Gen Pattern: " + pt.data);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");

                //firstly, sod =1
                bw.write("\t\t#20 sod = 1;\n");
                char cshow = ' ';
                if (Character.isLetterOrDigit(pt.data.charAt(0))) {
                    cshow = pt.data.charAt(0);
                } else {
                    cshow = ' ';
                }
                bw.write("\t\t#20 char = " + ((int) pt.data.charAt(0)) + ";//" + cshow + "\n");
                if (Character.isLetterOrDigit(pt.data.charAt(1))) {
                    cshow = pt.data.charAt(1);
                } else {
                    cshow = ' ';
                }
                bw.write("\t\t#20 sod = 0;\n"
                        + "\t\t char=" + ((int) pt.data.charAt(1)) + ";//" + cshow + "\n");

                for (int j = 2; j < pt.data.length(); j++) {
                    if (Character.isLetterOrDigit(pt.data.charAt(j))) {
                        cshow = pt.data.charAt(j);
                    } else {
                        cshow = ' ';
                    }
                    bw.write("\t\t#20 char = " + ((int) pt.data.charAt(j)) + ";//" + cshow + "\n");
                    //bw.write("\t\t#10 i_data = {8'd" + ((int) temp.rule.testPartten.charAt(j)) + ",1'b0,1'b1,1'b0};//" + temp.rule.testPartten.charAt(j) + "\n");
                }
                if (i != bram.engineList.size() - 1) {
                    bw.write("\t\t#60\n");
                }
            }

            bw.write("\tend\n"
                    + "\tinitial begin\n"
                    + "\t\t#10 clk = ~clk;\n"
                    + "\t\tforever #10 clk = ~clk;\n"
                    + "\tend\n"
                    + "\t//initial #100000 $finish;\n"
                    + "endmodule\n");

            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("hdl_generator_v1.java: exception: ");
            ex.printStackTrace();
        }
    }
    //block state

    /**
     * Build blockStart
     * @param bw
     * @throws java.io.IOException
     */
    public void state_buildHDL_start(BufferedWriter bw, BlockState bt) throws IOException {
        bw.write("module state_" + bt.engine.ram_id + "_" + bt.engine.order + "_" + bt.order + "(out1,in1,clk,en,rst");
        bw.write(");\n");
        bw.write("\tinput in1,clk,rst,en");
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\tmyDff Dff (out1,in1,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
        ".INIT(1'b0)" +
        ") FDCE_inst (\n" +
        "\t\t.Q(out1),\n" +
        "\t\t.C(clk),\n" +
        "\t\t.CE(en),\n" +
        "\t\t.CLR(rst),\n" +
        "\t\t.D(in1)\n" +
        ");\n");
         *
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     * Build block End
     * @param bw
     * @throws java.io.IOException
     */
    public void state_buildHDL_End(BufferedWriter bw, BlockState bt) throws IOException {
        int inputsize = bt.comming.size();
        bw.write("module state_" + bt.engine.ram_id + "_" + bt.engine.order + "_" + bt.order + "(out1,clk,en,rst");

        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }
        bw.write(");\n");
        bw.write("\tinput clk,rst,en");
        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\twire w1;\n");
        if (inputsize > 1) {
            bw.write("\tor(w1,out1");
            for (int i = 0; i < inputsize; i++) {
                bw.write(",in" + i);
            }
            bw.write(");\n");

        } else {
            bw.write("\tor(w1,out1,in0);\n");
        }
        bw.write("\tmyDff Dff (out1,w1,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
        ".INIT(1'b0)" +
        ") FDCE_inst (\n" +
        "\t\t.Q(out1),\n" +
        "\t\t.C(clk),\n" +
        "\t\t.CE(en),\n" +
        "\t\t.CLR(rst),\n" +
        "\t\t.D(w1)\n" +
        ");\n");
         *
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     * Build normal blockState
     * Example:
     *  module state_0_1(out1,in_char,clk,en,rst,in0);
    input in_char,clk,en,rst,in0;
    output out1;
    wire w1,w2;
    or(w1,in0);
    and(w2,in_char,w1);
    myDff(out1,w2,clk,en,rst);
    endmodule
     */
    public void state_buildHDL(BufferedWriter bw, BlockState bt) throws IOException {
        if (bt.isStart) {
            this.state_buildHDL_start(bw, bt);
            return;
        }
        if (bt.isEnd) {
            this.state_buildHDL_End(bw, bt);
            return;
        }

        int inputsize = bt.comming.size();

        bw.write("module state_" + bt.engine.ram_id + "_" + bt.engine.order + "_" + bt.order + "(out1,in_char,clk,en,rst");

        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }

        bw.write(");\n");
        bw.write("\tinput in_char,clk,en,rst");
        for (int i = 0; i < inputsize; i++) {
            bw.write(",in" + i);
        }
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\twire w1,w2;\n");

        if (inputsize > 1) {
            bw.write("\tor(w1");
            for (int i = 0; i < inputsize; i++) {
                bw.write(",in" + i);
            }
            bw.write(");\n");
        } else {
            bw.write("\tassign w1 = in0; \n");
        }

        bw.write("\tand(w2,in_char,w1);\n");
        bw.write("\tmyDff Dff (out1,w2,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
        ".INIT(1'b0)" +
        ") FDCE_inst (\n" +
        "\t\t.Q(out1),\n" +
        "\t\t.C(clk),\n" +
        "\t\t.CE(en),\n" +
        "\t\t.CLR(rst),\n" +
        "\t\t.D(w2)\n" +
        ");\n");
         *
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }

    public void crb_buildHDL(BlockConRep crb) throws IOException {
        HDL_CRB_Generator_v1 gen = new HDL_CRB_Generator_v1(crb);
        gen.genHDL(this.genfolder_verilog);
    }

    private void createFolderTree() {
        boolean success = false;
        //create IPCores folder
        success = (new File(this.genfolder + "IPCores")).mkdir();
        if (!success) {
            System.out.println("HDL_Generator_v1: can't create folder");
        }
        this.genfolder_IPCore = this.genfolder + "IPCores" + File.separator;
        //create Verilog folder
        success = (new File(this.genfolder + "verilog")).mkdir();
        if (!success) {
            System.out.println("HDL_Generator_v1: can't create folder");
        }
        this.genfolder_verilog = this.genfolder + "verilog" + File.separator;
        //create testbench folder
        success = (new File(this.genfolder + "testbench")).mkdir();
        if (!success) {
            System.out.println("HDL_Generator_v1: can't create folder");
        }
        this.genfolder_testbench = this.genfolder + "testbench" + File.separator;
        //create pcap folder
        success = (new File(this.genfolder + "mz")).mkdir();
        if (!success) {
            System.out.println("HDL_Generator_v1: can't create folder");
        }
        this.genfolder_packet = this.genfolder + "mz" + File.separator;

    }

    public void outputExcelStatistic() {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(this.genfolder + "Report.xls"));
            //ouput rules
            WritableSheet sheet = workbook.createSheet("Rules", 0); // sheet name
            int row = 1;
            for (int i = 0; i < this.rtlCreator.arrayBRam.length; i++) {
                Label label = new Label(0, row, Integer.toString(i));
                sheet.addCell(label);
                LinkedList<String> list = this.rtlCreator.arrayListPcre[i];
                for (int j = 0; j < list.size(); j++) {
                    //index
                    label = new Label(1, row, Integer.toString(row));
                    sheet.addCell(label);
                    //value
                    label = new Label(2, row, list.get(j));
                    sheet.addCell(label);
                    row++;
                }
            }

            //write titles of columns
            WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat w1 = new WritableCellFormat(wf);
            String titles = "Index Bram; Index Pcre; PCRE";
            String[] atitle = titles.split("; ");
            for (int i = 0; i < atitle.length; i++) {
                Label label = new Label(i, 0, atitle[i], w1);
                sheet.addCell(label);
            }



            //ouput statis tic
            sheet = workbook.createSheet("Statistic", 1); // sheet name
            for (int i = 0; i < this.rtlCreator.arrayBRam.length; i++) {
                BRAM bram = this.rtlCreator.arrayBRam[i];
                Label label = new Label(0, i + 1, "BRam" + Integer.toString(i));
                sheet.addCell(label);
                //no of cpre
                label = new Label(1, i + 1, Integer.toString(bram.engineList.size()));
                sheet.addCell(label);
                // no of char
                label = new Label(2, i + 1, Integer.toString(bram.listBlockChar.size()));
                sheet.addCell(label);

                //count nfa state
                int nfa = 0;
                for (int j = 0; j < bram.engineList.size(); j++) {
                    ReEngine re = bram.engineList.get(j);
                    nfa += re.nfa.lState.size();
                }
                label = new Label(3, i + 1, Integer.toString(nfa));
                sheet.addCell(label);

                //count no state crb
                int state = 0, crb = 0;
                for (int j = 0; j < bram.engineList.size(); j++) {
                    ReEngine re = bram.engineList.get(j);
                    for (int k = 0; k < re.listBlockState.size(); k++) {
                        if (re.listBlockState.get(k).isConRep) {
                            crb++;
                            BlockConRep bcr = (BlockConRep) re.listBlockState.get(k);

                        } else {
                            state++;
                        }
                    }
                }
                label = new Label(4, i + 1, Integer.toString(state));
                sheet.addCell(label);
                label = new Label(5, i + 1, Integer.toString(crb));
                sheet.addCell(label);
            }



            //write titles of columns
            wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            w1 = new WritableCellFormat(wf);
            titles = "BRAM; No PCRE; No Char; No NFA; No State; No CRB";
            atitle = titles.split("; ");
            for (int i = 0; i < atitle.length; i++) {
                Label label = new Label(i, 0, atitle[i], w1);
                sheet.addCell(label);
            }


            workbook.write();
            workbook.close();
        } catch (WriteException ex) {
            Logger.getLogger(HDL_Generator_v1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HDL_Generator_v1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void genMZInstructionFile() {
        MzInstructionBuilder_v1 mzbuilder = new MzInstructionBuilder_v1();
        mzbuilder.setTestCaseList(listTestCase);
        mzbuilder.setOutputFolder(this.genfolder_packet);
        //generate testcase for each bram
        mzbuilder.GenerateSeperateTestCase();
        //generate testcase for all will differet load
        mzbuilder.GenerateVariousTestcase();

    }
}
