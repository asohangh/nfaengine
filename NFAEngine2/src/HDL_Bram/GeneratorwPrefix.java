/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HDL_Bram;

import BRAM.BRAM;
import NFA.NFA;
import ParseTree.ParseTree;
import PrefixShare.PrefixShare;
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
public class GeneratorwPrefix {

    LinkedList<LinkedList<RuleUnit>> listRule;
    LinkedList<BRAM> listBram;
    public String genfolder = System.getProperty("user.dir") + File.separator + "GenHDL" + File.separator;

    public static void main(String[] args) throws Exception {
        GeneratorwPrefix gen = new GeneratorwPrefix();
        gen.doAction();
    }

    public void doAction() throws Exception {
        String filename = "bram.input.prefix.pcre";
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
        this.listRule = new LinkedList<LinkedList<RuleUnit>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            LinkedList<RuleUnit> lpcre = null;

            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.isEmpty()) {
                    continue;
                }
                if (s.startsWith("#bram")) {
                    if (lpcre != null) {
                        this.listRule.add(lpcre);
                    }
                    lpcre = new LinkedList<RuleUnit>();
                    continue;
                } else if (s.startsWith("#prefix")) {
                    String ss;
                    RuleUnit ru = new RuleUnit();
                    ru.isPrefix = true;
                    while ((ss = br.readLine()) != null) {
                        ss = ss.trim();
                        if (ss.isEmpty()) {
                            continue;
                        } else if (ss.startsWith("#endprefix")) {
                            lpcre.add(ru);
                            break;
                        } else {
                            ru.insertPcre(ss);
                        }
                    }
                } else {
                    RuleUnit ru = new RuleUnit();
                    ru.isPrefix = false;
                    ru.lpcre.add(s);
                    lpcre.add(ru);
                }
            }
            if (lpcre != null) {
                this.listRule.add(lpcre);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneratorwPrefix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeneratorwPrefix.class.getName()).log(Level.SEVERE, null, ex);
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
        GeneratorwPrefix.doBuildInterfacer(genfolder);
        GeneratorwPrefix.createTopEngineTogether(genfolder, this.listBram);
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
            GeneratorwPrefix.doBuildInterfacer(folder);
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
     * this step will create each bram and insert it to listBram of GeneratorwPrefix.
     */
    private void processData() {
        this.listBram = new LinkedList<BRAM>();
        for (int i = 0; i < this.listRule.size(); i++) {
            LinkedList<RuleUnit> lpcre = this.listRule.get(i);
            LinkedList<ReEngine> lengine = new LinkedList<ReEngine>();
            System.out.println("\n\n\nProcess BRAM " + i + "\n");
            for (int j = 0; j < lpcre.size(); j++) {
                RuleUnit ru = lpcre.get(j);
                ReEngine engine;
                if (!ru.isPrefix) {
                    System.out.println("" + lpcre.get(j));
                    ParseTree tree = new ParseTree(ru.getPcre());
                    NFA nfa = new NFA();
                    nfa.buildNFA(tree);
                    nfa.reduceRedundantState();
                    engine = new ReEngine();
                    engine.buildEngine(nfa);
                } else {//is have prefix
                    PrefixShare ps = new PrefixShare();
                    ps.addPrefixPcre(ru.getPrefix());
                    ps.addSubfixPcre(ru.getSubfix());
                    ps.buildEngine();
                    engine = ps.engine;
                }
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
}
