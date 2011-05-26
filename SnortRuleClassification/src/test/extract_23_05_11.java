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
        this.outputSupportedRules();
        //this.outputTestingRulesFile("simple.nids.1.rules");
        //this.outputRules(this.outputfolder + "nids.rules");


    }

    private void outputSupportedRules() throws IOException {
        //rule actives
        LinkedList<RuleComponent> lscomp = this.getAllRuleWithPcre(db.lstRuleActive);
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.outputfolder + "Rules.active.rules")));

        for (int i = 0; i < lscomp.size(); i++) {
            RuleComponent rcomp = lscomp.get(i);
            bw.write(rcomp.toString()+"\n");
        }

        bw.flush();
        bw.close();

        lscomp = this.getAllRuleWithPcre(db.lstRuleInactive);
        bw = new BufferedWriter(new FileWriter(new File(this.outputfolder + "Rules.inactive.rules")));

        for (int i = 0; i < lscomp.size(); i++) {
            RuleComponent rcomp = lscomp.get(i);
            bw.write("#"+rcomp.toString()+"\n");
        }

        bw.flush();
        bw.close();
    }

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
                    if (!References.isSupportablePCRE(tPcre.get(j))) {
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
}
