/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Builder;

import BRAM.BRAM;
import HDL_Generator.HDL_Generator_v1;
import RTL_Creator.RTL_Creator_v1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class Builder_v1 {

    public LinkedList<LinkedList<String>> listRule;
    public LinkedList<BRAM> listBram;
    //public String genfolder = System.getProperty("user.dir") + File.separator + "GenHDL" + File.separator;
    public String genDir = "~/NetBeansProjects/pcreengine/generate" + File.separator;
    public String inputDir = "~/NetBeansProjects/pcreengine/pcre" + File.separator; 
    public String outputfolder = System.getProperty("user.dir") + File.separator + "output" + File.separator;
    public String pcrefile;
    RTL_Creator_v1 rtlCreator = new RTL_Creator_v1();
    HDL_Generator_v1 hdlGen = new HDL_Generator_v1();

    public static void main(String[] args) throws Exception {
        Builder_v1 gen = new Builder_v1();
        gen.doAction();
    }

    public void addlistRule(LinkedList<LinkedList<String>> listRule) {
        this.listRule = listRule;
    }

    public void buildRTL() {
        this.createRTLStructure();
        this.hdlGen.setRTLCreator(rtlCreator);
    }
    public void genTestBench(){
        //this.hdlGen.setGenerateFolder(genDir);
        this.hdlGen.setGenerateFolderDefault(genDir);
        //this.hdlGen.genHDL();
        //Generate Testbench
        this.hdlGen.genTestBench();
        //this.hdlGen.outputExcelStatistic();
        this.hdlGen.genMZInstructionFile();
    }
    public void genHDL() {
        this.hdlGen.setGenerateFolder(genDir);
        this.hdlGen.genHDL();
        //Generate Testbench
        this.hdlGen.genTestBench();
        this.hdlGen.outputExcelStatistic();
        this.hdlGen.genMZInstructionFile();
    }

    public void doAction() throws Exception {
        String filename = "rules.test/cr.test";
        //String inputfile = System.getProperty("user.dir") + File.separator + filename;
        String inputfile = "/home/heckarim/NetBeansProjects/nfaengine/test/extract/oh";
        this.genDir = "/home/heckarim/NetBeansProjects/nfaengine/test/";
        //Read from file
        this.readFromFile(inputfile);

        //process data
        this.rtlCreator.setNoCRB(false);
        this.createRTLStructure();
        //Generate HDL.
        this.hdlGen.setRTLCreator(rtlCreator);
        this.hdlGen.setGenerateFolderDefault(genDir);
        this.hdlGen.genTestBench();
        //Generate Testbench
        //this.hdlGen.genTestBench();
        //this.hdlGen.outputExcelStatistic();
        //this.hdlGen.genMZInstructionFile();
        //Generate Testbench.
        //this.GenTestBenchv01();
        //this.generateTestbench(); //testcase ...
        //this.generateTestRam();
        this.rtlCreator.outputStatistic(this.outputfolder + "statistic.havecrb.mip");
    }

    /**
     * After this step listrule will contain all pcre from the file
     * @param filename
     */
    public boolean readFromFile(String filename) {
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
                // s = s + 'i'; //todo all is case sensitive
                lpcre.add(s);
            }
            if (lpcre != null) {
                this.listRule.add(lpcre);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            return false;
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    /**
     * We havae a list of pcre rule, for each bram
     * this step will create each bram and insert it to listBram of Generator.
     */
    private int createRTLStructure() {
        //set number of BRam Entity in RTLStructure.
        this.rtlCreator.setNumberBRam(this.listRule.size());
        //Update pcre list for each BRam.
        for (int i = 0; i < this.listRule.size(); i++) {
            LinkedList<String> lpcre = this.listRule.get(i);
            for (int j = 0; j < lpcre.size(); j++) {
                this.rtlCreator.addPcreBRam(i, lpcre.get(j));
            }
        }
        //build RTL Structure.
        this.rtlCreator.createRTLStructure();
        return 0;
    }
}
