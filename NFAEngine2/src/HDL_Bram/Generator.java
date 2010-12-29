/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HDL_Bram;

import BRAM.BRAM;
import NFA.NFA;
import ParseTree.ParseTree;
import RegexEngine.ReEngine;
import TestPattern.PCRETestCase;
import TestPattern.Pattern;
import TestPattern.TestCase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class Generator {

    public LinkedList<LinkedList<String>> listRule;
    LinkedList<BRAM> listBram;
    public String genfolder = System.getProperty("user.dir") + File.separator + "GenHDL" + File.separator;
    public String pcrefile;

    public static void main(String[] args) throws Exception {
        Generator gen = new Generator();
        gen.doAction();
    }

    public void doAction() throws Exception {
        String filename = "bram.input.11.pcre";
        String inputfile = System.getProperty("user.dir") + File.separator + filename;
        //Read from file
        this.readFromFile(inputfile);
        //process data
        this.processData();
        //Generate HDL.
        this.generateHDL();
        //Generate Testbench.
        this.GenTestBenchv01();
        //this.generateTestbench(); //testcase ...
        //this.generateTestRam();
    }

    /**
     * After this step listrule will contain all pcre from the file
     * @param filename
     */
    public void readFromFile(String filename) {
        this.listRule = new LinkedList<LinkedList<String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            LinkedList<String> lpcre = null;

            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty()) {
                    continue;
                }

                if (s.startsWith("#bram")) {
                    if (lpcre != null) {
                        this.listRule.add(lpcre);
                    }
                    lpcre = new LinkedList<String>();
                    continue;
                }
                s = s + 'i'; //todo all is case sensitive
                lpcre.add(s);
            }
            if (lpcre != null) {
                this.listRule.add(lpcre);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * We have enough neccessary file to create HDL file.
     * @throws Exception
     */
    private void generateHDL() throws Exception {
        //Generate Each BRAM
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = this.listBram.get(i);
            //generate each engien
            for (int j = 0; j < bram.engineList.size(); j++) {
                bram.engineList.get(j).buildHDL(genfolder);
            }
            //generate Bram.v
            this.generateHDLBram(bram);
        }
        //Generate Top Engine;
        Generator.doBuildInterfacer(genfolder);
        Generator.createTopEngineTogether(genfolder, this.listBram);
    }

    public void generateBramTestBench() {
        //Generate testbench.
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = this.listBram.get(i);
            //generate each engien
            this.buildTestBench(genfolder, bram);
            //this.buildTestBench_1(genfolder, bram);
        }
    }

    private void generateHDLBram(BRAM bram) {
        bram._outputFolder = this.genfolder;
        bram.fillEntryValue();
        bram.buildHDL();
        bram.buildCOE();
        //this.buildCORE_RAM_HDL();
        bram.buildXCO();
    }

    public static void createTopEngineTogether(String folder, LinkedList<BRAM> bramList) {
        int noEngine = 0;
        for (int i = 0; i < bramList.size(); i++) {
            noEngine += bramList.get(i).engineList.size();
        }
        try {
            Generator.doBuildInterfacer(folder);
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(folder + "top_engine.v"));
            bw.write("module top_engine(out,stop,clk_in,sod,en,char,eod);\n");
            bw.write("\tinput [7:0] char;\n\tinput clk_in,sod,en,eod;\n");
            bw.write("\toutput stop;\n ");
            bw.write("\twire [7:0] char_int;\n\twire en_int;\n");
            bw.write("\toutput [" + (noEngine - 1) + ":0] out;\n\n");
            bw.write("\tassign clk = ~clk_in;\n");
            bw.write("\tinterfacer I1(stop,char_int,en_int,en,char,sod,eod,clk);\n");
            int offset = 0;
            for (int j = 0; j < bramList.size(); j++) {
                BRAM temp = bramList.get(j);
                offset = offset + temp.engineList.size();
                bw.write("\tBRAM_" + temp.ID + " blockram_" + temp.ID + " (out[" + (offset - 1) + ":" + (offset - temp.engineList.size()) + "],clk,sod,en_int,char_int);\n");
            }

            bw.write("\nendmodule\n");
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void doBuildInterfacer(String folder) throws Exception {

        BufferedWriter bw = new BufferedWriter(new FileWriter(folder + "interfacer.v"));

        bw.write("module interfacer(stop,data,en,en_in,data_in,sod,eod,clk);\n"
                + "\tinput en_in,eod,clk,sod;\n"
                + "\tinput [7:0] data_in;\n"
                + "\toutput [7:0] data;\n"
                + "\toutput  stop,en;\n"
                + "\twire [8:0] buffer;\n"
                + "\twire [8:0] tembreap,temp1;\n"
                + "\twire sod_0,sod_1;\n\n"
                + "\tassign data = buffer[7:0];\n"
                + "\tor(en,sod_1,stop,buffer[8]);\n"
                + "\tdelay_1 de_1(sod_0,sod,clk);\n"
                + "\tdelay_1 de_2(sod_1,sod_0,clk);\n"
                + "\tcountforstop c1(stop,clk,sod,eod);\n"
                + "\tdelay de1(temp,{en_in,data_in},clk);\n"
                + "\tdelay de2(buffer,temp,clk);\n"
                + "\t//delay de3(buffer,temp1,clk);\n"
                + "endmodule\n\n"
                + "module delay(out,in,clk);\n"
                + "\tinput [8:0] in;\n"
                + "\toutput [8:0] out;\n"
                + "\tinput clk;\n"
                + "\treg [8:0] out;\n\n"
                + "\talways @(negedge clk)\n"
                + "\t\tbegin\n"
                + "\t\t\tout <= in;\n"
                + "\t\tend\n"
                + "endmodule\n\n"
                + "module delay_1(out,in,clk);\n"
                + "\tinput [0:0] in;\n"
                + "\toutput [0:0] out;\n"
                + "\tinput clk;\n"
                + "\treg [0:0] out;\n\n"
                + "\talways @(negedge clk)\n"
                + "\t\tbegin"
                + "\t\t\tout <= in;\n"
                + "\t\tend\n"
                + "endmodule\n\n"
                + "module countforstop(out,clk,rst_out,rst);\n"
                + "\tinput clk,rst,rst_out;\n"
                + "\toutput out;\n"
                + "\twire d_in;\n"
                + "\treg out;\n"
                + "\treg [2:0] count;\n"
                + "\tor(d_in,rst,out);\n"
                + "\talways @(posedge clk)\n"
                + "\t\tbegin\n"
                + "\t\t\tif(rst)\n"
                + "\t\t\t\tbegin\n"
                + "\t\t\t\t\tcount <= 3'b001;\n"
                + "\t\t\t\tend\n"
                + "\t\t\telse if(out == 1'b1)\n"
                + "\t\t\t\tbegin\n"
                + "\t\t\t\t\tcount <= count +1;\n"
                + "\t\t\t\tend\n"
                + "\t\t\telse\n"
                + "\t\t\t\tbegin\n"
                + "\t\t\t\t\tcount <= count;\n"
                + "\t\t\t\tend\n"
                + "\t\tend\n"
                + "\talways @(posedge clk)\n"
                + "\t\tbegin\n"
                + "\t\t\tif(rst_out)\n"
                + "\t\t\t\tout <= 1'b0;\n"
                + "\t\t\telse if(rst)\n"
                + "\t\t\t\tout <= 1'b1;\n"
                + "\t\t\telse if(count >= 3'b100)\n"
                + "\t\t\t\tout <= 1'b0;\n"
                + "\t\t\telse\n"
                + "\t\t\t\tout <= d_in;\n"
                + "\t\tend\n"
                + "endmodule\n\n");
        bw.flush();
        bw.close();
    }

    /**
     * We havae a list of pcre rule, for each bram
     * this step will create each bram and insert it to listBram of Generator.
     */
    private int processData() {
        this.listBram = new LinkedList<BRAM>();
        for (int i = 0; i < this.listRule.size(); i++) {
            LinkedList<String> lpcre = this.listRule.get(i);
            LinkedList<ReEngine> lengine = new LinkedList<ReEngine>();
            System.out.println("\n\n\nProcess BRAM " + i + "\n");
            for (int j = 0; j < lpcre.size(); j++) {
                System.out.println("" + lpcre.get(j));
                ParseTree tree = new ParseTree(lpcre.get(j));
                NFA nfa = new NFA();
                nfa.buildNFA(tree);
                nfa.reduceRedundantState();
                ReEngine engine = new ReEngine();
                engine.buildEngine(nfa);
                lengine.add(engine);
            }
            BRAM bram = new BRAM(i);
            bram.addEngine(lengine);
            System.out.println("Do Union charblock");
            bram.unionCharBlocks();
            System.out.println("Print Char block");
            //bram.printBlockCharBram();
            this.listBram.add(bram);
        }
        return 0;
    }

    /**
     * module top_nfa_one_rule_tb;

    // Inputs
    reg i_clk;
    reg i_rst;
    reg [10:0] i_data;

    // Outputs
    wire [10:0] o_dout;
    wire o_eod;
    wire [7:0] o_data;
    wire o_sod;
    wire [8:0] w_in;

    // Instantiate the Unit Under Test (UUT)
    top_nfa_one_rule uut (
    .o_dout(o_dout),
    .o_eod(o_eod),
    .o_data(o_data),
    .o_sod(o_sod),
    .i_clk(i_clk),
    .i_rst(i_rst),
    .i_data(i_data),
    .w_in(w_in)
    );
    initial begin
    // Initialize Inputs
    i_clk = 0;
    i_rst = 0;
    i_data = 0;

    // Wait 100 ns for global reset to finish
    #100;

    #100;

    #10 i_rst = 1;
    #10 i_rst = 0;

    // Add stimulus here

    #25 i_data = {8'd1,1'b1,1'b0,1'b0};//sod

    #10 i_data = {8'd67,1'b0,1'b1,1'b0};//C
    #10 i_data = {8'd85,1'b0,1'b1,1'b0};//U
    #10 i_data = {8'd38,1'b0,1'b1,1'b0};//&
    #10 i_data = {8'd193,1'b0,1'b1,1'b0};//Á
    #10 i_data = {8'd17,1'b0,1'b1,1'b0};//
    #10 i_data = {8'd94,1'b0,1'b1,1'b0};//^
    #10 i_data = {8'd230,1'b0,1'b1,1'b0};//æ
    #10 i_data = {8'd64,1'b0,1'b1,1'b0};//@
    #10 i_data = {8'd54,1'b0,1'b1,1'b0};//6
    #10 i_data = {8'd115,1'b0,1'b1,1'b0};//s
    #10 i_data = {8'd226,1'b0,1'b1,1'b0};//â
    #10 i_data = {8'd24,1'b0,1'b1,1'b0};//
    #10 i_data = {8'd51,1'b0,1'b1,1'b0};//3
    #10 i_data = {8'd49,1'b0,1'b1,1'b0};//1
    #10 i_data = {8'd57,1'b0,1'b1,1'b0};//9
    #10 i_data = {8'd52,1'b0,1'b1,1'b0};//4
    #10 i_data = {8'd50,1'b0,1'b1,1'b0};//2
    #10 i_data = {8'd53,1'b0,1'b1,1'b0};//5
    #10 i_data = {8'd54,1'b0,1'b1,1'b0};//6
    #10 i_data = {8'd24,1'b0,1'b1,1'b0};//
    #10 i_data = {8'd22,1'b0,1'b1,1'b0};//
    #10 i_data = {8'd108,1'b0,1'b1,1'b0};//l
    #10 i_data = {8'd111,1'b0,1'b1,1'b0};//o

    #10 i_data = {8'd1,1'b0,1'b1,1'b0};//eod
    #10 i_data = {8'd108,1'b0,1'b1,1'b0};//l
    #10 i_data = {8'd111,1'b0,1'b1,1'b0};//o
    end

    initial begin
    #0 i_clk = 5;
    forever #5 i_clk = ~i_clk;
    end

    endmodule
     * @param folder
     * @param bram
     */
    public void buildTestBench_1(String folder, BRAM bram) {
        try {
            //top_nfa_one_rule_tb

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + File.separator + "top_nfa_one_rule_tb.v")));
            //top_nfa_one_rule_tb

            bw.write("module top_nfa_one_rule_tb;\n");

            bw.write("// Inputs\n"
                    + "\treg i_clk;\n"
                    + "\treg i_rst;\n"
                    + "\treg [10:0] i_data;\n"
                    + "wire o_eod;\n"
                    + "wire [7:0] o_data;\n"
                    + "wire o_sod;\n"
                    + "wire [8:0] w_in;\n"
                    + "// Instantiate the Unit Under Test (UUT)\n"
                    + "top_nfa_one_rule uut (\n"
                    + ".o_dout(o_dout),\n"
                    + ".o_eod(o_eod),\n"
                    + ".o_data(o_data),\n"
                    + ".o_sod(o_sod),\n"
                    + ".i_clk(i_clk),\n"
                    + ".i_rst(i_rst),\n"
                    + ".i_data(i_data),\n"
                    + ".w_in(w_in)\n"
                    + ");\n"
                    + "initial begin\n"
                    + "// Initialize Inputs\n"
                    + "i_clk = 0;\n"
                    + "i_rst = 0;\n"
                    + "i_data = 0;\n"
                    + "// Wait 100 ns for global reset to finish\n"
                    + "#100;\n"
                    + "#100;\n"
                    + "#10 i_rst = 1;\n"
                    + "#10 i_rst = 0;\n"
                    + "// Add stimulus here\n"
                    + "#25 i_data = {8'd1,1'b1,1'b0,1'b0};//sod \n");

            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
            }

            //bw.write("\t\t#20 sod = 0;\n");
            bw.write("//#10 i_data = {ASCII code, 0, 1, 0}\n");
            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                System.out.println("Gen Pattern: " + temp.rule.testPartten);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
                for (int j = 0; j < temp.rule.testPartten.length(); j++) {
                    //bw.write("\t\t#20 char = " + ((int) temp.rule.testPartten.charAt(j)) + ";//" + temp.rule.testPartten.charAt(j) + "\n");
                    bw.write("\t\t#10 i_data = {8'd" + ((int) temp.rule.testPartten.charAt(j)) + ",1'b0,1'b1,1'b0};//" + temp.rule.testPartten.charAt(j) + "\n");
                }
            }

            bw.write("\tend\n"
                    + "\tinitial begin\n"
                    + "\t\t#10 i_clk = ~i_clk;\n"
                    + "\t\tforever #10 i_clk = ~i_clk;\n"
                    + "\tend\n"
                    + "\tinitial #100000 $finish;\n"
                    + "endmodule\n");

            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildTestBench(String folder, BRAM bram) {
        //firstly, create testcase
        PCRETestCase pcretestcase = new PCRETestCase();
        for (int i = 0; i < bram.engineList.size(); i++) {
            pcretestcase.addPCRE(bram.engineList.get(i).rule.getRule(), i);
        }
        pcretestcase.generateSimpleTestcase(2);
        //generate two testcase, remember that each testcase contain n pattern corresponding to size of data.
        TestCase tc = pcretestcase.listTestCase.getFirst();
        //this.genMzFile(this.genfolder+"pack.mz", tc);
        this.gen4LongLoc(this.genfolder + "head.pay.draf", tc, bram);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + File.separator + "BRAM_" + bram.ID + "_tb.v")));
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
            ex.printStackTrace();
        }
    }

    private void generateTestbench() {
        System.out.println("generateTestbench()");
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = listBram.get(i);
            PCRETestCase ptc = new PCRETestCase("bram_" + i);
            System.out.println("BRAm " + i);
            for (int j = 0; j < bram.engineList.size(); j++) {
                System.out.println("\txxx " + bram.engineList.get(j).rule.getRule());
                ptc.addPCRE(bram.engineList.get(j).rule.getRule(), j);
            }
            ptc.generateSimpleTestcase(5);
            ptc.genTestbench(genfolder);
        }

    }

    private void generateTestRam() {
        /**
         * ;d b a \x2e \x0A \d \x2d C L I S
        ;Block memory of depth=256, and width=11
        MEMORY_INITIALIZATION_RADIX=2;
        MEMORY_INITIALIZATION_VECTOR=
        00000000000,
        00000000000,
        00000000000;
         */
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.genfolder + "testram.content"));
            if (br == null) {
                return;
            }
            String s;
            String[] array;
            LinkedList<String[]> ldata = new LinkedList<String[]>();
            System.out.println("Read Data file for test\n");
            while ((s = br.readLine()) != null) {
                array = s.split("-");
                for (int i = 0; i < array.length; i++) {
                    System.out.print(array[i]);
                }
                System.out.print("\n");

                ldata.add(array);
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(this.genfolder + "TESTRAM.coe"));
            bw.write(";Xbit - 1bit - 1bit\n");
            bw.write(";char - sood - enab\n");

            bw.write("\n");
            bw.write(";Block memory of depth=256, and width=10\n"
                    + "MEMORY_INITIALIZATION_RADIX=2;\n"
                    + "MEMORY_INITIALIZATION_VECTOR=\n");
            for (int i = 0; i < ldata.size() && i < 256; i++) {
                String[] ar = ldata.get(i);
                bw.write(Integer.toString(Integer.parseInt(ar[2]), 2) + ar[0] + ar[1] + ",\n");
                //bw.write(";"+ ar[2]+ar[0] + ar[1] +"\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void genMzFile(String filename, TestCase tc) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < tc.listPattern.size(); i++) {
                bw.write(this.genMzInstruction(tc.listPattern.get(i).data) + ""
                        + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String genMzInstruction(String data) {
        String ret = "";
        ret += "mz vboxnet0 ";

        //src address and des address
        ret += "-A 192.168.56.1 " + " -B 192.168.56.101 ";
        //protocol
        ret += "-t tcp ";
        ret += "sp=80" + ",dp=80" + ",p=" + this.createPayloadHex(data);
        return ret;

    }

    public String createPayloadHex(String data) {
        String ret = "";
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            ret += Integer.toHexString((int) ch) + ":";
        }
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    /**
     * ==========================================================================
     *
     * This section for Guiv01 support
     *
     * ==========================================================================
     */
    public void GenHDLv01() {
        //process data
        this.processData();
        try {
            //Generate HDL.
            this.generateHDL();
            //Generate Testbench.
            //this.generateTestRam();
            //this.generateTestRam();
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void GenTestBenchv01() {
        //generate Bram Testbench
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = this.listBram.get(i);
            //generate each engien
            this.buildTestBench(genfolder, bram);
            this.buildTestBench_1(genfolder, bram);
        }
    }

    private void generateTestRamCOE(String filename, LinkedList<String> lstring) {
        BufferedWriter bw = null;
        try {
            /**
             * ;d b a \x2e \x0A \d \x2d C L I S
            ;Block memory of depth=256, and width=11
            MEMORY_INITIALIZATION_RADIX=2;
            MEMORY_INITIALIZATION_VECTOR=
            00000000000,
            00000000000,
            00000000000;
             */
            String s;
            String[] array;
            LinkedList<String[]> ldata = new LinkedList<String[]>();
            System.out.println("Read Data file for test\n");
            for (int j = 0; j < lstring.size(); j++) {
                s = lstring.get(j);
                array = s.split("-");
                for (int i = 0; i < array.length; i++) {
                    System.out.print(array[i]);
                }
                System.out.print("\n");
                ldata.add(array);
            }
            bw = new BufferedWriter(new FileWriter(filename));
            bw.write(";Xbit - 1bit - 1bit\n");
            bw.write(";char - sood - enab\n");
            bw.write("\n");
            bw.write(";Block memory of depth=256, and width=10\n" + "MEMORY_INITIALIZATION_RADIX=2;\n" + "MEMORY_INITIALIZATION_VECTOR=\n");
            for (int i = 0; i < ldata.size() && i < 256; i++) {
                String[] ar = ldata.get(i);
                bw.write(Integer.toString(Integer.parseInt(ar[0]), 2) + ar[1] + ar[2] + ",\n");
                //bw.write(";"+ ar[2]+ar[0] + ar[1] +"\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void geCOEv01() {
        LinkedList<String> ls;
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = this.listBram.get(i);
            ls = new LinkedList<String>();
            ls.add("8-1-1");
            for (int k = 0; k < bram.engineList.size(); k++) {
                ReEngine temp = bram.engineList.get(k);
                for (int j = 0; j < temp.rule.testPartten.length(); j++) {
                    if (k == 0 && j == 0) {
                        ls.add((int) temp.rule.testPartten.charAt(j) + "-1-1");
                    } else {
                        ls.add((int) temp.rule.testPartten.charAt(j) + "-0-1");
                    }
                }
            }
            this.generateTestRamCOE(this.genfolder + "TestBram_" + i + ".coe", ls);
        }

    }

    private void gen4LongLoc(String file, TestCase tc, BRAM bram) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
            }
            bw.write("//---------------------------------------------------\n");

            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                Pattern pt = tc.listPattern.get(i);
                System.out.println("Gen Pattern: " + pt.data);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
                bw.write("//Start\n"
                        + "#50 	rx_ll_sof_in_n = 0;\n"
                        + "rx_ll_src_rdy_n = 0;\n"
                        + "rx_ll_data_in = 8'h1;\n"
                        + "#10	rx_ll_sof_in_n = 1;\n"
                        + "rx_ll_data_in = 8'h2;\n");
                bw.write("//header\n"
                        + "#10 rx_ll_data_in = 8'h03;\n"
                        + "#10 rx_ll_data_in = 8'h04;\n"
                        + "#10 rx_ll_data_in = 8'h05;\n"
                        + "#10 rx_ll_data_in = 8'h06;\n"
                        + "#10 rx_ll_data_in = 8'h07;\n"
                        + "#10 rx_ll_data_in = 8'h08;\n"
                        + "#10 rx_ll_data_in = 8'h09;\n"
                        + "#10 rx_ll_data_in = 8'h0a;\n"
                        + "#10 rx_ll_data_in = 8'h0b;\n"
                        + "#10 rx_ll_data_in = 8'h0c;\n"
                        + "#10 rx_ll_data_in = 8'h0d;\n"
                        + "#10 rx_ll_data_in = 8'h0e;\n"
                        + "#10 rx_ll_data_in = 8'h45;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h2e;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h40;\n"
                        + "#10 rx_ll_data_in = 8'h06;\n"
                        + "#10 rx_ll_data_in = 8'hf7;\n"
                        + "#10 rx_ll_data_in = 8'h74;\n"
                        + "#10 rx_ll_data_in = 8'hc0;\n"
                        + "#10 rx_ll_data_in = 8'ha8;\n"
                        + "#10 rx_ll_data_in = 8'h01;\n"
                        + "#10 rx_ll_data_in = 8'h03;\n"
                        + "#10 rx_ll_data_in = 8'hc0;\n"
                        + "#10 rx_ll_data_in = 8'ha8;\n"
                        + "#10 rx_ll_data_in = 8'h01;\n"
                        + "#10 rx_ll_data_in = 8'h02;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h01;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h01;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h50;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h02;\n"
                        + "#10 rx_ll_data_in = 8'h5a;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n"
                        + "#10 rx_ll_data_in = 8'h00;\n");

                for (int j = 0; j < pt.data.length(); j++) {

                    bw.write("#10 rx_ll_data_in = " + ((int) pt.data.charAt(j)) + ";\n");
                    if (j == (pt.data.length() - 1)) {
                        bw.write("//end of packet\n"
                                + "#10 rx_ll_data_in = " + ((int) pt.data.charAt(j)) + ";\n"
                                + "rx_ll_eof_in_n = 0;\n");
                    }
                }

                bw.write("rx_ll_eof_in_n = 0;\n"
                        + "#10 rx_ll_sof_in_n = 1;\n"
                        + "rx_ll_eof_in_n = 1;\n"
                        + "rx_ll_src_rdy_n = 1;\n");
            }


            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
