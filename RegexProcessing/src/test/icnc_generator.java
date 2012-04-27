/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import RTL_Creator.RTL_Creator_v2;
import hdl_generator.HDL_Generator_v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import mip.pcre.pcre_v2.PCREPattern;

/**
 *
 * @author heckarim
 */
public class icnc_generator {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        icnc_generator test = new icnc_generator();
        test.action();
    }
    LinkedList<String> lpre = new LinkedList<String>();
    LinkedList<String> lin = new LinkedList<String>();
    LinkedList<String> len = new LinkedList<String>();
    LinkedList<pcregroup> lgpcre = new LinkedList<pcregroup>();
    RTL_Creator_v2 creator;
    //ieice section
    public static String infolder = System.getProperty("user.dir")
            + File.separator + "ieice" + File.separator + "test.3" + File.separator;
    public static String outfolder = System.getProperty("user.dir")
            + File.separator + "ieice" + File.separator + "test.3" + File.separator;
    /*//LVTN secsion
    public static String infolder = System.getProperty("user.dir")
    + File.separator + "lvtn" + File.separator + "4dung" + File.separator;
    public static String outfolder = System.getProperty("user.dir")
    + File.separator + "lvtn" + File.separator + "4dung" + File.separator;
     */

    private void action() throws FileNotFoundException, IOException {
        String file = infolder + "web-activex.rules";
        //this.readfromfile_v1(file); //v1 support only sigle group.
        this.readFromFile_v2(file); //v2 support multipe group.
        System.out.println("finish readfromfile");

        /*//craete ReGroup v1 only support sigle group
        creator = new RTL_Creator_v2();
        creator.createGroup(0);
        creator.addPrefix(0, lpre);
        System.out.println("finish prefix");
        creator.addInfix(0, lin);
        System.out.println("finish infix");
        creator.addEngine(0, len);
        System.out.println("finish create RTL");
        
        creator.reduceChar(0);
        //creator.print(0);
        //generate HDL
        HDL_Generator_v2 gen = new HDL_Generator_v2();
        gen.genfolder = outfolder;
        gen.setRTLCreator(creator);
        //0: LUT, 1: Bram, 2: decoder
        gen.genHDL(1);
        gen.outstatistic();
        //this.calculateChar();
         * 
         */
        //craete ReGroup v2  support multiple groups
        creator = new RTL_Creator_v2();
        for (int i = 0; i < this.lgpcre.size(); i++) {
            pcregroup group = lgpcre.get(i);
            creator.createGroup(i);
            creator.addPrefix(i, group.lpre);
            System.out.println("group " + i + ": finish prefix");
            creator.addInfix(i, group.lin);
            System.out.println("group " + i + ": finish infix");
            creator.addEngine(i, group.len);
            System.out.println("group " + i + ": finish create RTL");
            creator.reduceChar(i);
        }
        //creator.print(0);
        //generate HDL
        HDL_Generator_v2 gen = new HDL_Generator_v2();
        gen.genfolder = outfolder;
        gen.setRTLCreator(creator);
        //0: LUT, 1: Bram, 2: decoder
        gen.genHDL(1);
        gen.outstatistic();

        //Generate Testbench
        gen.genTestBench();
        gen.outputExcelStatistic();
        gen.outstatistic();
       // gen.genMZInstructionFile();
        this.calculateChar();
    }

    private void readFromFile_v1(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        int mode = 0; //o pref, 1 infi, 2 engie
        while ((s = br.readLine()) != null) {
            if (s.compareToIgnoreCase("#prefix") == 0) {//read prefix
                mode = 0;
                continue;
            } else if (s.compareToIgnoreCase("#infix") == 0) {
                mode = 1;
                continue;
            } else if (s.compareToIgnoreCase("#engine") == 0) {
                mode = 2;
                continue;
            }
            if (s.startsWith("#")) {
                continue;
            }
            if (s.trim().isEmpty()) {
                continue;
            }
            switch (mode) {
                case 0:
                    this.lpre.add(s.trim());
                    break;
                case 1:
                    this.lin.add(s.trim());
                    break;
                case 2:
                    this.len.add(s.trim());
                    break;
            }
        }
        br.close();
    }

    private void calculateChar() {
        int count = 0;
        for (int i = 0; i < this.len.size(); i++) {
            int size;
            String pcre = this.len.get(i);
            PCREPattern rule = new PCREPattern(pcre);
            size = rule.getNoChar();
            System.out.println("\t" + size + " - " + pcre);
            count += rule.getNoChar();
        }
        System.out.println("no char : " + count);
    }

    private void readFromFile_v2(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        int mode = 0; //o pref, 1 infi, 2 engie
        pcregroup group = null;
        while ((s = br.readLine()) != null) {
            if (s.compareToIgnoreCase("#group") == 0) {
                if (group == null) {
                    group = new pcregroup();
                } else {
                    this.lgpcre.add(group);
                    group = new pcregroup();
                }
            } else {
                if (s.compareToIgnoreCase("#prefix") == 0) {//read prefix
                    mode = 0;
                    continue;
                } else if (s.compareToIgnoreCase("#infix") == 0) {
                    mode = 1;
                    continue;
                } else if (s.compareToIgnoreCase("#engine") == 0) {
                    mode = 2;
                    continue;
                }
                if (s.startsWith("#")) {
                    continue;
                }
                if (s.trim().isEmpty()) {
                    continue;
                }
                switch (mode) {
                    case 0:
                        group.lpre.add(s.trim());
                        break;
                    case 1:
                        group.lin.add(s.trim());
                        break;
                    case 2:
                        group.len.add(s.trim());
                        break;
                }

            }
        }
        this.lgpcre.add(group);
        br.close();
    }

    public class pcregroup {

        LinkedList<String> lpre = new LinkedList<String>();
        LinkedList<String> lin = new LinkedList<String>();
        LinkedList<String> len = new LinkedList<String>();
    }
}
