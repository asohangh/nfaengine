/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HDL_Generator;

import Builder.MzInstructionBuilder_v1;
import RTL_Creator.RTL_Creator_v2;
import RegexEnginev2.BlockChar;
import RegexEnginev2.BlockConRep;
import RegexEnginev2.BlockInfix;
import RegexEnginev2.BlockMemory;
import RegexEnginev2.BlockPrefix;
import RegexEnginev2.BlockState;
import RegexEnginev2.Infix;
import RegexEnginev2.Prefix;
import RegexEnginev2.ReEngine;
import RegexEnginev2.ReEngineGroup;
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

/**
 *
 * @author heckarim
 */
public class HDL_Generator_v2 {

    public String genfolder;
    private RTL_Creator_v2 rtlCreator;
    Random ran = new Random();
    private String genfolder_IPCore;
    private String genfolder_verilog;
    private String genfolder_testbench;
    private String genfolder_packet;
    public LinkedList<TestCase> listTestCase = new LinkedList<TestCase>();

    public void setGenerateFolder(String genfolder) {
        this.genfolder = genfolder;
    }

    public void setRTLCreator(RTL_Creator_v2 rtlCreator) {
        this.rtlCreator = rtlCreator;
    }

    public void genHDL() {
        //Create folder tree
        this.createFolderTree();
        //Generate Each BRAM
        for (int i = 0; i < this.rtlCreator.lsGroup.size(); i++) {
            System.out.println("\n\n HDL_Generator_v2: GenHDL REGroup " + i + "\n");
            this.genHDLGroup(i);
        }
        //Generate top Module
        // this.genSimpleTopModule();
    }

    public void genTestBench() {
        //Generate Each BRAM TestBench
        for (int i = 0; i < this.rtlCreator.lsGroup.size(); i++) {
            this.bram_buildTestBench(this.rtlCreator.lsGroup.get(i));
        }
    }

    private void genHDLGroup(int i) {
        ReEngineGroup group = this.rtlCreator.lsGroup.get(i);

        //generate prefix

        for (int j = 0; j < group.lprefix.size(); j++) {
            this.prefix_buildHDL(group.lprefix.get(j));
        }
        //generate infix
        for (int j = 0; j < group.linfix.size(); j++) {
            this.infix_buildHDL(group.linfix.get(j));
        }

        //generate each engine
        for (int j = 0; j < group.lengine.size(); j++) {
            this.engine_buildHDL(group.lengine.get(j));
        }
        // build up Block BRam memory , 0 is LUT, 1 is BRAm
        this.memory_buildHDL(group, group.memory, 1);

        // build up Engine Group HDL
        this.group_buildHDL(group);
        //  bram.fillEntryValue();
        //generate BRam verilog file and all content
        //bram.buildHDL();
        //  this.bram_buildHDL(bram);
        //  this.bram_buildCOE(bram);
        //  this.bram_buildXCO(bram);
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
    /*
    
     * 
     */
    public void bram_buildCOE(ReEngineGroup group, BlockMemory memory) {
        try {

            memory.BRam = new char[256][memory.width];
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_IPCore + "BRAM_" + group.id + ".coe"));
            bw.write(";");
            System.out.println("bram_buildCOE: memorywidth " + memory.width + " char size " + memory.lchar.size());
            for (int i = memory.lchar.size() - 1; i >= 0; i--) {
                bw.write(memory.lchar.get(i).value + " ");
                for (int j = 0; j < 256; j++) {
                    memory.BRam[j][i] = memory.lchar.get(i).value256[j] ? '1' : '0';
                }
            }
            bw.write("\n");
            bw.write(";Block memory of depth=256, and width=" + memory.width + "\n"
                    + "MEMORY_INITIALIZATION_RADIX=2;\n"
                    + "MEMORY_INITIALIZATION_VECTOR=\n");
            System.out.println("\txxxxxx");
            for (int i = 0; i < 256; i++) {
                for (int j = memory.width - 1; j >= 0; j--) {
                    System.out.print(memory.BRam[i][j]);
                    bw.write(memory.BRam[i][j]);
                }
                System.out.println("");
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

    public void bram_buildXCO(ReEngineGroup group, BlockMemory memory) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_IPCore + "bram_entity_" + group.id + ".xco")));
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
                    + "SET package = ff1152\n"
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
                    + "CSET coefficient_file=" + "BRAM_" + group.id + ".coe\n"
                    + "CSET select_primitive=512x36\n"
                    + "CSET initialization_pin_polarity=Active_High\n"
                    + "CSET global_init_value=0\n"
                    + "CSET depth=256\n"
                    + "CSET write_enable_polarity=Active_High\n"
                    + "CSET port_configuration=Read_Only\n"
                    + "CSET enable_pin_polarity=Active_High\n"
                    + "CSET component_name=" + "bram_entity_" + group.id + "\n"
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
                    + "CSET width=" + memory.width + "\n"
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
        String prefix = "";
        this.buildDflipflop(); // build DFF
        System.out.println(" Builder_v2: engine_buildHDL " + engine.groupID + " : " + engine.listBlockChar.size());
        System.out.println(engine.rule.getRule());
        for (int i = 0; i < engine.listBlockChar.size(); i++) {
            BlockChar bc = engine.listBlockChar.get(i);
            System.out.print(bc.value + "[" + bc.id + "] ");
        }
        System.out.println();
        //get infix
        LinkedList<BlockInfix> linfix = engine.getInfix();
        LinkedList<BlockInfix> lnotsameinfix = this.getreduceinfix(linfix);
        //Create top module HDL code.
        //this.updateBlockStateOrder();//update oder of blockstate;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "engine_" + engine.groupID + "_" + engine.order + ".v"));
            //get number of endstate
            int noEndState = engine.listEndState.size();
            bw.write("module engine_" + engine.groupID + "_" + engine.order + "(out,clk,sod,en");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                //this.listBlockChar.get(i).id = i; //update id for block char
                bw.write(", in_" + engine.listBlockChar.get(i).order);
            }
            //prefix input
            if (engine.getStartState().isPrefix) {
                BlockPrefix pre = (BlockPrefix) engine.getStartState();
                bw.write(",i_pre_" + pre.prefixId);
            }
            //infix input
            for (int i = 0; i < lnotsameinfix.size(); i++) {
                BlockInfix in = lnotsameinfix.get(i);
                bw.write(",i_in_" + in.infixID + ",o_in_" + in.infixID);
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
            if (!engine.listBlockChar.isEmpty()) {
                bw.write("\n\tinput ");
                for (int i = 0; i < engine.listBlockChar.size(); i++) {
                    if (i == 0) {
                        bw.write("in_" + engine.listBlockChar.get(i).order);
                    } else {
                        bw.write(", in_" + engine.listBlockChar.get(i).order);
                    }
                }
                bw.write(";\n");
            }

            //prefix input declare
            if (engine.getStartState().isPrefix) {
                BlockPrefix pre = (BlockPrefix) engine.getStartState();
                bw.write("\tinput i_pre_" + pre.prefixId + ";\n");
            }
            //infix input, out declare
            if (!lnotsameinfix.isEmpty()) {

                bw.write("\tinput ");
                for (int i = 0; i < lnotsameinfix.size(); i++) {
                    BlockInfix in = lnotsameinfix.get(i);
                    bw.write("i_in_" + in.infixID);
                    if (i != lnotsameinfix.size() - 1) {
                        bw.write(",");
                    } else {
                        bw.write(";\n");
                    }
                }

                bw.write("\toutput ");
                for (int i = 0; i < lnotsameinfix.size(); i++) {
                    BlockInfix in = lnotsameinfix.get(i);
                    bw.write("o_in_" + in.infixID);
                    if (i != lnotsameinfix.size() - 1) {
                        bw.write(",");
                    } else {
                        bw.write(";\n");
                    }
                }
            }
            //start end;
            if (noEndState == 1) {
                bw.write("\toutput out;\n\n");
            } else {
                bw.write("\toutput [" + (noEndState - 1) + ":0] out;\n\n");
            }


            //routing here
            //net connect block State
            //START STATE

            if (engine.getStartState().isPrefix) {
                bw.write("\tassign w0 = i_pre_" + ((BlockPrefix) engine.getStartState()).prefixId + ";\n");
            } else {
                bw.write("\tassign w0 = 1'b1" + ";\n");
            }
            //Assign infix state
            while (!linfix.isEmpty()) {
                BlockInfix in = linfix.getFirst();
                LinkedList<BlockInfix> lsameinfix = this.getSameInfix(linfix, in);

                for (int i = 0; i < lsameinfix.size(); i++) {
                    bw.write("\twire w_i_in_" + in.infixID + "_" + lsameinfix.get(i).order + ";\n");
                }
                bw.write("\tassign o_in_" + in.infixID + " = ");
                bw.write("w_i_in_" + in.infixID + "_" + lsameinfix.get(0).order);
                for (int i = 1; i < lsameinfix.size(); i++) {
                    bw.write(" | w_i_in_" + in.infixID + "_" + lsameinfix.get(i).order);
                }
                bw.write(";\n");
            }
            //route other blockState
            int size = engine.listBlockState.size();
            for (int i = 1; i < size; i++) {
                BlockState bt = engine.listBlockState.get(i);
                if (bt.isEnd) {
                    if (noEndState == 1) {
                        bw.write("\tEnd_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockState_" + engine.groupID + "_" + engine.order + "_" + bt.order + " (out,clk,en,sod");
                    } else {
                        //get index on listBlockend
                        int index = engine.listEndState.indexOf(bt);
                        bw.write("\tEnd_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockState_" + engine.groupID + "_" + engine.order + "_" + bt.order + " (out[" + index + "]" + ",clk,en,sod");
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
                    bw.write("\t" + prefix + "BCR_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockState_ConRep_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                } else if (bt.isInfix) {
                    bw.write("\t// Routing infix state\n");
                    BlockInfix in = (BlockInfix) bt;
                    //assign output
                    bw.write("\twire w_o_infix_" + in.infixID + "_" + in.order + ";\n");
                    bw.write("\tassign w_i_in_" + in.infixID + "_" + in.order + " = w" + bt.comming.getFirst().order);
                    for (int j = 1; j < bt.comming.size(); j++) {
                        bw.write("|w" + bt.comming.get(j).order);
                    }
                    bw.write(";\n");
                    // bw.write("\tassign o_in_" + in.infixID + " = "
                    //        + "w_i_in_" + in.infixID + ";\n");
                    //assign input
                    bw.write("\tassign w" + bt.order + " = i_in_" + in.infixID + " & "
                            + "w_o_infix_" + in.infixID + "_" + in.order + ";\n");
                    bw.write("\tInfix_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockInfix_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + "(\n");
                    bw.write("\t\t.i_clk(clk),\n"
                            + "\t\t.i_en(en),\n"
                            + "\t\t.i_rst(sod),\n"
                            + "\t\t.i_infix(w_i_in_" + in.infixID + "_" + in.order + "),\n"
                            + "\t\t.o_infix(w_o_infix_" + in.infixID + "_" + in.order + ")\n");
                    bw.write("\t\t);\n");
                    bw.write("\t// Finish routing infix state\n");
                } else {//normal state.
                    bw.write("\tstate_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + " BlockState_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                    continue;
                }
                if (engine.listBlockState.get(i).isInfix) {
                    BlockInfix bin = (BlockInfix) engine.listBlockState.get(i);
                    this.infix_state_buildHDL(bw, bin, this.rtlCreator.getInfix(engine.groupID, bin.infixID), prefix);
                } else if (engine.listBlockState.get(i).isConRep) {
                    this.crb_buildHDL((BlockConRep) engine.listBlockState.get(i), "");
                } else {
                    this.state_buildHDL(bw, engine.listBlockState.get(i), "");
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

    public void bram_buildTestBench(ReEngineGroup group) {
        //firstly, create testcase
        PCRETestCase pcretestcase = new PCRETestCase();
        for (int i = 0; i < group.lengine.size(); i++) {
            pcretestcase.addPCRE(group.lengine.get(i).rule.getRule(), i);
        }
        pcretestcase.generateSimpleTestcase(2);
        //generate two testcase, remember that each testcase contain n pattern corresponding to size of data.
        TestCase tc = pcretestcase.listTestCase.getFirst();
        this.listTestCase.add(tc);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_testbench + File.separator + "BRAM_" + group.id + "_tb.v")));
            bw.write("`timescale 1ns/1ps\n"
                    + "module bram_test_" + group.id + "_tb_v;\n"
                    + "// Inputs\n"
                    + "\treg clk;\n"
                    + "\treg sod;\n"
                    + "\treg en;\n"
                    + "\treg [7:0] char;\n"
                    + "// Outputs\n"
                    + "\twire [" + (group.lengine.size() - 1) + ":0] out;\n"
                    + "\tinteger fd,i,index;\n"
                    + "// Instantiate the Unit Under Test (UUT)\n"
                    + "\tBRAM_" + group.id + " uut (\n"
                    + "\t\t.out(out),\n"
                    + "\t\t.clk(clk),\n"
                    + "\t\t.sod(sod),\n"
                    + "\t\t.en(en),\n"
                    + "\t\t.char(char)\n"
                    + "\t\t);\n"
                    + "\talways @(out) begin\n"
                    + "\t\tindex = 0;\n"
                    + "\t\tfor(i = 0; i <= " + (group.lengine.size() - 1) + "; i=i+1 )\n"
                    + "\t\t\tif (out[i] == 1)\n"
                    + "\t\t\tbegin\n"
                    + "\t\t\t\tindex = i + 1;\n"
                    + "\t\t\t\t$display(\"%d\",index);\n"
                    + "\t\t\tend\n"
                    + "\t\tif (index == 0)\n"
                    + "\t\t\t$display(\"%d\",index);\n"
                    + "\tend\n"
                    + "\tinitial begin\n"
                    + "\t\tfd = $fopen(\"bram_test_" + group.id + "_tb.out\",\"w\");\n"
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

            for (int i = 0; i < group.lengine.size(); i++) {
                ReEngine temp = group.lengine.get(i);
                bw.write("//" + (i + 1) + "." + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
            }

            bw.write("//---------------------------------------------------\n");
            for (int i = 0; i < group.lengine.size(); i++) {
                ReEngine temp = group.lengine.get(i);
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
                if (i != group.lengine.size() - 1) {
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
            ex.printStackTrace();
        }
    }
    //block state

    /**
     * Build blockStart
     * @param bw
     * @throws java.io.IOException
     */
    public void state_buildHDL_start(BufferedWriter bw, BlockState bt, String prefix) throws IOException {
        bw.write("module " + prefix + "state_" + bt.engine.groupID + "_" + bt.engine.order + "_" + bt.order + "(out1,in1,clk,en,rst");
        bw.write(");\n");
        bw.write("\tinput in1,clk,rst,en");
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        // bw.write("\tmyDff Dff (out1,in1,clk,en,rst);\n");
        bw.write("\tFDCE #("
                + ".INIT(1'b0)"
                + ") FDCE_inst (\n"
                + "\t\t.Q(out1),\n"
                + "\t\t.C(clk),\n"
                + "\t\t.CE(en),\n"
                + "\t\t.CLR(rst),\n"
                + "\t\t.D(in1)\n"
                + ");\n");

        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     * Build block End
     * @param bw
     * @throws java.io.IOException
     */
    public void state_buildHDL_End(BufferedWriter bw, BlockState bt, String prefix) throws IOException {
        int inputsize = bt.comming.size();
        bw.write("module " + prefix + "End_state_" + bt.engine.groupID + "_" + bt.engine.order + "_" + bt.order + "(out1,clk,en,rst");

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
        //bw.write("\tmyDff Dff (out1,w1,clk,en,rst);\n");
        bw.write("\tFDCE #("
                + ".INIT(1'b0)"
                + ") FDCE_inst (\n"
                + "\t\t.Q(out1),\n"
                + "\t\t.C(clk),\n"
                + "\t\t.CE(en),\n"
                + "\t\t.CLR(rst),\n"
                + "\t\t.D(w1)\n"
                + ");\n");


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
    public void state_buildHDL(BufferedWriter bw, BlockState bt, String prefix) throws IOException {
        if (bt.isStart) {
            this.state_buildHDL_start(bw, bt, prefix);
            return;
        }
        if (bt.isEnd) {
            this.state_buildHDL_End(bw, bt, prefix);
            return;
        }

        int inputsize = bt.comming.size();

        bw.write("module " + prefix + "state_" + bt.engine.groupID + "_" + bt.engine.order + "_" + bt.order + "(out1,in_char,clk,en,rst");

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
        //bw.write("\tmyDff Dff (out1,w2,clk,en,rst);\n");
        bw.write("\tFDCE #("
                + ".INIT(1'b0)"
                + ") FDCE_inst (\n"
                + "\t\t.Q(out1),\n"
                + "\t\t.C(clk),\n"
                + "\t\t.CE(en),\n"
                + "\t\t.CLR(rst),\n"
                + "\t\t.D(w2)\n"
                + ");\n");


        bw.write("endmodule\n\n");
        bw.flush();
    }

    public void crb_buildHDL(BlockConRep crb, String prefix) throws IOException {
        HDL_CRB_Generator_v2 gen = new HDL_CRB_Generator_v2(crb);
        gen.genHDL(this.genfolder_verilog, prefix);
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

    private void infix_state_buildHDL(BufferedWriter bw, BlockState bt, Infix in, String prefix) throws IOException {
        int size = in.size - 1;
        // [srl16c]-[srl16c]-[srl16c]-.. -[srl16e]
        int sizesrl16e; //size of last srl16e
        int nosrl16e; //num of srl16c



        sizesrl16e = size % 16;
        nosrl16e = size / 16;

        int a3 = sizesrl16e / 8;
        int a2 = (sizesrl16e % 8) / 4;
        int a1 = (sizesrl16e % 4) / 2;
        int a0 = (sizesrl16e % 2);

        bw.write("module " + prefix + "Infix_state_" + bt.engine.groupID + "_" + bt.engine.order + "_" + bt.order + "(\n"
                + "\toutput o_infix,\n"
                + "\tinput i_infix,i_clk,i_en,i_rst);\n");
        bw.write("// size: " + size + "\n");
        //route srl16c
        bw.write("\tassign w0 = i_infix;\n");
        for (int i = 0; i < nosrl16e; i++) {
            bw.write("\tSRL16 #(\n"
                    + "\t\t.INIT(16'h0000) // Initial Value of Shift Register\n"
                    + "\t\t) SRL16_inst" + i + " (\n"
                    + "\t\t.Q(w" + (i + 1) + "), // SRL data output\n"
                    + "\t\t.A0(1), // Select[0] input\n"
                    + "\t\t.A1(1), // Select[1] input\n"
                    + "\t\t.A2(1), // Select[2] input\n"
                    + "\t\t.A3(1), // Select[3] input\n"
                    + "\t\t.CLK(i_clk), // Clock input\n"
                    + "\t\t.D(w" + i + ") // SRL data input\n"
                    + "\t\t);\n");

        }
        //last SRL16;
        bw.write("\tSRL16 #(\n"
                + "\t.INIT(16'h0000) // Initial Value of Shift Register\n"
                + "\t) SRL16_inst" + nosrl16e + " (\n"
                + "\t.Q(o_infix), // SRL data output\n"
                + "\t.A0(" + a0 + "), // Select[0] input\n"
                + "\t.A1(" + a1 + "), // Select[1] input\n"
                + "\t.A2(" + a2 + "), // Select[2] input\n"
                + "\t.A3(" + a3 + "), // Select[3] input\n"
                + "\t.CLK(i_clk), // Clock input\n"
                + "\t.D(w" + nosrl16e + ") // SRL data input\n"
                + "\t);\n");

        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     *
     * @param memory
     * @param type
     * type =0; LUTbased
     * type =1; Brambased;
     * type =2; decoder;
     */
    private void memory_buildHDL(ReEngineGroup group, BlockMemory memory, int type) {
        if (type == 0) {
            this.memory_LUTBased_HDL(group, memory);
        } else if (type == 1) {
            this.memory_BramBased_HDL(group, memory);
        } else if (type == 2) {
            this.memory_Decoder_HDL(group, memory);
        }
    }

    private void memory_BramBased_HDL(ReEngineGroup group, BlockMemory memory) {
        this.memory_BramBased_buildHDL(group, memory);
        this.bram_buildCOE(group, memory);
        this.bram_buildXCO(group, memory);
    }

    private void memory_BramBased_buildHDL(ReEngineGroup group, BlockMemory memory) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_verilog + "CentralizedCharacter_" + group.id + ".v")));
            bw.write("module CentralizedCharacter_" + group.id + "(o_match,clk,sod,eod,en,i_char);\n");
            bw.write("\tinput clk, sod,eod, en;\n");
            bw.write("\tinput [7:0] i_char;\n");

            bw.write("\toutput [" + (memory.width - 1) + ":0] o_match;\n");
            bw.write("\twire [" + (memory.width - 1) + ":0] o_ram;\n");
            //BRAM declare
            //assume that output of single port ram is q_out[width - 1 : 0]
            bw.write("\n//BRAM declare \n");
            bw.write("\tbram_entity_" + group.id + " ram (\n"
                    + "\t\t.addr(i_char),\n"
                    + "\t\t.clk(clk),\n"
                    + "\t\t.dout(o_ram),\n"
                    + "\t\t.en(en));\n");
            // hadle ^ and $
            bw.write("\talways@(posedge clk)\n"
                    + "\tbegin\n"
                    + "\t\tif(sod)\n"
                    + "\t\t\tsod_trigger <= 1;\n"
                    + "\t\telse\n"
                    + "\t\t\tsod_trigger <= 0;\n"
                    + "\t\tif(eod)\n"
                    + "\t\t\teod_trigger <=1;\n"
                    + "\t\telse\n"
                    + "\t\t\teod_trigger <=0;\n"
                    + "\tend\n");

            for (int i = 0; i < group.memory.lchar.size(); i++) {
                BlockChar ch = group.memory.lchar.get(i);
                if (ch.isStartChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = sod_trigger | o_ram[" + ch.order + "];\n");
                } else if (ch.isEndChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = eod_trigger | o_ram[" + ch.order + "];\n");
                } else {
                    bw.write("\tassign o_match[" + ch.order + "] = o_ram[" + ch.order + "];\n");
                }
            }
            bw.write("endmodule\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void group_buildHDL(ReEngineGroup group) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_verilog + "Group_" + group.id + ".v")));
            bw.write("module Group_" + group.id + "(out,clk,sod,eod,en,char,rst);\n");
            bw.write("\tinput clk, sod, en, eod,rst;\n");
            bw.write("\tinput [7:0] char;\n");

            //get real number of pcre on Bram
            int noPcre = 0;
            for (int i = 0; i < group.lengine.size(); i++) {
                noPcre += group.lengine.get(i).listEndState.size();
            }
            //

            bw.write("\toutput [" + (noPcre - 1) + ":0] out;\n");
            bw.write("\twire [" + (group.memory.width - 1) + ":0] q_out;\n");
            //BRAM declare
            //assume that output of single port ram is q_out[width - 1 : 0]
            bw.write("\n//Centranlized block declare \n");
            bw.write("\tCentralizedCharacter_" + group.id + " ram (\n"
                    + "\t\t.sod(sod),\n"
                    + "\t\t.eod(eod),\n"
                    + "\t\t.i_char(char),\n"
                    + "\t\t.clk(clk),\n"
                    + "\t\t.o_match(q_out),\n"
                    + "\t\t.en(en));\n");
            bw.write("//Finish centranlized block declare \n");
            //end of bram
            //prefix
            if (!group.lprefix.isEmpty()) {
                bw.write("\n//Prefix declare \n");
                for (int i = 0; i < group.lprefix.size(); i++) {
                    Prefix pre = group.lprefix.get(i);

                    bw.write("\tprefix_" + group.id + "_" + pre.prefixID + " prex_" + group.id + "_" + pre.prefixID + " (\n"
                            + "\t\t.out(w_prefix_" + pre.prefixID + "),\n"
                            + "\t\t.clk(clk),\n"
                            + "\t\t.sod(sod),\n");

                    for (int j = 0; j < pre.listBlockChar.size(); j++) {
                        //this.listBlockChar.get(i).id = i; //update id for block char
                        bw.write("\t\t.in_" + pre.listBlockChar.get(j).order + "(q_out[" + pre.listBlockChar.get(j).order + "]),\n");
                    }

                    bw.write("\t\t.en(en));\n");

                }
                bw.write("//Finish Prefix declare \n");
            }

            //end prefix

            //infix
            if (!group.linfix.isEmpty()) {
                bw.write("\n//Infix declare \n");
                for (int i = 0; i < group.linfix.size(); i++) {
                    Infix in = group.linfix.get(i);
                    //declare 
                    bw.write("\twire ");
                    //check to or all engine with infix output
                    for (int j = 0; j < group.lengine.size(); j++) {
                        boolean contain = false;
                        LinkedList<BlockInfix> lin = group.lengine.get(j).getInfix();
                        for (int k = 0; k < lin.size(); k++) {
                            if (lin.get(k).infixID == in.infixID) {
                                contain = true;
                            }
                        }
                        if (contain) {
                            bw.write("w_i_infix_" + in.infixID + "_" + group.lengine.get(j).order + ", ");
                        }
                    }
                    bw.write("w_i_infix_" + in.infixID + ";\n");

                    //assign
                    bw.write("\tassign w_i_infix_" + in.infixID + " = ");
                    //check to or all engine with infix output
                    for (int j = 0; j < group.lengine.size(); j++) {
                        boolean contain = false;
                        LinkedList<BlockInfix> lin = group.lengine.get(j).getInfix();
                        for (int k = 0; k < lin.size(); k++) {
                            if (lin.get(k).infixID == in.infixID) {
                                contain = true;
                            }
                        }
                        if (contain) {
                            bw.write("w_i_infix_" + in.infixID + "_" + group.lengine.get(j).order + " | ");
                        }
                    }
                    bw.write("0;\n");

                    bw.write("\tinfix_" + group.id + "_" + in.infixID + " in_" + group.id + "_" + in.infixID + "(\n"
                            + "\t\t.clk(clk),\n"
                            + "\t\t.sod(sod),\n"
                            + "\t\t.i_infix(w_i_infix_" + in.infixID + "),\n"
                            + "\t\t.o_infix(w_o_infix_" + in.infixID + "),\n");
                    for (int j = 0; j < in.listBlockChar.size(); j++) {
                        //this.listBlockChar.get(i).id = i; //update id for block char
                        bw.write("\t\t.in_" + in.listBlockChar.get(j).order + "(q_out[" + in.listBlockChar.get(j).order + "]),\n");
                    }

                    bw.write("\t\t.en(en));\n");

                }
                bw.write("//Finish Infix declare \n");
            }
            //end infix



            //declare engine
            bw.write("\n//Pcre Engine declare \n");
            int index = 0;
            for (int i = 0; i < group.lengine.size(); i++) {
                //module engine_0_1(out,clk,sod,en, in_9, in_10, in_11, in_12,i_pre_0,i_in_1,o_in_1);
                //currently, bram support engine with multi end state, so need to
                //sperate upper index and lower index of ouput of each engine.
                int lindex = index;
                int uindex = index + group.lengine.get(i).listEndState.size() - 1;
                index = uindex + 1;
                if (lindex == uindex) {
                    bw.write("\tengine_" + group.id + "_" + group.lengine.get(i).order + " engine_" + group.id + "_" + group.lengine.get(i).order + "(\n"
                            + "\t\t.out(out[" + uindex + "]), \n"
                            + "\t\t.clk(clk), \n"
                            + "\t\t.sod(sod), \n"); // thieu char
                } else {
                    bw.write("\tengine_" + group.id + "_" + group.lengine.get(i).order + " engine_" + group.id + "_" + group.lengine.get(i).order + "(\n"
                            + "\t\t.out(out[" + uindex + ":" + lindex + "]), \n"
                            + "\t\t.clk(clk), \n"
                            + "\t\t.sod(sod), \n"); // thieu char
                }
                ReEngine te = group.lengine.get(i);
                //route prefix
                if (te.listBlockState.getFirst().isPrefix) {
                    BlockPrefix pre = (BlockPrefix) te.listBlockState.getFirst();
                    bw.write("\t\t.i_pre_" + pre.prefixId + "(w_prefix_" + pre.prefixId + "),\n");
                }
                //route infix

                LinkedList<BlockInfix> lin = te.getInfix();
                lin = this.getreduceinfix(lin);
                if (!lin.isEmpty()) {
                    for (int k = 0; k < lin.size(); k++) {
                        BlockInfix in = lin.get(k);
                        bw.write("\t\t.i_in_" + in.infixID + "(w_o_infix_" + in.infixID + "),\n");
                        bw.write("\t\t.o_in_" + in.infixID + "(w_i_infix_" + in.infixID + "_" + te.order + "),\n");
                    }
                }
                //route char
                //routing to each engine
                for (int j = 0; j < te.listBlockChar.size(); j++) {
                    bw.write("\t\t .in_" + te.listBlockChar.get(j).order + "(q_out[" + te.listBlockChar.get(j).order + "]),\n");
                }
                bw.write("\t\t.en(en));\n");
            }
            bw.write("\n//Finish PCRE engine declare \n");

            bw.write("\n");
            bw.write("endmodule\n");
            bw.flush();
            bw.close();



        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void memory_LUTBased_HDL(ReEngineGroup group, BlockMemory memory) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_verilog + "CentralizedCharacter_" + group.id + ".v")));
            bw.write("module CentralizedCharacter_" + group.id + "(o_match,clk,sod,eod,en,i_char);\n");
            bw.write("\tinput clk, sod,eod, en;\n");
            bw.write("\tinput [7:0] i_char;\n");

            bw.write("\toutput [" + (memory.width - 1) + ":0] o_match;\n");
            bw.write("\twire [" + (memory.width - 1) + ":0] w_char;\n");

            // hadle ^ and $
            for (int i = 0; i < group.memory.lchar.size(); i++) {
                BlockChar ch = group.memory.lchar.get(i);
                if (ch.isStartChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = sod & w_char[" + ch.order + "];\n");
                } else if (ch.isEndChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = eod & w_char[" + ch.order + "];\n");
                } else {
                    bw.write("\tassign o_match[" + ch.order + "] = w_char[" + ch.order + "];\n");
                }
            }
            //routing block char
            for (int i = 0; i < group.memory.lchar.size(); i++) {
                BlockChar ch = group.memory.lchar.get(i);
                bw.write("\tblockchar_" + ch.order + " char_" + ch.order + "(\n"
                        + "\t\t.in(i_char),\n"
                        + "\t\t.out(w_char[" + ch.order + "])\n"
                        + "\t\t);\n");
            }

            bw.write("endmodule\n");

            for (int i = 0; i < group.memory.lchar.size(); i++) {
                this.buildLUTChar(group.memory.lchar.get(i), bw);
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buildLUTChar(BlockChar get, BufferedWriter bw) throws IOException {
        /**module char1(
        input [3:0] in,
        output out
        );
        parameter [15:0] a = 16'b0000_0000_1111_0000;
        
        assign out = a[in];
        
        endmodule*/
        bw.write("\nmodule blockchar_" + get.order + " ( \n"
                + "\tinput [7:0] in,\n"
                + "\toutput out\n"
                + "\t);\n");
        bw.write("\tparameter [255:0] array = 256'b");
        for (int i = 255; i >= 0; i--) {
            if (get.value256[i]) {
                bw.write("1");
            } else {
                bw.write("0");
            }
        }
        bw.write(";\n");
        bw.write("\tassign out = array[in];\n");
        bw.write("endmodule\n");
    }

    private void prefix_buildHDL(Prefix engine) {
        String prefix = "pre_";
        this.buildDflipflop(); // build DFF
        System.out.println(" Builder_v2: prefix_buildHDL " + engine.groupID + " : " + engine.listBlockChar.size());
        System.out.println(engine.rule.getRule());
        for (int i = 0; i < engine.listBlockChar.size(); i++) {
            BlockChar bc = engine.listBlockChar.get(i);
            System.out.print(bc.value + "[" + bc.id + "] ");
        }
        System.out.println();
        LinkedList<BlockInfix> linfix = engine.getInfix();
        //Create top module HDL code.
        //this.updateBlockStateOrder();//update oder of blockstate;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "prefix_" + engine.groupID + "_" + engine.prefixID + ".v"));
            //get number of endstate
            int noEndState = engine.listEndState.size();
            bw.write("module prefix_" + engine.groupID + "_" + engine.order + "(out,clk,sod,en");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                //this.listBlockChar.get(i).id = i; //update id for block char
                bw.write(", in_" + engine.listBlockChar.get(i).order);
            }
            //prefix input
            if (engine.getStartState().isPrefix) {
                BlockPrefix pre = (BlockPrefix) engine.getStartState();
                bw.write(",i_pre_" + pre.prefixId);
            }
            //infix input
            for (int i = 0; i < linfix.size(); i++) {
                BlockInfix in = linfix.get(i);
                bw.write(",i_in_" + in.infixID + ",o_in_" + in.infixID);
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

            //prefix input declare
            if (engine.getStartState().isPrefix) {
                BlockPrefix pre = (BlockPrefix) engine.getStartState();
                bw.write("\tinput i_pre_" + pre.prefixId + ";\n");
            }
            //infix input, out declare
            if (!linfix.isEmpty()) {
                bw.write("\tinput ");
                for (int i = 0; i < linfix.size(); i++) {
                    BlockInfix in = linfix.get(i);
                    bw.write("i_in_" + in.infixID);
                    if (i != linfix.size() - 1) {
                        bw.write(",");
                    } else {
                        bw.write(";\n");
                    }
                }

                bw.write("\toutput ");
                for (int i = 0; i < linfix.size(); i++) {
                    BlockInfix in = linfix.get(i);
                    bw.write("o_in_" + in.infixID);
                    if (i != linfix.size() - 1) {
                        bw.write(",");
                    } else {
                        bw.write(";\n");
                    }
                }
            }
            //start end;
            if (noEndState == 1) {
                bw.write("\toutput out;\n\n");
            } else {
                bw.write("\toutput [" + (noEndState - 1) + ":0] out;\n\n");
            }


            //routing here
            //net connect block State
            //START STATE

            if (engine.getStartState().isPrefix) {
                bw.write("\tassign w0 = i_pre_" + ((BlockPrefix) engine.getStartState()).prefixId + ";\n");
            } else {
                bw.write("\tassign w0 = 1'b1" + ";\n");
            }
            //route other blockState
            int size = engine.listBlockState.size();
            for (int i = 1; i < size; i++) {
                BlockState bt = engine.listBlockState.get(i);
                if (bt.isEnd) {
                    bw.write("\tassign out = w" + bt.comming.getFirst().order);
                    for (int j = 1; j < bt.comming.size(); j++) {
                        bw.write("| w" + bt.comming.get(j).order);
                    }
                    bw.write(";\n");
                } else if (bt.isConRep) {
                    //TODO
                    //module blockContraint_0_id (out, in, clk);
                    BlockConRep btc = (BlockConRep) bt;
                    //(out,i_char_1,i_char_2,i_clk,i_en,i_rst,in0);
                    bw.write("\t" + prefix + "BCR_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockState_ConRep_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                } else if (bt.isInfix) {
                    BlockInfix in = (BlockInfix) bt;
                    //assign output
                    //assign output
                    bw.write("\twire w_o_infix_" + in.infixID + ";\n");
                    bw.write("\tassign w_i_in_" + in.infixID + " = w" + bt.comming.getFirst().order);
                    for (int j = 1; j < bt.comming.size(); j++) {
                        bw.write("| w" + bt.comming.get(j).order);
                    }
                    bw.write(";\n");
                    bw.write("\tassign o_in_" + in.infixID + " = "
                            + "w_i_in_" + in.infixID + ";\n");
                    //assign input
                    bw.write("\tassign w" + bt.order + " = i_in_" + in.infixID + " & "
                            + "w_o_infix_" + in.infixID + ";\n");
                    bw.write("\tInfix_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockInfix_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + "(\n");
                    bw.write("\t\t.i_clk(clk),\n"
                            + "\t\t.i_en(en),\n"
                            + "\t\t.i_rst(sod),\n"
                            + "\t\t.i_infix(w_i_in_" + in.infixID + "),\n"
                            + "\t\t.o_infix(w_o_infix_" + in.infixID + ")\n");
                    bw.write("\t\t);\n");
                } else {//normal state.
                    bw.write("\t" + prefix + "state_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + " BlockState_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                    continue;
                }
                //not need endstate in prefix
                if (engine.listBlockState.get(i).isEnd) {
                    continue;
                }
                if (engine.listBlockState.get(i).isInfix) {
                    BlockInfix bin = (BlockInfix) engine.listBlockState.get(i);
                    this.infix_state_buildHDL(bw, bin, this.rtlCreator.getInfix(engine.groupID, bin.infixID), prefix);
                } else if (engine.listBlockState.get(i).isConRep) {
                    this.crb_buildHDL((BlockConRep) engine.listBlockState.get(i), prefix);
                } else {
                    this.state_buildHDL(bw, engine.listBlockState.get(i), prefix);
                }
            }
            bw.flush();
            bw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void infix_buildHDL(Infix engine) {
        String prefix = "in_";
        this.buildDflipflop(); // build DFF
        System.out.println(" Builder_v2: Infix_buildHDL " + engine.groupID + " : " + engine.listBlockChar.size());
        System.out.println(engine.rule.getRule());
        for (int i = 0; i < engine.listBlockChar.size(); i++) {
            BlockChar bc = engine.listBlockChar.get(i);
            System.out.print(bc.value + "[" + bc.id + "] ");
        }
        System.out.println();

        //Create top module HDL code.
        //this.updateBlockStateOrder();//update oder of blockstate;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "infix_" + engine.groupID + "_" + engine.infixID + ".v"));
            //get number of endstate
            int noEndState = engine.listEndState.size();
            bw.write("module infix_" + engine.groupID + "_" + engine.order + "(clk,sod,en,i_infix,o_infix");
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
            bw.write("\n\tinput clk,sod,en,i_infix;\n");
            bw.write("\n\tinput ");
            for (int i = 0; i < engine.listBlockChar.size(); i++) {
                if (i == 0) {
                    bw.write("in_" + engine.listBlockChar.get(i).order);
                } else {
                    bw.write(", in_" + engine.listBlockChar.get(i).order);
                }
            }
            bw.write(";\n");

            //state end;

            bw.write("\toutput o_infix;\n\n");

            //routing here
            //net connect block State
            //START STATE

            bw.write("\tassign w0 = i_infix" + ";\n");

            //route other blockState
            int size = engine.listBlockState.size();
            for (int i = 1; i < size; i++) {
                BlockState bt = engine.listBlockState.get(i);
                if (bt.isEnd) {
                    bw.write("\tassign o_infix = w" + bt.comming.getFirst().order);
                    for (int j = 1; j < bt.comming.size(); j++) {
                        bw.write("| w" + bt.comming.get(j).order);
                    }
                    bw.write(";\n");
                } else if (bt.isConRep) {
                    //TODO
                    //module blockContraint_0_id (out, in, clk);
                    BlockConRep btc = (BlockConRep) bt;
                    //(out,i_char_1,i_char_2,i_clk,i_en,i_rst,in0);
                    bw.write("\t" + prefix + "BCR_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockState_ConRep_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                } else if (bt.isInfix) {
                    BlockInfix in = (BlockInfix) bt;
                    //assign output
                    //assign output
                    bw.write("\twire w_o_infix_" + in.infixID + ";\n");
                    bw.write("\tassign w_i_in_" + in.infixID + " = w" + bt.comming.getFirst().order);
                    for (int j = 1; j < bt.comming.size(); j++) {
                        bw.write("| w" + bt.comming.get(j).order);
                    }
                    bw.write(";\n");
                    bw.write("\tassign o_in_" + in.infixID + " = "
                            + "w_i_in_" + in.infixID + ";\n");
                    //assign input
                    bw.write("\tassign w" + bt.order + " = i_in_" + in.infixID + " & "
                            + "w_o_infix_" + in.infixID + ";\n");
                    bw.write("\tInfix_state_" + engine.groupID + "_" + engine.order + "_" + bt.order + " BlockInfix_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + "(\n");
                    bw.write("\t\t.i_clk(clk),\n"
                            + "\t\t.i_en(en),\n"
                            + "\t\t.i_rst(sod),\n"
                            + "\t\t.i_infix(w_i_in_" + in.infixID + "),\n"
                            + "\t\t.o_infix(w_o_infix_" + in.infixID + ")\n");
                    bw.write("\t\t);\n");
                } else {//normal state.
                    bw.write("\t" + prefix + "state_" + engine.groupID + "_" + engine.order + "_" + bt.order
                            + " BlockState_" + engine.groupID + "_" + engine.order + "_" + bt.order
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
                    continue;
                }
                //not need endstate in infix
                if (engine.listBlockState.get(i).isEnd) {
                    continue;
                }
                if (engine.listBlockState.get(i).isInfix) {
                    BlockInfix bin = (BlockInfix) engine.listBlockState.get(i);
                    this.infix_state_buildHDL(bw, bin, this.rtlCreator.getInfix(engine.groupID, bin.infixID), prefix);
                } else if (engine.listBlockState.get(i).isConRep) {
                    this.crb_buildHDL((BlockConRep) engine.listBlockState.get(i), prefix);
                } else {
                    this.state_buildHDL(bw, engine.listBlockState.get(i), prefix);
                }
            }
            bw.flush();
            bw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private LinkedList<BlockInfix> getSameInfix(LinkedList<BlockInfix> linfix, BlockInfix in) {
        LinkedList<BlockInfix> ret = new LinkedList<BlockInfix>();
        for (int i = 0; i < linfix.size();) {
            BlockInfix t = linfix.get(i);
            if (t.infixID == in.infixID) {
                ret.add(t);
                linfix.remove(i);
            } else {
                i++;
            }
        }
        return ret;
    }

    private LinkedList<BlockInfix> getreduceinfix(LinkedList<BlockInfix> linfix) {
        LinkedList<BlockInfix> ret = new LinkedList<BlockInfix>();
        if (!linfix.isEmpty()) {
            ret.add(linfix.getFirst());
            for (int i = 1; i < linfix.size(); i++) {
                boolean same = false;
                for (int j = 0; j < ret.size(); j++) {
                    if (ret.get(j).infixID == linfix.get(i).infixID) {
                        same = true;
                        break;
                    }
                }
                if (!same) {
                    ret.add(linfix.get(i));
                }
            }

        }
        return ret;
    }

    public void genHDL(int chartype) {
        //Create folder tree
        this.createFolderTree();
        //Generate Each BRAM
        for (int i = 0; i < this.rtlCreator.lsGroup.size(); i++) {
            System.out.println("\n\n HDL_Generator_v2: GenHDL REGroup " + i + "\n");
            this.genHDLGroup(i, chartype);
        }
        //Generate top engine
        this.genHDLPCREMatchingEngine();
    }

    private void genHDLGroup(int i, int chartype) {
        ReEngineGroup group = this.rtlCreator.lsGroup.get(i);

        System.out.println("genHDLGroup " + i + " " + group.id + ": \n"
                + "\t\tno prefix: " + group.lprefix.size() + "\n"
                + "\t\tno infix : " + group.linfix.size() + "\n"
                + "\t\tno engine: " + group.lengine.size() + "\n");
        //generate prefix

        for (int j = 0; j < group.lprefix.size(); j++) {
            this.prefix_buildHDL(group.lprefix.get(j));
        }
        //generate infix
        for (int j = 0; j < group.linfix.size(); j++) {
            this.infix_buildHDL(group.linfix.get(j));
        }

        //generate each engine
        for (int j = 0; j < group.lengine.size(); j++) {
            this.engine_buildHDL(group.lengine.get(j));
        }
        // build up Block BRam memory
        this.memory_buildHDL(group, group.memory, chartype);

        // build up Engine Group HDL
        this.group_buildHDL(group);
        //  bram.fillEntryValue();
        //generate BRam verilog file and all content
        //bram.buildHDL();
        //  this.bram_buildHDL(bram);
        //  this.bram_buildCOE(bram);
        //  this.bram_buildXCO(bram);
    }

    public void outstatistic() {
        //num of char. num of reduce char
        System.out.println("outstatistic: print charsize");
        rtlCreator.printCharSize();
    }

    private void memory_Decoder_HDL(ReEngineGroup group, BlockMemory memory) {
        this.build8_256Decoder();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.genfolder_verilog + "CentralizedCharacter_" + group.id + ".v")));
            bw.write("module CentralizedCharacter_" + group.id + "(o_match,clk,sod,eod,en,i_char);\n");
            bw.write("\tinput clk, sod,eod, en;\n");
            bw.write("\tinput [7:0] i_char;\n");

            bw.write("\toutput [" + (memory.width - 1) + ":0] o_match;\n");
            bw.write("\twire [" + (memory.width - 1) + ":0] w_char;\n");
            bw.write("\twire [255:0] w_decoder;\n");

            bw.write("\tDecoder8_256 decoder(\n"
                    + "\t\t.i_char(i_char),\n"
                    + "\t\t.o_decoder(w_decoder)\n"
                    + "\t\t);\n");

            // hadle ^ and $
            for (int i = 0; i < group.memory.lchar.size(); i++) {
                BlockChar ch = group.memory.lchar.get(i);
                if (ch.isStartChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = sod & w_char[" + ch.order + "];\n");
                } else if (ch.isEndChar()) {
                    bw.write("\tassign o_match[" + ch.order + "] = eod & w_char[" + ch.order + "];\n");
                } else {
                    bw.write("\tassign o_match[" + ch.order + "] = w_char[" + ch.order + "];\n");
                }
            }
            //routing block char
            for (int i = 0; i < group.memory.lchar.size(); i++) {
                BlockChar ch = group.memory.lchar.get(i);
                bw.write("\tassign w_char[" + ch.order + "] = 1'b0");
                for (int j = 0; j < ch.value256.length; j++) {
                    if (ch.value256[j]) {
                        bw.write(" | w_decoder[" + j + "]");
                    }
                }
                bw.write(";\n");
            }

            bw.write("endmodule\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void build8_256Decoder() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "Decoder8_256.v"));

            bw.write("module Decoder8_256 (\n"
                    + "\t\tinput [7:0] i_char,\n"
                    + "\t\toutput [255:0] o_decoder);\n"
                    + "\tassign o_decoder = 1<< i_char;\n"
                    + "endmodule\n");

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(HDL_Generator_v2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void genHDLPCREMatchingEngine() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder_verilog + "PCRE_Engine_Matching.v"));

            bw.write(" module PCRE_Engine_Matching\n"
                    + "\t#(\n"
                    + "\tparameter NO_PCRE = 215\n"
                    + "\t)\n"
                    + "\t(\n"
                    + "\t//input\n"
                    + "\tinput clk, iEn, iSod, iEod,iRst,\n"
                    + "\tinput [7:0] iData,\n"
                    + "\t//output\n"
                    + "\toutput [NO_PCRE -1 : 0 ] oPcre\n"
                    + "\t);\n");
            //todo
            bw.write("\tGroup_0 group0(\n"
                    + "\t.clk(clk),\n"
                    + "\t.rst(iRst),\n"
                    + ".sod(iSod),\n"
                    + ".eod(iEod),\n"
                    + ".en(iEn),\n"
                    + ".char(iData),\n"
                    + ".out(oPcre)\n"
                    + ");\n"
                    + "endmodule\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(HDL_Generator_v2.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
