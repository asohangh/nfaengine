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
import snort_rule.*;
import snort_rule.RuleDatabase;

/**
 *
 * @author heckarim
 */

public class GetPcre {

    public static void main(String[] args) {
        new GetPcre().action();
    }

    private void action() {
        RuleDatabase db = new RuleDatabase();
        db.BuildDatabase();
        String outputfolder = System.getProperty("user.dir") + File.separator + "output" + File.separator;

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
                    if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                        //bw.write(rs.lstRuleAll.get(j).value + "\n");
                        bw.write(rs.lstRuleAll.get(j).getOpPcre().toString() + "\n");
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
            LinkedList<RuleComponent> [] arrRule = new LinkedList[20];
            for(int i =0; i<  arrRule.length; i++){
                arrRule[i] = new LinkedList<RuleComponent>();
            }

            for (int i = 0; i < db.lstSnortRuleSet.size(); i++) {
                RuleSet rs = db.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    //if (!rs.lstRuleAll.get(j).ruleStatus.isHaveOption("content") && !rs.lstRuleAll.get(j).ruleStatus.isHaveOption("uricontent")) {
                    int noContent = 0;
                    if (rs.lstRuleAll.get(j).ruleStatus.CompareMask(mask)) {
                        
                        if(rs.lstRuleAll.get(j).GetOpContent() != null)
                            noContent = rs.lstRuleAll.get(j).GetOpContent().size();
                        
                        arrContent[noContent]++;
                        //add it to linkedlist rule
                        arrRule[noContent].add(rs.lstRuleAll.get(j));
                        
                        //bw.write(rs.lstRuleAll.get(j).getOpPcre().toString() + "\n");
                    }
                }
            }
            for(int i =0; i< arrRule.length; i++){
                bw.write("# noContent: " +i + "\t noPcre: "+ arrRule[i].size() + "\n");
                for(int j =0; j<arrRule[i].size(); j++){
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
            snort_rule.References.WritePcreToFile(db.lstRuleAll, outputfolder + "all.pcre.ref");
            System.out.println("Write to all.pcre.ref OK");

        } catch (IOException ex) {
            Logger.getLogger(GetPcre.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
