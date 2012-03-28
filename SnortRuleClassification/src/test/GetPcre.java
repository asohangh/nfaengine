/*
 * This class for get PCRE for further process.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import VRTSignature.*;
import VRTSignature.RuleDatabase;

/**
 *
 * @author heckarim
 */
public class GetPcre {

    String outputfolder;
    RuleDatabase db;

    public static void main(String[] args) {
        GetPcre gpcre = new GetPcre();
        gpcre.outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
        gpcre.action();
        gpcre.pcreStatistic(gpcre.outputfolder+"pcrestatistic.thesis");
        //gpcre.getAllPCRE();
        //gpcre.getSimplePCRE();
        //gpcre.getSupportedPCRE();
    }

    /**
     *           // Extract all pcre from rule set to all.pcre
     *          // Extract all pcre from ruleset and count the number of content in each rule have pcre.
     *          // it will extract all pcre to file, using writepcretofile from Refrence
     *
     */
    private void action() {
        db = new RuleDatabase();
        db.buildDatabase();
        //String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");

        try {
            // Extract all pcre from rule set to all.pcre
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "all.pcre"));
            bw.write("Malicious Rules: " + db.lstSnortRuleSet.size() + "\n");
            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                bw.write("\n#" + db.lstSnortRuleSet.get(i).name + "\n");
                RuleSet rs = db.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    if (rs.lstRuleAll.get(j).ApplyMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        bw.write(rs.lstRuleAll.get(j).getFirstOpPcre().toString() + "\n");
                    }
                }
            }
            bw.flush();
            bw.close();

// Extract all pcre from ruleset and count the number of content in each rule have pcre.
// as i know there are maximum of 13 content in one snort rule, so let consider the array of 20pcre.

            bw = new BufferedWriter(new FileWriter(outputfolder + "all.count.pcre"));
            //create 20 linkedlist for store rulecomponent;
            int[] arrContent = new int[20];
            LinkedList<RuleComponent>[] arrRule = new LinkedList[20];
            for (int i = 0; i < arrRule.length; i++) {
                arrRule[i] = new LinkedList<RuleComponent>();
            }

            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                RuleSet rs = db.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    int noContent = 0;
                    if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {

                        if (rs.lstRuleAll.get(j).GetOpContent() != null) {
                            noContent = rs.lstRuleAll.get(j).GetOpContent().size();
                        }
                        arrContent[noContent]++;
                        //add it to linkedlist rule
                        arrRule[noContent].add(rs.lstRuleAll.get(j));

                        //bw.write(rs.lstRuleAll.get(j).getOpPcre().toString() + "\n");
                    }
                }
            }
            for (int i = 0; i < arrRule.length; i++) {
                bw.write("# noContent: " + i + "\t noPcre: " + arrRule[i].size() + "\n");
                for (int j = 0; j < arrRule[i].size(); j++) {
                    bw.write(arrRule[i].get(j).value + "\n");
                }
            }

            bw.write("\n\n#Result: \n number of pcre combinate with number of content\nnoContent\t noPcre\n");
            for (int i = 0; i < 20; i++) {
                if (arrContent[i] != 0) {
                    bw.write(i + "\t" + arrContent[i] + "\n");
                }
            }
            bw.flush();
            bw.close();
// it will extract all pcre to file, using writepcretofile from Refrence
            VRTSignature.References.WritePcreToFileRuleComponent(db.lstRuleAll, outputfolder + "all.pcre.ref");
            System.out.println("Write to all.pcre.ref OK");

        } catch (IOException ex) {
            Logger.getLogger(GetPcre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void pcreStatistic(String file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            OptionMask mask = new OptionMask();
            mask.SetPermit("pcre");
            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                //bw.write("\n#" + db.lstSnortRuleSet.get(i).name + "\n");
                RuleSet rs = db.lstSnortRuleSet.get(i);
                LinkedList<PCRE> lpe = new LinkedList<PCRE>();
                LinkedList<PCRE> lpd = new LinkedList<PCRE>();
                for (int j = 0; j < rs.lstRuleActive.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    if (rs.lstRuleActive.get(j).ApplyMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        lpe.add((PCRE) rs.lstRuleActive.get(j).getFirstOpPcre());
                    }
                }
                for (int j = 0; j < rs.lstRuleInactive.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    if (rs.lstRuleInactive.get(j).ApplyMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        lpd.add((PCRE) rs.lstRuleInactive.get(j).getFirstOpPcre());
                    }
                }
                bw.write(rs.name + "\t" + rs.lstRuleAll.size() + "\t" + lpe.size() + "\t" + lpd.size()+ "\t" + this.doReducePcre(lpe).size() + "\t" + this.doReducePcre(lpd).size()+ "\n");
            }

            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(GetPcre.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public LinkedList<PCRE> doReducePcre(LinkedList<PCRE> lpcre) {
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

    private void getSimplePCRE() {
        RuleDatabase db = new RuleDatabase();
        db.buildDatabase();
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> lpcre = new LinkedList<PCRE>();

        try {
            // Extract all pcre from rule set to simple.pcre

            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "simple.pcre"));
            bw.write("Malicious Rules: " + db.lstSnortRuleSet.size() + "\n");
            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                bw.write("\n#" + db.lstSnortRuleSet.get(i).name + "\n");
                RuleSet rs = db.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getFirstOpPcre();
                        if (References.isSimplyPcre(pcre)) {
                            bw.write(pcre.toString() + "\n");
                            lpcre.add(pcre);
                        }
                    }
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GetPcre.class.getName()).log(Level.SEVERE, null, ex);
        }

        References.WritePcreToFile(lpcre, outputfolder + "simple.pcre.ref");
        References.WritePcreToFile(References.doReducePcre(lpcre), outputfolder + "reduce.simple.pcre.ref");
    }

    private void getAllPCRE() {
    }

    private void getSupportedPCRE() {

        RuleDatabase db = new RuleDatabase();
        db.buildDatabase();
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> lpcre = new LinkedList<PCRE>();

        try {
            // Extract all pcre from rule set to simple.pcre

            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "supported.pcre"));
            bw.write("Malicious Rules: " + db.lstSnortRuleSet.size() + "\n");
            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                bw.write("\n#" + db.lstSnortRuleSet.get(i).name + "\n");
                RuleSet rs = db.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        PCRE pcre = (PCRE) rs.lstRuleAll.get(j).getFirstOpPcre();
                        if (References.isSupportablePCRE(pcre)) {
                            bw.write(pcre.toString() + "\n");
                            lpcre.add(pcre);
                        }
                    }
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GetPcre.class.getName()).log(Level.SEVERE, null, ex);
        }

        References.WritePcreToFile(lpcre, outputfolder + "supported.pcre.ref");
        References.WritePcreToFile(References.doReducePcre(lpcre), outputfolder + "reduce.suported.pcre.ref");
    }
}
