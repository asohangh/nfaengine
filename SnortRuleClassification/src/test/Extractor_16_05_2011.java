/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

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
import snort_rule.*;

/**
 *
 * @author heckarim
 */
public class Extractor_16_05_2011 {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        Extractor_16_05_2011 ex = new Extractor_16_05_2011();
        ex.Action();
    }

    /**
     *
     *
     *
     *
     */
    private void Action() throws IOException, WriteException {
        String rulefolder = System.getProperty("user.dir") + File.separator + "rules.2.9" + File.separator;
        db = new RuleDatabase(rulefolder);
        db.BuildDatabase();
        this.outputExcel();
        //this.outputTestingRulesFile("simple.nids.1.rules");
        //this.outputRules(this.outputfolder + "nids.rules");


    }

    private void outputExcel() {
        try {
            this.outputExcelExtraction("snort2.9.extraction.xls");
            this.outputExcelStatistic("snort2.9.statistic.xls");

        } catch (IOException ex) {
            Logger.getLogger(Extractor_16_05_2011.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(Extractor_16_05_2011.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void outputExcelExtraction(String filename) throws IOException, WriteException {
        // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(this.outputfolder + filename));
        //1. Rule id references.
        this.outExcelReferences(workbook, "references");
        //2. pcre references.
        this.outExcelAllPCRE(workbook, "allPcre");
        //3. pcre by rulerset
        this.outExcelRulesetPCRE(workbook);

        //close the all opened connections
        workbook.write();
        workbook.close();
    }

    private void outputExcelStatistic(String filename) throws IOException, WriteException {
        // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(this.outputfolder + filename));
        //create general
        this.outputGeneralStatistic(workbook, "general");
        //couting content, uri, pcre
        this.outputCountCUPStatistic(workbook, "CUP");


        //close the all opened connections
        workbook.write();
        workbook.close();
    }

    private void outExcelReferences(WritableWorkbook workbook, String name) throws WriteException {
        //LinkedList<PCRE> lp = this.getSupportedPCRE();
        //LinkedList<PCRE> rlp = this.reducePCRE(lp);
        //this.lpcre = this.morePCREFilter(rlp);
        //this.lpcre = lp;
        WritableSheet sheet = workbook.createSheet(name, 1); // sheet name

        //write pcre
        int index = 0;//this.lcontent.size();
        for (int i = 0; i < this.db.lstRuleAll.size(); i++) {
            Label label;
            Number nb;
            RuleComponent con = this.db.lstRuleAll.get(i);
            //Index
            nb = new Number(0, i + 1,  (i + 1));
            sheet.addCell(nb);
            //SID
            label = new Label(1, i + 1, con.sid);
            sheet.addCell(label);
            //RuleSets
            label = new Label(2, i + 1, con.ruleSet.name);
            sheet.addCell(label);
            //Rule
            label = new Label(3, i + 1, con.value);
            sheet.addCell(label);

        }

        //write titles of columns
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; SID; RuleSets; Rule";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void outExcelAllPCRE(WritableWorkbook workbook, String string) throws WriteException {

        WritableSheet sheet = workbook.createSheet(string, 2);
        LinkedList<PCRE> allPcre = this.getAllPCRE();
        int i = 0;
        for (i = 0; i < allPcre.size(); i++) {
            PCRE pcre = allPcre.get(i);
            Label lb;
            Number nb;
            //Index
            nb = new Number(0, i + 1,  (i + 1));
            sheet.addCell(nb);
            //SID
            lb = new Label(1, i + 1, pcre.getSID());
            sheet.addCell(lb);
            //RuleSets
            lb = new Label(2, i + 1, pcre.getRule().ruleSet.name);
            sheet.addCell(lb);
            //Support
            int support = 0;
            if (References.isSupportablePCRE(pcre)) {
                support = 1;
            }
            nb = new Number(3, i + 1,  support);
            sheet.addCell(nb);
            //PCRE
            lb = new Label(4, i + 1, pcre.toString());
            sheet.addCell(lb);

        }

        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; SID; RuleSets; Support; PCRE";
        String[] atitle = titles.split("; ");
        for (i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private LinkedList<PCRE> getAllPCRE() {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> rPcre = new LinkedList<PCRE>();

        // Extract all pcre from rule set to simple.pcre
        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                    //bw.write(rs.lstRuleAll.get(j).value + "\n");
                    LinkedList<PCRE> tPcre = rs.lstRuleAll.get(j).getOpPcre();
                    rPcre.addAll(tPcre);
                }
            }
        }
        return rPcre;
    }

    /**
     *
     * @param workbook
     * @param sheetname
     * @param listrule
     * @param exportRule     need to export all content of rule.?
     * @throws WriteException
     */
    private void writeSheetRule(WritableWorkbook workbook, String sheetname, LinkedList<RuleComponent> listrule, boolean exportRule) throws WriteException {
        WritableSheet sheet = workbook.createSheet(sheetname, 2);
        int i = 0;
        for (i = 0; i < listrule.size(); i++) {
            RuleComponent rule = listrule.get(i);
            Label lb;
            Number nb;
            //Index
            nb = new Number(0, i + 1,  (i + 1));
            sheet.addCell(nb);
            //SID
            lb = new Label(1, i + 1, rule.sid);
            sheet.addCell(lb);
            //RuleSets
            lb = new Label(2, i + 1, rule.ruleSet.name);
            sheet.addCell(lb);
            //Rule
            if (exportRule) {
                lb = new Label(3, i + 1, rule.value);
                sheet.addCell(lb);
            }
        }

        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; SID; RuleSets; Rule";
        String[] atitle = titles.split("; ");
        for (i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void writeSheetPCRE(WritableWorkbook workbook, String sheetname, int pos, LinkedList<PCRE> listpcre, boolean exportRule) throws WriteException {
        WritableSheet sheet = workbook.createSheet(sheetname, pos);
        int i = 0;
        for (i = 0; i < listpcre.size(); i++) {
            PCRE pcre = listpcre.get(i);
            Label lb;
            Number nb;
            //Index
            nb = new Number(0, i + 1,  (i + 1));
            sheet.addCell(nb);
            //SID
            lb = new Label(1, i + 1, pcre.getSID());
            sheet.addCell(lb);
            //RuleSets
            lb = new Label(2, i + 1, pcre.getRule().ruleSet.name);
            sheet.addCell(lb);
            //Support
            int support = 0;
            if (References.isSupportablePCRE(pcre)) {
                support = 1;
            }
            nb = new Number(3, i + 1,  support);
            sheet.addCell(nb);
            //PCRE
            lb = new Label(4, i + 1, pcre.toString());
            sheet.addCell(lb);
            //Rule
            if (exportRule) {
                lb = new Label(5, i + 1, pcre.getRule().value);
                sheet.addCell(lb);
            }

        }

        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; SID; RuleSets; Support; PCRE; Rule";
        String[] atitle = titles.split("; ");
        for (i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private void outputGeneralStatistic(WritableWorkbook workbook, String name) throws WriteException {

        WritableSheet sheet = workbook.createSheet(name, 1);
        int i = 0;
        for (i = 0; i < this.db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = this.db.lstSnortRuleSet.get(i);
            Label lb;
            Number nb;
            //Index
            nb = new Number(0, i + 1,  (i + 1));
            sheet.addCell(nb);
            //Rulerset
            lb = new Label(1, i + 1, rs.name);
            sheet.addCell(lb);
            //Active
            nb = new Number(2, i + 1,  rs.lstRuleActive.size());
            sheet.addCell(nb);
            //Inactive

            nb = new Number(3, i + 1,  rs.lstRuleInactive.size());
            sheet.addCell(nb);
            //Total
            nb = new Number(4, i + 1,  (rs.lstRuleActive.size() + rs.lstRuleInactive.size()));
            sheet.addCell(nb);
        }
        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Index; RuleSet; active; inactive; total";
        String[] atitle = titles.split("; ");
        for (i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }

    }

    private void outputCountCUPStatistic(WritableWorkbook workbook, String name) throws WriteException {
        WritableSheet sheet = workbook.createSheet(name, 1);
        LinkedList<RuleComponent> lrules;
        LinkedList<PCRE> lpcre;
        Label lb;
        Number nb;
        int index = 0;
        //calculate data.
        //------------------------------------------------------
        //0 C _ 0 U _ 0 P
        //-----------------------------------------------------
        lrules = this.getRules_0C0U0P();
        index++;
        //Content
        lb = new Label(0, index, "0");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "0");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lrules.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "0c0u0p");
        sheet.addCell(lb);
        this.writeSheetRule(workbook, "0c0u0p", lrules, true);

        //------------------------------------------------------
        //0 C _ 0 U _ 1 P
        //-----------------------------------------------------
        lpcre = this.getRules_0C0U1P();
        index++;
        //Content
        lb = new Label(0, index, "0");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "1");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lpcre.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "0c0u1p");
        sheet.addCell(lb);
        this.writeSheetPCRE(workbook, "0c0u1p", 4, lpcre, true);
        //------------------------------------------------------
        //0 C _ 0 U _ n P
        //-----------------------------------------------------
        lpcre = this.getRules_0C0UnP();
        index++;
        //Content
        lb = new Label(0, index, "0");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "n");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lpcre.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "0c0unp");
        sheet.addCell(lb);
        this.writeSheetPCRE(workbook, "0c0unp", 5, lpcre, true);
        //------------------------------------------------------
        //? C _ + U _ ? P
        //-----------------------------------------------------
        lrules = this.getRules__CiU_P();
        index++;
        //Content
        lb = new Label(0, index, "?");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "+");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "?");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lrules.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "_ciu_p");
        sheet.addCell(lb);
        this.writeSheetRule(workbook, "_ciu_p", lrules, true);
        //------------------------------------------------------
        //+ C _ 0 U _ 0 P
        //-----------------------------------------------------
        lrules = this.getRules_iC0U0P();
        index++;
        //Content
        lb = new Label(0, index, "+");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "0");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lrules.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "ic0u0p");
        sheet.addCell(lb);

        this.writeSheetRule(workbook, "ic0u0p", lrules, true);
        //------------------------------------------------------
        //+ C _ 0 U _ 1 P
        //-----------------------------------------------------
        lpcre = this.getRules_iC0U1P();
        index++;
        //Content
        lb = new Label(0, index, "+");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "1");
        sheet.addCell(lb);
        //count
       nb = new Number(3, index,lpcre.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "ic0u1p");
        sheet.addCell(lb);
        this.writeSheetPCRE(workbook, "ic0u1p", 7, lpcre, true);
        //------------------------------------------------------
        //+ C _ 0 U _ n P
        //-----------------------------------------------------
        lpcre = this.getRules_iC0UnP();
        index++;
        //Content
        lb = new Label(0, index, "+");
        sheet.addCell(lb);
        //URI
        lb = new Label(1, index, "0");
        sheet.addCell(lb);
        //PCRE
        lb = new Label(2, index, "n");
        sheet.addCell(lb);
        //count
        nb = new Number(3, index,lpcre.size());
        sheet.addCell(nb);
        //sheet name
        lb = new Label(4, index, "ic0unp");
        sheet.addCell(lb);
        this.writeSheetPCRE(workbook, "ic0unp", 8, lpcre, true);
        //------------------------------------------------------

        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "Content; URI; PCRE; No;";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private LinkedList<RuleComponent> getRules_0C0U0P() {
        //all rule don't have content and uricontent and pcre
        LinkedList<RuleComponent> lrules = new LinkedList<RuleComponent>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetForbid("content");
        mask.SetForbid("uricontent");
        mask.SetForbid("pcre");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    lrules.add(rs.lstRuleAll.get(j));
                }
            }
        }
        return lrules;
    }

    private LinkedList<PCRE> getRules_0C0U1P() {
        //all rule don't have content and uricontent and pcre
        LinkedList<PCRE> lstpcre = new LinkedList<PCRE>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetForbid("content");
        mask.SetForbid("uricontent");
        mask.SetPermit("pcre");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    LinkedList<PCRE> tpcre = rs.lstRuleAll.get(j).getOpPcre();
                    if (tpcre.size() == 1) {
                        lstpcre.add(tpcre.getFirst());
                    }
                }
            }
        }
        return lstpcre;
    }

    private LinkedList<PCRE> getRules_0C0UnP() {
        //all rule don't have content and uricontent and pcre
        LinkedList<PCRE> lstpcre = new LinkedList<PCRE>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetForbid("content");
        mask.SetForbid("uricontent");
        mask.SetPermit("pcre");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    LinkedList<PCRE> tpcre = rs.lstRuleAll.get(j).getOpPcre();
                    if (tpcre.size() > 1) {
                        lstpcre.add(tpcre.getFirst());
                    }
                }
            }
        }
        return lstpcre;
    }

    private LinkedList<RuleComponent> getRules__CiU_P() {
        //all rule don't have content and uricontent and pcre
        LinkedList<RuleComponent> lrules = new LinkedList<RuleComponent>();
        //create mask
        OptionMask mask = new OptionMask();

        mask.SetPermit("uricontent");


        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    lrules.add(rs.lstRuleAll.get(j));
                }
            }
        }
        return lrules;
    }

    private LinkedList<RuleComponent> getRules_iC0U0P() {
        //all rule don't have content and uricontent and pcre
        LinkedList<RuleComponent> lrules = new LinkedList<RuleComponent>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetPermit("content");
        mask.SetForbid("uricontent");
        mask.SetForbid("uricontent");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    lrules.add(rs.lstRuleAll.get(j));
                }
            }
        }
        return lrules;
    }

    private LinkedList<PCRE> getRules_iC0U1P() {
        //all rule don't have content and uricontent and pcre
        LinkedList<PCRE> lstpcre = new LinkedList<PCRE>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetPermit("content");
        mask.SetForbid("uricontent");
        mask.SetPermit("pcre");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    LinkedList<PCRE> tpcre = rs.lstRuleAll.get(j).getOpPcre();
                    if (tpcre.size() == 1) {
                        lstpcre.add(tpcre.getFirst());
                    }
                }
            }
        }
        return lstpcre;
    }

    private LinkedList<PCRE> getRules_iC0UnP() {
        //all rule don't have content and uricontent and pcre
        LinkedList<PCRE> lstpcre = new LinkedList<PCRE>();
        //create mask
        OptionMask mask = new OptionMask();
        mask.SetPermit("content");
        mask.SetForbid("uricontent");
        mask.SetPermit("pcre");

        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            int count = 0;
            RuleSet rs = db.lstSnortRuleSet.get(i);
            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                    LinkedList<PCRE> tpcre = rs.lstRuleAll.get(j).getOpPcre();
                    if (tpcre.size() > 1) {
                        lstpcre.addAll(tpcre);
                    }
                }
            }
        }
        return lstpcre;
    }

    private void outExcelRulesetPCRE(WritableWorkbook workbook) throws WriteException {

        LinkedList<PCRE> lpcre;
        WritableSheet sheet = workbook.createSheet("byRuleSet", 3);
        //Integer countsupport[] = new Integer[this.db.lstSnortRuleSet.size()];
        int support;
        for (int i = 0; i < this.db.lstSnortRuleSet.size(); i++) {
            RuleSet ruleset = this.db.lstSnortRuleSet.get(i);
            lpcre = this.getPCRERuleSet(ruleset);
            //cout support
            support = 0;
            for (int j = 0; j < lpcre.size(); j++) {
                if (References.isSupportablePCRE(lpcre.get(j))) {
                    support++;
                }
            }
            Label lb;
            Number nb;
            //Index
            nb = new Number(0, i + 1, (i + 1));
            sheet.addCell(nb);
            //Ruleset
            lb = new Label(1, i + 1, ruleset.name);
            sheet.addCell(lb);
            //Total
            nb = new Number(2, i + 1, lpcre.size());
            sheet.addCell(nb);
            //Support
            nb = new Number(3, i + 1, support);
            sheet.addCell(nb);
            //Unsupport
            nb = new Number(4, i + 1, (lpcre.size() - support));
            sheet.addCell(nb);

            //wirte to neew sheet
            if (!lpcre.isEmpty()) {
                this.writeSheetPCRE(workbook, ruleset.name, i + 3, lpcre, false);
            }
        }

        //title of colums
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        WritableCellFormat w1 = new WritableCellFormat(wf);
        String titles = "index; ruleset; total; suport; unsuport";
        String[] atitle = titles.split("; ");
        for (int i = 0; i < atitle.length; i++) {
            Label label = new Label(i, 0, atitle[i], w1);
            sheet.addCell(label);
        }
    }

    private LinkedList<PCRE> getPCRERuleSet(RuleSet rs) {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> rPcre = new LinkedList<PCRE>();
        for (int j = 0; j < rs.lstRuleAll.size(); j++) {
            if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                //bw.write(rs.lstRuleAll.get(j).value + "\n");
                LinkedList<PCRE> tPcre = rs.lstRuleAll.get(j).getOpPcre();
                rPcre.addAll(tPcre);
            }
        }
        return rPcre;
    }
}
