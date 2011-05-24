/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RuleSet {

    public String name;
    public LinkedList<RuleComponent> lstRuleAll;
    public LinkedList<RuleComponent> lstRuleInactive;
    public LinkedList<RuleComponent> lstRuleActive;

    public RuleSet(File file) {
        lstRuleActive = new LinkedList<RuleComponent>();
        lstRuleInactive = new LinkedList<RuleComponent>();
        lstRuleAll = new LinkedList<RuleComponent>();
        name = file.getName();
        ParseRuleFile(file);
    }

    /**
     *
     * @param rfile
     *
     * Nhan tham so la mot rule file, doc tung dong lay ra rule Component roi add vao listRuleComponent
     */
    public void ParseRuleFile(File rfile) {
        String s = "";
        System.out.println("ParseFile: " + rfile.getName());
        try {
            BufferedReader br = new BufferedReader(new FileReader(rfile));
            //System.out.println("parse rules file: " + rfile.getName());
            while ((s = br.readLine()) != null) {
                //System.out.println(s);
                RuleComponent ruleComponent = null;
                if (CheckRule(s) == -1) {
                    //inactive rule
                    s = s.substring(1).trim();
                    ruleComponent = new RuleComponent(s, this);
                    //listRuleComponent.size();
                    if (ruleComponent.isValid) {
                        lstRuleInactive.add(ruleComponent);
                    }
                } else if (CheckRule(s) == 1) {
                    //active rule;
                    ruleComponent = new RuleComponent(s, this);
                    if (ruleComponent.isValid) {
                        lstRuleActive.add(ruleComponent);
                    //listRuleComponent.size();
                    }
                }
                if (ruleComponent != null) {
                    this.lstRuleAll.add(ruleComponent);
                }
            }
            br.close();
        } catch (FileNotFoundException t1) {
            System.err.println(t1);
        } catch (IOException t2) {
            System.err.println(t2);
        }
    }

    /**
     *
     *  Return:
     *      0: nonRule
     *      1: activerule
     *      -1: nonactive rule
     */
    public int CheckRule(String tar) {
        String action = "alert; log; pass; activate; dynamic; drop; reject; sdrop ";
        String[] split = action.split("; ");
        for (int i = 0; i < split.length; i++) {
            if (tar.startsWith(split[i])) {
                return 1;
            } else if (tar.startsWith("# " + split[i]) || tar.startsWith("#" + split[i])) {
                if (tar.indexOf("(") == -1) {
                    return 0;
                }
                return -1;
            }
        }
        return 0;
    }

    public String toString() {
        return this.name;
    }
}
