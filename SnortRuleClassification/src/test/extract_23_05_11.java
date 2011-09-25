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
public class extract_23_05_11 {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        extract_23_05_11 ex = new extract_23_05_11();
       // ex.temp();
        ex.Action();

    }

    /**
     *
     *
     *
     *
     */
    private void Action() throws IOException, WriteException {
        String rulefolder = System.getProperty("user.dir") + File.separator + "rules.2.9.test" + File.separator;
        String outfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
        db = new RuleDatabase(rulefolder);
        db.BuildDatabase();
        //this.outputSupportedRules();
        this.outputHeader(outfolder + "all.header");
        
        //this.outputTestingRulesFile("simple.nids.1.rules");
        //this.outputRules(this.outputfolder + "nids.rules");
    }

    private void outputSupportedRules() throws IOException {
        //rule actives
        //LinkedList<RuleComponent> lscomp = this.getOnlyRuleWithPcre(db.lstRuleActive);
        LinkedList<RuleComponent> lscomp = this.getAllRuleWithPcre(db.lstRuleActive);
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.outputfolder + "rule.active.rules")));

        for (int i = 0; i < lscomp.size(); i++) {
            RuleComponent rcomp = lscomp.get(i);
            bw.write(rcomp.value+"\n");
        }

        bw.flush();
        bw.close();

        //lscomp = this.getOnlyRuleWithPcre(db.lstRuleInactive);
        lscomp = this.getAllRuleWithPcre(db.lstRuleInactive);
        bw = new BufferedWriter(new FileWriter(new File(this.outputfolder + "rule.inactive.rules")));

        for (int i = 0; i < lscomp.size(); i++) {
            RuleComponent rcomp = lscomp.get(i);
            bw.write("#"+rcomp.value+"\n");

        }

        bw.flush();
        bw.close();
    }

    /**
     *
     * @param lstrule
     * @return  all rule include non pcre and having supported pcre
     */
    private LinkedList<RuleComponent> getAllRuleWithPcre(LinkedList<RuleComponent> lstrule) {
        LinkedList<RuleComponent> rlst = new LinkedList<RuleComponent>();
        //add all
        for (int i = 0; i < lstrule.size(); i++) {
            rlst.add(lstrule.get(i));
        }
        //fillter
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");

        // Extract all pcre from rule set to simple.pcre
        for (int i = 0; i < rlst.size();) {
            boolean sup = true;
            if (rlst.get(i).ApplyMask(mask)) {
                //bw.write(rs.lstRuleAll.get(j).value + "\n");
                LinkedList<PCRE> tPcre = rlst.get(i).getOpPcre();
                for (int j = 0; j < tPcre.size(); j++) {
                    if (!References.isSupportableAndSimplePCRE(tPcre.get(j))) {
                        sup = false;
                        break;
                    }
                }
            }
            if (!sup) {
                rlst.remove(i);
            } else {
                i++;
            }
        }
        return rlst;
    }

      /**
     *
     * @param lstrule
     * @return  all rules which have supported pcre
       *  These pcre must be extreme simple
     */
    private LinkedList<RuleComponent> getOnlyRuleWithPcre(LinkedList<RuleComponent> lstrule) {
        LinkedList<RuleComponent> rlst = new LinkedList<RuleComponent>();
        //add all
        for (int i = 0; i < lstrule.size(); i++) {
            rlst.add(lstrule.get(i));
        }
        //fillter
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");

        // Extract all pcre from rule set to simple.pcre
        for (int i = 0; i < rlst.size();) {
            boolean sup = true;
            if (rlst.get(i).ApplyMask(mask)) {
                //bw.write(rs.lstRuleAll.get(j).value + "\n");
                LinkedList<PCRE> tPcre = rlst.get(i).getOpPcre();
                for (int j = 0; j < tPcre.size(); j++) {
                    if (!References.isSupportableAndSimplePCRE(tPcre.get(j))) {
                        sup = false;
                        break;
                    }
                }
            }else
                sup = false;
            if (!sup) {
                rlst.remove(i);
            } else {
                i++;
            }
        }
        return rlst;
    }

    private void outputHeader(String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        LinkedList<String> ruleheader = this.reduceHeader(db.lstRuleAll);
        for (int i=0 ;i<ruleheader.size(); i++){
            String hd = ruleheader.get(i);
            bw.write(hd+"\n");
        }
        bw.flush();
        bw.close();
    }

    private void temp() {
        System.out.println(this.outputfolder);
    }

    private LinkedList<String> reduceHeader(LinkedList<RuleComponent> lstRuleAll) {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add(lstRuleAll.getFirst().header.value);
        for(int i =1; i<lstRuleAll.size(); i++){
            String t= lstRuleAll.get(i).header.value;
            boolean same = false;
            for(int j=0; j<ret.size(); j++){
                if(ret.get(j).compareToIgnoreCase(t) == 0){
                    same = true;
                    break;
                }

            }
            if(!same)
                ret.add(t);
        }
        return ret;
    }
}
