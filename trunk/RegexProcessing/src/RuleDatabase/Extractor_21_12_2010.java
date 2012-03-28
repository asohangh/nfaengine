/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

import RuleDatabase.*;
import jxl.biff.DisplayFormat;

/**
 *
 * @author heckarim
 */
public class Extractor_21_12_2010 {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint = null;

    public static void main(String[] args) throws IOException, WriteException {
        Extractor_21_12_2010 ex = new Extractor_21_12_2010();
        ex.Action();
    }

    public void setOuputFolder(String folder) {
        if (folder.endsWith(File.separator)) {
            this.outputfolder = folder;
        } else {
            this.outputfolder = folder + File.separator;
        }
    }

    public void setRuleDatabase(RuleDatabase rb) {
        this.db = rb;
    }

    /**
     *
     *
     *
     *
     */
    private void Action() throws IOException, WriteException {
        db = new RuleDatabase();
        db.BuildDatabase();
        this.outputExcel();
        //this.outputTestingRulesFile("simple.nids.1.rules");
        //this.outputRules(this.outputfolder + "nids.rules");


    }

    /**
     *
     * @param lpcre
     * @return
     *
     * reduce all duplicate pcre
     */
    private LinkedList<PCRE> reducePCRE(LinkedList<PCRE> lpcre) {
        LinkedList<PCRE> rpcre = new LinkedList<PCRE>();
        for (int i = 0; i < lpcre.size(); i++) {
            boolean same = false;
            PCRE temp = lpcre.get(i);
            for (int j = 0; j < rpcre.size(); j++) {
                if (temp.CompareTo(rpcre.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                rpcre.add(temp);
            }
        }
        return rpcre;
    }

    /**
     *
     * @param lpcre
     * @return
     *
     *  Main purpose is reduce the number of pcre,
     *  Note:
     *      +,
     *      +,
     *      +,
     */
    private LinkedList<PCRE> morePCREFilter(LinkedList<PCRE> lpcre) {
        LinkedList<PCRE> rpcre = new LinkedList<PCRE>();
        /*
        this.lconstraint = new LinkedList<PCRE>();
        LinkedList<String> lsid = new LinkedList<String>();
        try {
        //get list pcre sid


        BufferedReader br = new BufferedReader(new FileReader(this.outputfolder + "pcre.choose"));
        String s;
        while ((s = br.readLine()) != null) {
        if (s.startsWith("#")) {
        continue;
        }
        if (!s.trim().isEmpty()) {
        if (lsid.indexOf(s.trim()) != -1) {
        System.out.println("Dupplicate " + s);
        }
        lsid.add(s.trim());
        }
        }
        } catch (IOException ex) {
        System.out.println(ex);
        }

        for (int i = 0; i < lpcre.size(); i++) {
        rpcre.add(lpcre.get(i));
        }

        for (int i = 0; i < rpcre.size();) {
        PCRE pcre = rpcre.get(i);
        if (lsid.indexOf(pcre.getSID()) == -1) {
        rpcre.remove(i);
        } else {
        i++;
        }
        }
         */
        return lpcre;
    }

    /**
     *  filter all rule wich our soft ware suppport
     */
    private LinkedList<PCRE> getSupportedPCRE() {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> lpcre = new LinkedList<PCRE>();

        // Extract all pcre from rule set to simple.pcre
        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                    //bw.write(rs.lstRuleAll.get(j).value + "\n");
                    PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getOpPcre();
                    if (this.checkSuportbySoftware(pcre)) {
                        lpcre.add(pcre);
                    }
                }
            }
        }
        return lpcre;
    }

    /**
     *
     * @param lcontent
     * @return
     */
    public LinkedList<OpContent> reduceContent(LinkedList<OpContent> lcontent) {
        LinkedList<OpContent> rcontent = new LinkedList<OpContent>();
        for (int i = 0; i < lcontent.size(); i++) {
            boolean same = false;
            OpContent temp = lcontent.get(i);
            for (int j = 0; j < rcontent.size(); j++) {
                if (temp.compareTo(rcontent.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                rcontent.add(temp);
            }
        }
        /*
        //4test
        for (int i = 0; i < rcontent.size(); i++) {
        System.out.println(rcontent.get(i).toString());
        }
         *
         */

        return rcontent;
    }

    /**
     *
     * @param lcontent
     * @return
     *
     * further filter for content
     *  note:
     *      +, <=16;
     *      +, <=512;
     *
     */
    public LinkedList<OpContent> moreContentFilter(LinkedList<OpContent> lcontent) {
        LinkedList<OpContent> rcontent = new LinkedList<OpContent>();
        for (int i = 0; i < lcontent.size(); i++) {
            OpContent temp = lcontent.get(i);
            if (temp.countCharacter() <= 16) {
                rcontent.add(temp);
            }
        }
        try {
            //remove content from notmatch
            BufferedReader br = new BufferedReader(new FileReader(this.outputfolder + "alert.content.notmatch"));
            String s;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("#")) {
                    continue;
                }
                for (int i = 0; i < rcontent.size();) {
                    if (rcontent.get(i).getSID().compareToIgnoreCase(s.trim()) == 0) {
                        rcontent.remove(i);
                    } else {
                        i++;
                    }
                }
            }
            br.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }


        return rcontent;
    }

    public void outputExcel() throws IOException, WriteException {

        // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(this.outputfolder + "Rules2.9.xls"));
        //this.outputExcelContent(workbook);

        this.outputExcelPCRE(workbook);

        //Statistic
        this.outputExcelStatistic(workbook);

        //output pcre by ruleset
        this.outputExcelPcreByRuleset(workbook);


        //this.outputExcelHeader(workbook);

        //this.outputLinkedIndex(workbook);

        //close the all opened connections
        workbook.write();
        workbook.close();
    }

    private void outputExcelPcreByRuleset(WritableWorkbook workbook) throws WriteException {
        WritableSheet sheet = workbook.createSheet("Pcre By Ruleset", 3); // sheet name

        //write ruleset No Enrules No Disrules
        int row = 1;
        for (int i = 0; i < this.db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = this.db.lstSnortRuleSet.get(i);
            Label label;

            //ruleset name
            label = new Label(0, row, rs.name.replace(".rules", ""));
            sheet.addCell(label);
            row++;

            //number of pcre;
            OptionMask mask = new OptionMask();
            mask.SetPermit("pcre");

            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                    //bw.write(rs.lstRuleAll.get(j).value + "\n");
                    PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getOpPcre();
                    if (this.checkSuportbySoftware(pcre)) {
                        label = new Label(1, row, pcre.getvalue());
                        sheet.addCell(label);
                        row ++;
                    }
                }
            }
        }

        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Rule Set; PCRE";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void outputExcelStatistic(WritableWorkbook workbook) throws WriteException {
        WritableSheet sheet = workbook.createSheet("Rules Statistic", 2); // sheet name

        //write ruleset No Enrules No Disrules
        for (int i = 0; i < this.db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = this.db.lstSnortRuleSet.get(i);
            Label label;

            //ruleset name
            label = new Label(0, i + 1, rs.name.replace(".rules", ""));
            sheet.addCell(label);

            //rule set enable rules
            label = new Label(1, i + 1, Integer.toString(rs.lstRuleActive.size()));
            sheet.addCell(label);

            //rule set disable rules
            label = new Label(2, i + 1, Integer.toString(rs.lstRuleInactive.size()));
            sheet.addCell(label);

            //number of pcre;
            int countEn = 0;
            int count =0;
            OptionMask mask = new OptionMask();
            mask.SetPermit("pcre");

            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                    //bw.write(rs.lstRuleAll.get(j).value + "\n");
                    PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getOpPcre();
                    count ++;
                    if (this.checkSuportbySoftware(pcre)) {
                        countEn++;
                    }
                }
            }
            label = new Label(3, i + 1, Integer.toString(count));
            sheet.addCell(label);
            label = new Label(4, i + 1, Integer.toString(countEn));
            sheet.addCell(label);

        }


        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Rule Set; No EnRules; No DisRules; No Pcre; No Supported PCRE";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void outputExcelPCRE(WritableWorkbook workbook) throws WriteException {
        LinkedList<PCRE> lp = this.getSupportedPCRE();
        //LinkedList<PCRE> rlp = this.reducePCRE(lp);
        lpcre = lp;//this.morePCREFilter(rlp);
        WritableSheet sheet = workbook.createSheet("PCRE", 1); // sheet name

        //write pcre
        int index = 0;//this.lcontent.size();
        for (int i = 0; i < this.lpcre.size(); i++) {
            Label label;
            PCRE con = lpcre.get(i);
            //Local Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //SID
            label = new Label(1, i + 1, con.getSID());
            sheet.addCell(label);
            //PCRE
            label = new Label(2, i + 1, con.toString());
            sheet.addCell(label);

            //Rule
            label = new Label(3, i + 1, con.getRule().value);
            sheet.addCell(label);
        }
        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; SID; PCRE; Rule";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }

    }

    private void outputExcelHeader(WritableWorkbook workbook) throws WriteException {
        LinkedList<RuleHeader> lheader = this.getHeader();

        WritableSheet sheet = workbook.createSheet("Header", 0); // sheet name
        for (int i = 0; i < lheader.size(); i++) {
            Label label;
            RuleHeader con = lheader.get(i);
            //Local Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Global Index
            label = new Label(1, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Action
            label = new Label(2, i + 1, con.action);
            sheet.addCell(label);
            //Src Add
            label = new Label(3, i + 1, con.srcAddress);
            sheet.addCell(label);
            //SRC Port
            label = new Label(4, i + 1, con.srcPort);
            sheet.addCell(label);
            //Flow
            label = new Label(5, i + 1, con.direction);
            sheet.addCell(label);
            //Dst Address
            label = new Label(6, i + 1, con.dstAddress);
            sheet.addCell(label);
            //Dst Port
            label = new Label(7, i + 1, con.dstPort);
            sheet.addCell(label);
            //Header
            label = new Label(8, i + 1, con.toString());
            sheet.addCell(label);
            //SID
            label = new Label(9, i + 1, con.getSID());
            sheet.addCell(label);
            //Rule
            label = new Label(10, i + 1, con.getRule().toString());
            sheet.addCell(label);
        }

        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Local Index; Global Index; Action; SrcAddress; SrcPort; Flow; DstAddress; DstPort; Header; SID; Rule";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    /**
     *
     * @return
     *
     * get header from lcontent and lpcre
     */
    private LinkedList<RuleHeader> getHeader() {
        LinkedList<RuleHeader> rhead = new LinkedList<RuleHeader>();
        // Get from lcontent first
        /*for (int i = 0; i < this.lcontent.size(); i++) {
        rhead.add(lcontent.get(i).getRule().header);
        }*/
        // Get from lpcre
        for (int i = 0; i < this.lpcre.size(); i++) {
            rhead.add(lpcre.get(i).getRule().header);
        }
        return rhead;
    }

    private void outputFakeRules(String filename, LinkedList<PCRE> lpcre) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.outputfolder + filename));
            bw.write("# SnortRule 2.9 for Testing NIDS Project on NetFPGA\n");
            bw.write("# Content Rules\n");
            //wirte content rule;
           /* for (int i = 0; i < lcon.size(); i++) {
            bw.write(this.formatFakeContentRule(lcon.get(i)));
            bw.write("\n");
            } 
             */
            //write pcre rule;
            for (int i = 0; i < lpcre.size(); i++) {
                bw.write(this.formatFakePCRERule(lpcre.get(i)));
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void outputTestingRulesFile(String filename) {

        //pcre
        LinkedList<PCRE> lp = this.getSupportedPCRE();
        LinkedList<PCRE> rlp = this.reducePCRE(lp);
        LinkedList<PCRE> lpcre = this.morePCREFilter(rlp);

        this.outputFakeRules(filename, lpcre);

    }

    /**
     *   If we extracted content and pcre from rules database this function will ouput formal rulex file
     * for it.
     * @param filename
     * @param lcon
     * @param lpcre
     */
    private void outputRules(String filename, LinkedList<OpContent> lcon, LinkedList<PCRE> lpcre) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write("# SnortRule 2.9 for Testing NIDS Project on NetFPGA\n");
            bw.write("# Content Rules\n");
            //wirte content rule;
            for (int i = 0; i < lcon.size(); i++) {
                bw.write(lcon.get(i).getRule().value);
                bw.write("\n");
            }
            //write pcre rule;
            for (int i = 0; i < lpcre.size(); i++) {
                bw.write(lpcre.get(i).getRule().value);
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     *
     * @param pcre
     * @return
     *
     *
     * NOTE: (todo).
     *      +, don't have metadata
     *      +, many reference isn't support.
     */
    private String formatFakePCRERule(PCRE pcre) {
        String ret = "";
        RuleComponent rule = pcre.getRule();
        ret = rule.header.value + " " + "(";
        //msg
        RuleOption msg = rule.getRuleOption("msg");
        if (msg != null) {
            ret += msg.toString() + "; ";
        }
        //PCRE
        ret += pcre.getOptionString() + "; ";
        //reference
        RuleOption reference = rule.getRuleOption("reference");
        if (reference != null) {
            ret += reference.toString() + "; ";
        }
        //classtype
        RuleOption classtype = rule.getRuleOption("classtype");
        if (classtype != null) {
            ret += classtype.toString() + "; ";
        }
        //rev
        RuleOption rev = rule.getRuleOption("rev");
        if (rev != null) {
            ret += rev.toString() + "; ";
        }
        //sid
        RuleOption sid = rule.getRuleOption("sid");
        if (sid != null) {
            ret += sid.toString() + ";)";
        }
        return ret;
    }

    private void outputLinkedIndex(WritableWorkbook workbook) throws WriteException {
        LinkedList<RuleHeader> lheader = this.getHeader();

        LinkedList<String> lsheader1 = new LinkedList<String>();
        for (int i = 0; i < lheader.size(); i++) {

            lsheader1.add(lheader.get(i).value);
        }
        //reduce header 1
        for (int i = 0; i < lsheader1.size() - 1; i++) {
            String s = lsheader1.get(i);
            for (int j = i + 1; j < lsheader1.size();) {
                String s1 = lsheader1.get(j);
                if (s.compareToIgnoreCase(s1) == 0) {
                    lsheader1.remove(j);
                } else {
                    j++;
                }
            }
        }

        WritableSheet sheet = workbook.createSheet("LinkedIndex", 2); // sheet name
        for (int i = 0; i < lpcre.size(); i++) {
            Label label;
            //Global Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //IndexPcre
            label = new Label(1, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Index header
            PCRE pcre = this.lpcre.get(i);
            int index;
            index = lsheader1.indexOf(pcre.getRule().header.value);
            label = new Label(2, i + 1, "" + (index + 1));
            sheet.addCell(label);
            //SID
            label = new Label(3, i + 1, pcre.getSID());
            sheet.addCell(label);
        }

        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Global Index; IndexPCRE; IndexHeader; SID";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
        sheet = workbook.createSheet("LinkedIndexHeader", 3); // sheet name
        for (int i = 0; i < lsheader1.size(); i++) {
            Label label;
            //Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Header
            label = new Label(1, i + 1, lsheader1.get(i));
            sheet.addCell(label);
        }

        //write titles of columns
        wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        w1 = new WritableCellFormat(wf);
        titles = "IndexHeader; Header";
        atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }

        //output file for Mr Loc
        this.out4Loc(this.outputfolder + "4loc.index", lsheader1, lpcre);

    }

    private void out4Loc(String string, LinkedList<String> lsheader1, LinkedList<PCRE> lpcre) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(string));
            for (int i = 0; i < lpcre.size(); i++) {
                //4; alert; 2;  4 ;
                bw.write((i + 1) + "; alert; " + (lsheader1.indexOf(lpcre.get(i).getRule().header.value) + 1) + "; ; " + (i + 1) + "\n");
            }

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Extractor_21_12_2010.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean checkSuportbySoftware(PCRE pcre) {

        if (this.checkEnd(pcre) > 0) {
            return false;
        }
        if (this.checkSubQues(pcre) > 0) {
            return false;
        }
        if (this.checkSubBrace(pcre) > 0) {
            return false;
        }
        if (this.checkSubOR(pcre) > 0) {
            return false;
        }
        if (this.checkORSub(pcre) > 0) {
            return false;
        }
        if (this.checkBR(pcre) > 0) {
            return false;
        }
        if (pcre.isReverse) {
            return false;
        }
        return true;
    }

    private int checkAND(PCRE pcre) {
        return References.countPcreElement(pcre);
    }

    private int checkOR(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '|' && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return count;
    }

    private int checkStar(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '*' && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return count;
    }

    private int checkPlus(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '+' && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return count;
    }

    private int checkQuestion(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '?' && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return count;
    }

    private int checkCR(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        int[] cr = new int[4];
        cr = this.countConstraint(s);
        for (int i = 0; i < 4; i++) {
            count += cr[i];
        }
        return count;
    }

    private int checkStart(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '^') {
                if (i == 0) {
                    count++;
                } else if (s.charAt(i - 1) != '\\' && s.charAt(i - 1) != '[') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkBR(PCRE rule) {
        int count = 0;
        for (int i = 0; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '\\') {
                char chr = rule.regex.charAt(i + 1);
                if (chr > '0' && chr < '9') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkSubQues(PCRE rule) {  //(?
        int count = 0;
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '(') {
                if (rule.regex.charAt(i - 1) != '\\' && rule.regex.charAt(i + 1) == '?') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkSubBrace(PCRE rule) {  //({
        int count = 0;
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '(') {
                if (rule.regex.charAt(i - 1) != '\\' && rule.regex.charAt(i + 1) == '{') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkSubOR(PCRE rule) {  //(|
        int count = 0;
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '(') {
                if (rule.regex.charAt(i - 1) != '\\' && rule.regex.charAt(i + 1) == '|') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkORSub(PCRE rule) {
        int count = 0;
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '|') {
                if (rule.regex.charAt(i - 1) != '\\' && rule.regex.charAt(i + 1) == ')') {
                    count++;
                }
            }
        }
        return count;
    }

    private int checkEnd(PCRE pcre) {
        String s = pcre.regex;
        int count = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '$' && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param pcre
     * @return
     * exact  between atleas atmost
     * 0        1       2       3
     */
    public int[] countConstraint(String s) {
        int[] ret = new int[4];

        for (int i = 1; i < s.length() - 2; i++) {
            char ch = s.charAt(i);
            if (ch == '{') {
                char ch0 = s.charAt(i - 1);
                if (ch0 != '\\') {
                    int index = References.getIndexOBlock(s.substring(i), '{', '}');
                    String ss = s.substring(i + 1, i + index);
                    System.out.println(ss);
                    if (ss.startsWith(",")) {
                        ret[3]++;
                    } else if (ss.endsWith(",")) {
                        ret[2]++;
                    } else if (Character.isDigit(ss.charAt(0))) {
                        if (ss.indexOf(",") >= 0) {
                            ret[1]++;
                        } else {
                            ret[0]++;
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        return ret;
    }
}
