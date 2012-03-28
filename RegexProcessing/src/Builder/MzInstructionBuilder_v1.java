/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Builder;

import TestPattern.TestCase;
import java.io.BufferedWriter;
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
public class MzInstructionBuilder_v1 {

    LinkedList<TestCase> listTestCase;
    public String genfolder;
    Random ran = new Random();

    public void setTestCaseList(LinkedList<TestCase> list) {
        this.listTestCase = list;
    }

    public void setOutputFolder(String dir) {
        this.genfolder = dir;
    }

    /**
     * Each testcase will be generate into seperated file
     */
    public void GenerateSeperateTestCase() {
        for (int i = 0; i < this.listTestCase.size(); i++) {
            TestCase tc = this.listTestCase.get(i);
            this.genMzFile(this.genfolder + "seperate." + i + ".mz", tc);
        }
    }

    public void genMzFile(String filename, TestCase tc) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < tc.listPattern.size(); i++) {
                bw.write(this.genMaliciousMZInstruction(tc.listPattern.get(i).data, 1200, 1400) + ""
                        + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.print(ex);
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
        if (!ret.isEmpty()) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    private String genNormalMzInstruction(int min, int max) {
        return this.genMZInstruction("", min, max);
    }

    private String genMaliciousMZInstruction(String data, int min, int max) {
        return this.genMZInstruction(data, min, max);
    }

    private String genMZInstruction(String data, int min, int max) {
        String ret = "";
        String[] sam = new String[5];
        sam[0] = "HECKARIM";
        sam[1] = "BIGWORM";
        sam[2] = "TANTAI";
        sam[3] = "MrBIN";
        sam[4] = "MrLoc";
        int len;
        int index = ran.nextInt(4);
        String sample = sam[index];
        //create payload
        String p = "";
        if (min == max) {
            len = min;
        } else {
            len = min + ran.nextInt(Math.abs(max - min));
        }
        int size = sample.length();

        //convert string to hex data
        p = this.createPayloadHex(data);

        int psize = p.length();
        if (psize < len) {
            if (!p.isEmpty() && !p.endsWith(":")) {
                p += ":";
            }
            for (int i = 0; i < (len - psize); i++) {
                p += Integer.toHexString((int) sample.charAt(i % size)) + ":";
            }
            //remove last :
            if (p.endsWith(":")) {
                p = p.substring(0, p.length() - 1);

            }
        }
        ///create mz instruction
        ret += "mz vboxnet0 ";

        //src address and des address
        ret += "-A 192.168.56.1 " + " -B 192.168.56.101 ";
        //protocol
        ret += "-t tcp ";
        ret += "sp=80" + ",dp=80";
        //src
        ret += ",p=" + p;
        //System.out.println("genMZInstruction(int min, int max) : " + ret);
        return ret;
    }

    public void GenerateVariousTestcase() {
        LinkedList<String> lstring = new LinkedList<String>();
        //put all patterm in to list
        for (int i = 0; i < this.listTestCase.size(); i++) {
            TestCase tc = this.listTestCase.get(i);
            for (int j = 0; j < tc.listPattern.size(); j++) {
                lstring.add(tc.listPattern.get(j).data);
            }
        }
        //Genertae short packet
        this.genMzFilePercent(lstring, this.genfolder + "pack.0.short.mz", 0, 1, 200, 500);
        this.genMzFilePercent(lstring, this.genfolder + "pack.25.short.mz", 1, 3, 200, 500);
        this.genMzFilePercent(lstring, this.genfolder + "pack.50.short.mz", 1, 1, 200, 500);
        this.genMzFilePercent(lstring, this.genfolder + "pack.75.short.mz", 3, 1, 200, 500);
        this.genMzFilePercent(lstring, this.genfolder + "pack.100.short.mz", 1, 0, 200, 500);
        //Genertae long packet
        this.genMzFilePercent(lstring, this.genfolder + "pack.0.long.mz", 0, 1, 1200, 1400);
        this.genMzFilePercent(lstring, this.genfolder + "pack.25.long.mz", 1, 3, 1200, 1400);
        this.genMzFilePercent(lstring, this.genfolder + "pack.50.long.mz", 1, 1, 1200, 1400);
        this.genMzFilePercent(lstring, this.genfolder + "pack.75.long.mz", 3, 1, 1200, 1400);
        this.genMzFilePercent(lstring, this.genfolder + "pack.100.long.mz", 1, 0, 1200, 1400);
        //generate special oerder
       // this.genMzFileSpecialOrder(lstring);


    }

    /**
     *
     * @param filename
     * @param m   number of malicious packet
     * @param g   number of backgourn packet
     * note:
     *      +, if we want to generate 20% : m=1  g=4.
     */
    private void genMzFilePercent(LinkedList<String> lpcre, String filename, int m, int g, int min, int max) {
        try {
            System.out.print("Begin Create Packet " + m + ":" + g + "-" + min + "-" + max + "\n");
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            //int base = this.lcontent.size();
            int index = 0;

            if (m == 0) {
                for (int j = 0; j < lpcre.size(); j++) {
                    for (int i = 0; i < g; i++) {
                        bw.write(this.genNormalMzInstruction(min, max) + "\n");
                    }
                }
            } else {
                while (index < lpcre.size()) {
                    for (int i = 0; i < m && index < lpcre.size(); i++, index++) {
                        bw.write(this.genMaliciousMZInstruction(lpcre.get(index), min, max) + "\n");
                    }
                    for (int i = 0; i < g; i++) {
                        bw.write(this.genNormalMzInstruction(min, max) + "\n");
                    }
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }


    /*
     * for framwork group they want some strick
     */
    private void genMzFilePercentSpecial(LinkedList<String> lpcre, String filename, int m, int g, int min, int max) {
        try {
            System.out.print("Begin Create Packet special " + m + ":" + g + "-" + min + "-" + max + "\n");
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            //int base = this.lcontent.size();

            if (m == 0) {
                for (int j = 0; j < lpcre.size(); j++) {
                    for (int i = 0; i < g; i++) {
                        bw.write(this.genNormalMzInstruction(min, max) + "\n");
                    }
                }
            } else {
                for (int i = 0; i < lpcre.size(); i++) {
                    for (int j = 0; j < m; j++) {
                        bw.write(this.genMaliciousMZInstruction(lpcre.get(i), min, max) + "\n");
                    }
                }
                for (int i = 0; i < lpcre.size(); i++) {
                    for (int j = 0; j < g; j++) {
                        bw.write(this.genNormalMzInstruction(min, max) + "\n");
                    }
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * for Framwork Group, they want some trick.
     * @param lstring
     */
    private void genMzFileSpecialOrder(LinkedList<String> lstring) {
        String filename;
        //special 25
        //short
        filename = this.genfolder + "special.25.short.mz";
        this.genMzFilePercentSpecial(lstring, filename, 1, 3, 200, 500);
        //long
        filename = this.genfolder + "special.25.long.mz";

        this.genMzFilePercentSpecial(lstring, filename, 1, 3, 1200, 1400);
        //special 75
        //short
        filename = this.genfolder + "special.75.short.mz";
        this.genMzFilePercentSpecial(lstring, filename, 3, 1, 200, 500);
        //long
        filename = this.genfolder + "special.75.long.mz";
        this.genMzFilePercentSpecial(lstring, filename, 3, 1, 1200, 1400);
    }
}
