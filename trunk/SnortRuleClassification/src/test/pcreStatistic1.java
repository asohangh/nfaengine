/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import PcreParseTree.PcreRule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.Option;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import VRTSignature.*;
import VRTSignature.PCRE;

/**
 *
 * @author heckarim
 *
 */
public class pcreStatistic1 {

    LinkedList<PCRE> lpcre;
    String outputfolder;
    RuleDatabase db;
    LinkedList<PCRE> ltemp;

    class operatorCount {

        public String _operator = "., |, *, +, ?, {}, ^, $, (?, ({, (|, \\K";
        public String[] soperator = _operator.split(", ");
        public String dataset = null;
        public int[] opcount = new int[soperator.length]; // only rule have operator
        public int[] opcountall = new int[soperator.length];//count all operator in
    }

    public static void main(String[] args) {
        new pcreStatistic1().action();
    }

    public void action() {
        //outputfolder = System.getProperty("user.dir") + File.separator + "output" + File.separator;
        outputfolder = System.getProperty("user.dir") + File.separator + "output" + File.separator;
        db = new RuleDatabase();
        db.buildDatabase();

        this.outputOperatorCount(outputfolder + "all.pcre.rule29.operator.count");
        this.statisticSupportableSoftware(this.outputfolder + "StatisticPcreSuported.xls");


        this.doExtractAllpcre();
        this.doCountConstraint(outputfolder + "all.pcre.rule29.constraint.count");



    }

    private void outputOperatorCount(String file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            LinkedList<operatorCount> lcount = this.countOperator();


            operatorCount oc = new operatorCount();
            bw.write("Dataset\t");
            for (int i = 0; i < oc.opcount.length; i++) {
                bw.write(oc.soperator[i] + "\t");
            }

            bw.write("\n");
            for (int i = 0; i < lcount.size(); i++) {
                operatorCount count = lcount.get(i);
                bw.write(count.dataset.replaceAll(".rulex", "") + "\t");
                for (int j = 0; j < count.opcount.length; j++) {
                    bw.write(count.opcount[j] + "\t");
                }
                bw.write("\n");
            }

            //write in horizontal
            bw.write("\n\n# write in horizontal\n");


            bw.write("Opeartor\t");

            for (int i = 0; i < lcount.size(); i++) {
                bw.write(lcount.get(i).dataset.replaceAll(".rulex", "") + "\t");
            }
            for (int i = 0; i < oc.soperator.length; i++) {
                bw.write(oc.soperator[i] + "\t");
                for (int j = 0; j < lcount.size(); j++) {
                    operatorCount count = lcount.get(j);
                    bw.write(count.opcount[i] + "\t");
                }
                bw.write("\n");
            }
            bw.write("\n\n# write in horizontal all of ...\n");


            bw.write("Opeartor\t");

            for (int i = 0; i < lcount.size(); i++) {
                bw.write(lcount.get(i).dataset.replaceAll(".rulex", "") + "\t");
            }
            for (int i = 0; i < oc.soperator.length; i++) {
                bw.write(oc.soperator[i] + "\t");
                for (int j = 0; j < lcount.size(); j++) {
                    operatorCount count = lcount.get(j);
                    bw.write(count.opcountall[i] + "\t");
                }
                bw.write("\n");
            }

            bw.write("......." + ltemp.size() + "  \n");
            for (int i = 0; i < ltemp.size(); i++) {
                bw.write(ltemp.get(i).regex + "\n");
            }
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(pcreStatistic1.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private LinkedList<operatorCount> countOperator() {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<operatorCount> locount = new LinkedList<operatorCount>();
        ltemp = new LinkedList<PCRE>();
        // Extract all pcre from rule set to simple.pcre
        for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
            RuleSet rs = db.lstSnortRuleSet.get(i);
            operatorCount opcount = new operatorCount();
            opcount.dataset = rs.name;

            for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                    //bw.write(rs.lstRuleAll.get(j).value + "\n");
                    PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getFirstOpPcre();
                    this.countOperatorPCRE(pcre, opcount);
                }
            }
            locount.add(opcount);
        }
        return locount;
    }
    /*
     *                      "., |, *, +, ?, {}, ^, $, (?, ({, (|, \\K";
     *                       0  1  2  3  4  5  6  7   8  9   10  11   12
     */

    private void countOperatorPCRE(PCRE pcre, operatorCount opcount) {
        if (this.checkAND(pcre) > 0) {
            opcount.opcount[0]++;
            opcount.opcountall[0] += this.checkAND(pcre);
        } else {
            ltemp.add(pcre);
        }
        if (this.checkOR(pcre) > 0) {
            opcount.opcount[1]++;
            opcount.opcountall[1] += this.checkOR(pcre);
        }
        if (this.checkStar(pcre) > 0) {
            opcount.opcount[2]++;
            opcount.opcountall[2] += this.checkStar(pcre);
        }
        if (this.checkPlus(pcre) > 0) {
            opcount.opcount[3]++;
            opcount.opcountall[3] += this.checkPlus(pcre);
        }
        if (this.checkQuestion(pcre) > 0) {
            opcount.opcount[4]++;
            opcount.opcountall[4] += this.checkQuestion(pcre);
        }
        if (this.checkCR(pcre) > 0) {
            opcount.opcount[5]++;
            opcount.opcountall[5] += this.checkCR(pcre);
        }
        if (this.checkStart(pcre) > 0) {
            opcount.opcount[6]++;
            opcount.opcountall[6] += this.checkStart(pcre);
        }
        if (this.checkEnd(pcre) > 0) {
            opcount.opcount[7]++;
            opcount.opcountall[7] += this.checkEnd(pcre);
        }
        if (this.checkSubQues(pcre) > 0) {
            opcount.opcount[8]++;
            opcount.opcountall[8] += this.checkSubQues(pcre);
        }
        if (this.checkSubBrace(pcre) > 0) {
            opcount.opcount[9]++;
            opcount.opcountall[9] += this.checkSubBrace(pcre);
        }
        if (this.checkSubOR(pcre) > 0) {
            opcount.opcount[10]++;
            opcount.opcountall[10] += this.checkSubOR(pcre);
        }
        if (this.checkBR(pcre) > 0) {
            opcount.opcount[11]++;
            opcount.opcountall[11] += this.checkBR(pcre);
        }

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

    private void doExtractAllpcre() {
        this.lpcre = new LinkedList<PCRE>();
        OptionMask om = new OptionMask();
        om.SetPermit("pcre");

        for (int i = 0; i < this.db.lstRuleAll.size(); i++) {
            RuleComponent rc = this.db.lstRuleAll.get(i);
            if (rc.ApplyMask(om)) {
                PCRE pcre = (PCRE) rc.getFirstOpPcre();
                this.lpcre.add(pcre);
            }
        }
    }

    private void doCountConstraint(String file) {
        int[] count = new int[4];
        int[] countRule = new int[4];
        for (int i = 0; i < 4; i++) {
            count[i] = 0;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < lpcre.size(); i++) {
                PcreRule p = new PcreRule(lpcre.get(i).toString());
                int[] ret = this.countConstraint(p.getRegex());
                for (int j = 0; j < 4; j++) {
                    count[j] += ret[j];
                }
            }
            bw.write("exact  between atleas atmost\n");
            for (int i = 0; i < 4; i++) {
                bw.write(count[i] + "\t");
            }
            bw.write("\n");
            //write number of rule
            for (int i = 0; i < this.db.lstRuleAll.size(); i++) {
            }



            //write number of rep
            int[] atleast = new int[3000];
            int[] exactly = new int[3000];
            for (int i = 0; i < 3000; i++) {
                atleast[i] = 0;
                exactly[i] = 0;
            }
            for (int j = 0; j < lpcre.size(); j++) {
                PcreRule p = new PcreRule(lpcre.get(j).toString());
                String s = p.getRegex();

                for (int i = 1; i < s.length() - 2; i++) {
                    char ch = s.charAt(i);
                    if (ch == '{') {
                        char ch0 = s.charAt(i - 1);
                        if (ch0 != '\\') {
                            int index = References.getIndexOBlock(s.substring(i), '{', '}');
                            String ss = s.substring(i + 1, i + index);
                            System.out.println(ss);
                            if (ss.startsWith(",")) {//atmost
                            } else if (ss.endsWith(",")) {//atleast
                                int in = Integer.parseInt(ss.substring(0, ss.length() - 1));
                                atleast[in]++;
                            } else if (Character.isDigit(ss.charAt(0))) {
                                if (ss.indexOf(",") >= 0) {//between
                                } else { // exactly.
                                    int in = Integer.parseInt(ss);
                                    exactly[in]++;
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            bw.write("\ncount \t Atleast \t exactly\n");

            for (int i = 0; i < 3000; i++) {
                if (atleast[i] != 0 || exactly[i] != 0) {
                    bw.write(i + "\t" + atleast[i] + "\t" + exactly[i] + "\n");
                }
            }
            //Do count constraint by rule
            OptionMask mask = new OptionMask();
            mask.SetPermit("pcre");
            int[] countrulecrb = new int[4];
            for (int i = 0; i < 4; i++) {
                countrulecrb[i] = 0;
            }
            for (int i = 0; i < this.db.lstRuleAll.size(); i++) {
                RuleComponent rc = this.db.lstRuleAll.get(i);
                if (rc.ApplyMask(mask)) {
                    PCRE pcre = (PCRE) rc.getFirstOpPcre();
                    int[] retcr = this.countConstraint(pcre.regex);
                    for (int j = 0; j < 4; j++) {
                        if (retcr[j] > 0) {
                            countrulecrb[j]++;
                        }
                    }
                }
            }
            bw.write("\n Count by rule\n");
            bw.write("exact  between atleas atmost\n");
            for (int i = 0; i < 4; i++) {
                bw.write(countrulecrb[i] + "\t");
            }


            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    class rulesetPcresuport {

        String dataset;
        public LinkedList<PCRE> lpcre = new LinkedList<PCRE>();

        public void addpcre(PCRE pcre) {
            this.lpcre.add(pcre);
        }

        public void setDataset(String dataset) {
            this.dataset = dataset;
        }
    }

    public void statisticSupportableSoftware(String filename) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(filename));

            LinkedList<rulesetPcresuport> lrulesup = new LinkedList<rulesetPcresuport>();
            OptionMask om = new OptionMask();
            om.SetPermit("pcre");
            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                RuleSet rs = db.lstSnortRuleSet.get(i);
                rulesetPcresuport rulesup = new rulesetPcresuport();
                rulesup.setDataset(rs.name);

                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    RuleComponent rc = rs.lstRuleAll.get(j);
                    if (rc.ApplyMask(om)) {
                        PCRE pcre = (PCRE) rc.getFirstOpPcre();
                        if (this.checkSuportbySoftware(pcre)) {
                            rulesup.addpcre(pcre);
                        }
                    }
                }
                lrulesup.add(rulesup);
            }
            //create statistic
            WritableSheet sheet = workbook.createSheet("Statistic", 0); // sheet name
            Label label;
            //write title
            label = new Label(0, 0, "Ruleset");
            sheet.addCell(label);
            label = new Label(1, 0, "Support");
            sheet.addCell(label);
            for (int i = 0; i < lrulesup.size(); i++) {
                label = new Label(0, i + 1, lrulesup.get(i).dataset.replaceAll(".rules", ""));
                sheet.addCell(label);
                label = new Label(1, i + 1, lrulesup.get(i).lpcre.size() + "");
                sheet.addCell(label);
            }

            //create rules file.
            sheet = workbook.createSheet("By Ruleset", 1);
            int row = 0;
            int ramindex = 0;
            for (int i = 0; i < lrulesup.size(); i++) {
                rulesetPcresuport ruleset = lrulesup.get(i);
                label = new Label(0, row, ruleset.dataset);
                sheet.addCell(label);
                if (ruleset.lpcre.size() > 0) {
                    label = new Label(1, row, "#bram" + ramindex + " " + ruleset.dataset);
                    sheet.addCell(label);
                    ramindex++;
                }
                row++;
                //wire suported rule
                for (int j = 0; j < ruleset.lpcre.size(); j++) {
                    PCRE pcre = ruleset.lpcre.get(j);
                    label = new Label(1, row, pcre.getOptionValue());
                    sheet.addCell(label);
                    row++;
                }
            }
            //creat index bram
            sheet = workbook.createSheet("BRams Rules", 2);
            row = 0;
            ramindex = 0;
            for (int i = 0; i < lrulesup.size(); i++) {
                rulesetPcresuport ruleset = lrulesup.get(i);
                label = new Label(0, row, ruleset.dataset);
                sheet.addCell(label);
                if (ruleset.lpcre.size() > 0) {
                    label = new Label(1, row, "#bram" + ramindex + " " + ruleset.dataset);
                    sheet.addCell(label);
                    ramindex++;
                }
                row++;
            }
            //Create sheet for suport pccre base on inactive and active.
            sheet = workbook.createSheet("SupportPcre", 3);
            int active = 0, inactive = 0;
            //get number of suport over active pcre

            for (int i = 0; i < this.db.lstRuleActive.size(); i++) {
                if (this.db.lstRuleActive.get(i).ApplyMask(om)) {
                    PCRE pcre = (PCRE) this.db.lstRuleActive.get(i).getFirstOpPcre();
                    if (this.checkSuportbySoftware(pcre)) {
                        active++;
                    }
                }
            }
            //get number of suport over inactive pcre

            for (int i = 0; i < this.db.lstRuleInactive.size(); i++) {
                if (this.db.lstRuleInactive.get(i).ApplyMask(om)) {
                    PCRE pcre = (PCRE) this.db.lstRuleInactive.get(i).getFirstOpPcre();
                    if (this.checkSuportbySoftware(pcre)) {
                        inactive++;
                    }
                }
            }
            label = new Label(0, 0, "Statistic number of supported pcre in inactive and active");
            sheet.addCell(label);
            label = new Label(0, 1, "active");
            sheet.addCell(label);
            label = new Label(0, 2, active+"");
            sheet.addCell(label);
            label = new Label(1, 1, "inactive");
            sheet.addCell(label);
            label = new Label(1, 2, inactive+"");
            sheet.addCell(label);


            //close the all opened connections
            workbook.write();
            workbook.close();
        } catch (WriteException ex) {
            Logger.getLogger(pcreStatistic1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(pcreStatistic1.class.getName()).log(Level.SEVERE, null, ex);
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
}
