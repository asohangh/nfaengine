/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

import VRTSignature.*;

/**
 *
 * @author heckarim
 */
public class Extractor_7_12_2010 {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<OpContent> lcontent= new LinkedList<OpContent>();
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint = new LinkedList<PCRE>();

    public static void main(String[] args) throws IOException, WriteException {
        Extractor_7_12_2010 ex = new Extractor_7_12_2010();
        ex.Action();
    }

    /**
     *
     *
     *
     *
     */
    private void Action() throws IOException, WriteException {
        db = new RuleDatabase();
        db.buildDatabase();

       // this.outputExcel();
        this.outputExcelPCRE();
        //this.outputTestingRulesFile("simple.nids.rules");
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
                if (temp.compareRegexTo(rpcre.get(j))) {
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
        this.lconstraint = new LinkedList<PCRE>();
        //todo
        for (int i = 0; i < lpcre.size(); i++) {
            PCRE pcre = lpcre.get(i);
            if (References.countPcreElement(pcre) >= 40) {
                continue;
            }
            if (References.isConstraintPCRE(pcre)) {
                this.lconstraint.add(pcre);
            } else {
                rpcre.add(pcre);
            }
        }
        return rpcre;
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
                    PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getFirstOpPcre();
                    if (References.isSupportablePCRE(pcre)) {
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
        return rcontent;
    }

    /**
     *
     * @return
     * Get simple content rule for process;
     * 
     * Option Reference:
     *  //String _allModified = "nocase; rawbytes; depth; offset; distance; within;
    //  http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header;
    //  http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode;
    //  fast_pattern";
     *
     */
    public LinkedList<OpContent> getContent() {
        LinkedList<OpContent> lcontent = new LinkedList<OpContent>();
        OptionMask mask = new OptionMask();
        mask.SetForbid(References._opContentModifier);
        mask.SetDontCare("nocase");
        mask.SetForbid("uricontent");
        mask.SetDontCare("rawbytes");
        mask.SetDontCare("fast_pattern");
        mask.SetPermit("content");
        mask.SetForbid("pcre");

        for (int j = 0; j < db.lstRuleAll.size(); j++) {
            if (db.lstRuleAll.get(j).ApplyMask(mask)) {
                //have one content
                if (db.lstRuleAll.get(j).ruleStatus.GetOptionStatus("content").count == 1) {
                    //only one content
                    lcontent.add(db.lstRuleAll.get(j).GetOpContent().getFirst());
                }
            }
        }
        return lcontent;
    }

    public void outputExcel() throws IOException, WriteException {

        // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(this.outputfolder + "Extractorv0.3.xls"));
        this.outputExcelContent(workbook);

        this.outputExcelPCRE(workbook);

        this.outputExcelHeader(workbook);

        //close the all opened connections
        workbook.write();
        workbook.close();
    }

    /**
     *
     * @param workbook
     * @throws WriteException
     *
     * output Sheet: Content
     */
    private void outputExcelContent(WritableWorkbook workbook) throws WriteException {
        /*
         * write content
         */
        LinkedList<OpContent> lc = this.getContent();
        LinkedList<OpContent> rlc = this.reduceContent(lc);
        lcontent = this.moreContentFilter(rlc);
        WritableSheet sheet = workbook.createSheet("Content", 0); // sheet name
        for (int i = 0; i < lcontent.size(); i++) {
            Label label;
            OpContent con = lcontent.get(i);
            //Local Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Global Index
            label = new Label(1, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Content
            label = new Label(2, i + 1, con.getOptionValue());
            sheet.addCell(label);
            //No Char
            label = new Label(3, i + 1, "" + con.countCharacter());
            sheet.addCell(label);
            //Modifier
            label = new Label(4, i + 1, con.getStringModifier());
            sheet.addCell(label);
            //SID
            label = new Label(5, i + 1, con.getSID());
            sheet.addCell(label);
            //Rule
            label = new Label(6, i + 1, con.getRule().value);
            sheet.addCell(label);

        }

        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Local Index; Global Index; Content; No Char; Modifier; SID; Rule";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void outputExcelPCRE(WritableWorkbook workbook) throws WriteException {
        LinkedList<PCRE> lp = this.getSupportedPCRE();
        //LinkedList<PCRE> rlp = this.reducePCRE(lp);
        //this.lpcre = this.morePCREFilter(rlp);
        this.lpcre = lp;
        WritableSheet sheet = workbook.createSheet("PCRE", 1); // sheet name

        //write pcre
        int index = 0;//this.lcontent.size();
        for (int i = 0; i < this.lpcre.size(); i++) {
            Label label;
            PCRE con = lpcre.get(i);
            //Local Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Global Index
            label = new Label(1, i + 1, "" + (index + i + 1));
            sheet.addCell(label);
            //PCRE
            label = new Label(2, i + 1, con.toString());
            sheet.addCell(label);
            //SID
            label = new Label(3, i + 1, con.getSID());
            sheet.addCell(label);
            //Rule
            label = new Label(4, i + 1, con.getRule().value);
            sheet.addCell(label);
        }
        index = index + this.lpcre.size();
        int i = this.lpcre.size() + 2;
        
        //write constraint
        for (int j = 0; j < this.lconstraint.size(); j++, i++) {
            Label label;
            PCRE con = lconstraint.get(j);
            //Local Index
            label = new Label(0, i + 1, "" + (i + 1));
            sheet.addCell(label);
            //Global Index
            label = new Label(1, i + 1, "" + (index + i + 1));
            sheet.addCell(label);
            //PCRE
            label = new Label(2, i + 1, con.toString());
            sheet.addCell(label);
            //SID
            label = new Label(3, i + 1, con.getSID());
            sheet.addCell(label);
            //Rule
            label = new Label(4, i + 1, con.getRule().value);
            sheet.addCell(label);
        }
        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Local Index; Global Index; PCRE; SID; Rule";
        String[] atitle = titles.split("; ");
        for (i = 0; i < atitle.length; i++) {
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
        for (int i = 0; i < this.lcontent.size(); i++) {
            rhead.add(lcontent.get(i).getRule().header);
        }
        // Get from lpcre
        for (int i = 0; i < this.lpcre.size(); i++) {
            rhead.add(lpcre.get(i).getRule().header);
        }
        return rhead;
    }

    private void outputFakeRules(String filename, LinkedList<OpContent> lcon, LinkedList<PCRE> lpcre) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.outputfolder + filename));
            bw.write("# SnortRule 2.9 for Testing NIDS Project on NetFPGA\n");
            bw.write("# Content Rules\n");
            //wirte content rule;
            for (int i = 0; i < lcon.size(); i++) {
                bw.write(this.formatFakeContentRule(lcon.get(i)));
                bw.write("\n");
            }
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

        //content
        LinkedList<OpContent> lc = this.getContent();
        LinkedList<OpContent> rlc = this.reduceContent(lc);
        LinkedList<OpContent> lcontent = this.moreContentFilter(rlc);
        //pcre
        LinkedList<PCRE> lp = this.getSupportedPCRE();
        LinkedList<PCRE> rlp = this.reducePCRE(lp);
        LinkedList<PCRE> lpcre = this.morePCREFilter(rlp);

        this.outputFakeRules(filename, lc, lpcre);

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

    private void outputRules(String filename) {
        try {
            //content
            LinkedList<OpContent> lc = this.getContent();
            LinkedList<OpContent> rlc = this.reduceContent(lc);
            lcontent = this.moreContentFilter(rlc);
            //pcre
            LinkedList<PCRE> lp = this.getSupportedPCRE();
            LinkedList<PCRE> rlp = this.reducePCRE(lp);
            lpcre = this.morePCREFilter(rlp);

            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write("# SnortRule 2.9 for Testing NIDS Project on NetFPGA\n");
            bw.write("# Content Rules\n");
            //wirte content rule;
            for (int i = 0; i < this.lcontent.size(); i++) {
                bw.write(this.lcontent.get(i).getRule().value);
                bw.write("\n");
            }
            //write pcre rule;
            for (int i = 0; i < this.lpcre.size(); i++) {
                bw.write(this.lpcre.get(i).getRule().value);
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Extractor_7_12_2010.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *  this function will return rule only have Conten
     * @param get
     * @return
     *  +, option:  msg, content, sid.
     *
     *
     * sample:
     *  alert udp $EXTERNAL_NET 3344 -> $HOME_NET 3345 (msg:"BACKDOOR Matrix 2.0 Client connect"; flow:to_server; content:"activate"; reference:arachnids,83; classtype:misc-activity; sid:161; rev:7;)
    msg, content, reference, classtype,sid, rev;
     *
     *  NOTE: (todo).
     *      +, don't have metadata
     *      +, many reference isn't support.
     */
    private String formatFakeContentRule(OpContent con) {
        String ret = "";
        RuleComponent rule = con.getRule();
        ret = rule.header.value + " " + "(";
        //msg
        RuleOption msg = rule.getRuleOption("msg");
        if (msg != null) {
            ret += msg.toString() + "; ";
        }
        //content
        ret += con.getOptionString() + "; ";
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

    private void outputExcelPCRE() throws IOException, WriteException {
         // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(this.outputfolder + "Extractorv1.0.xls"));

        this.outputExcelPCRE(workbook);

        //close the all opened connections
        workbook.write();
        workbook.close();
    }
}
