/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HDL_Bram;

import BRAM.BRAM;
import NFA.NFA;
import ParseTree.ParseTree;
import RegexEngine.ReEngine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class Generator {

    LinkedList<LinkedList<String>> listRule;
    LinkedList<BRAM> listBram;
    public String genfolder = System.getProperty("user.dir") + File.separator + "GenHDL" + File.separator;

    public static void main(String[] args) throws Exception {
        Generator gen = new Generator();
        gen.doAction();
    }

    public void doAction() throws Exception {
        String filename = "bram.input.pcre";
        String inputfile = System.getProperty("user.dir") + File.separator + filename;
        //Read from file
        this.readFromFile(inputfile);
        //process data
        this.processData();
        //Generate HDL.
        this.generateHDL();
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

        //Generate testbench.
        for (int i = 0; i < this.listBram.size(); i++) {
            BRAM bram = this.listBram.get(i);
            //generate each engien
            this.buildTestBench(genfolder, bram);
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
                + "\twire [8:0] temp,temp1;\n"
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
    private void processData() {
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
            bram.printBlockCharBram();
            this.listBram.add(bram);
        }
    }

    public void buildTestBench(String folder, BRAM bram) {
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
                    + "// Instantiate the Unit Under Test (UUT)\n"
                    + "\tBRAM_" + bram.ID + " uut (\n"
                    + "\t\t.out(out),\n"
                    + "\t\t.clk(clk),\n"
                    + "\t\t.sod(sod),\n"
                    + "\t\t.en(en),\n"
                    + "\t\t.char(char)\n"
                    + "\t\t);\n"
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
                    + "\tinitial begin\n");
            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                bw.write("//" + temp.rule.getPattern() + "..." + temp.rule.getModifier() + ";\n");
            }
            bw.write("\t\t#20 sod = 0;\n");
            for (int i = 0; i < bram.engineList.size(); i++) {
                ReEngine temp = bram.engineList.get(i);
                System.out.println("Gen Pattern: " + temp.rule.testPartten);
                for (int j = 0; j < temp.rule.testPartten.length(); j++) {
                    bw.write("\t\t#20 char = " + ((int) temp.rule.testPartten.charAt(j)) + ";//" + temp.rule.testPartten.charAt(j) + "\n");
                }
            }

            bw.write("\tend\n"
                    + "\tinitial begin\n"
                    + "\t\t#10 clk = ~clk;\n"
                    + "\t\tforever #10 clk = ~clk;\n"
                    + "\tend\n"
                    + "\tinitial #100000 $finish;\n"
                    + "endmodule\n");

            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
