/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

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
public class RuleDatabase {

    String currentDir = System.getProperty("user.dir");
    public String rulefolder = currentDir + File.separator + "rules.2.9" + File.separator;
    String outputfolder = currentDir + File.separator + "result.2.9" + File.separator;
    public LinkedList<RuleSet> lstSnortRuleSet; // with exception of Deleted.rules and Local.rules.
    RuleSet rsDeleted;
    RuleSet rsLocal;    //todo, current rslocal don't include in active or inactive rule.
    public LinkedList<RuleComponent> lstRuleAll;
    public LinkedList<RuleComponent> lstRuleInactive;
    public LinkedList<RuleComponent> lstRuleActive;
    public LinkedList<RuleComponent> lstRuleDeleted;
    
    public RuleDatabase() {
        lstSnortRuleSet = new LinkedList<RuleSet>();
        lstRuleActive = new LinkedList<RuleComponent>();
        lstRuleInactive = new LinkedList<RuleComponent>();
        lstRuleAll = new LinkedList<RuleComponent>();
        lstRuleDeleted = new LinkedList<RuleComponent>();

    }

    public RuleDatabase(String rulefolder) {
        this.rulefolder = rulefolder;

        lstSnortRuleSet = new LinkedList<RuleSet>();
        lstRuleActive = new LinkedList<RuleComponent>();
        lstRuleInactive = new LinkedList<RuleComponent>();
        lstRuleAll = new LinkedList<RuleComponent>();
        lstRuleDeleted = new LinkedList<RuleComponent>();
    }

    public void setRuleFolder(String rulefolder){
        this.rulefolder = rulefolder;
    }

    public void setOutputFolder(String outputfolder){
        this.outputfolder = outputfolder;
    }

    public void BuildDatabase() {
        File[] arrayFile = this.getRuleFiles(rulefolder);
        //Create Snort Rule Set
        for (int i = 0; i < arrayFile.length; i++) {
            System.out.println(arrayFile[i]);
            //parseRuleFile(arrayFile[i]);
            if(arrayFile[i].getName().compareToIgnoreCase("deleted.rules") == 0)
            {
                this.rsDeleted = new RuleSet(arrayFile[i]);
                continue;
            }
            if(arrayFile[i].getName().compareToIgnoreCase("local.rules") == 0){
                this.rsLocal = new RuleSet(arrayFile[i]);
                continue;
            }

            RuleSet rs = new RuleSet(arrayFile[i]);
            this.lstSnortRuleSet.add(rs);
        }
       //Include all rulecomponent to list
        //list of rulecomponent
        for(int i =0; i<this.lstSnortRuleSet.size(); i++){
            RuleSet rs = this.lstSnortRuleSet.get(i);
            this.lstRuleActive.addAll(rs.lstRuleActive);
            this.lstRuleInactive.addAll(rs.lstRuleInactive);
            this.lstRuleAll.addAll(rs.lstRuleAll);
        }
        //lisf of deleted rule;
        if(this.rsDeleted != null)
            this.lstRuleDeleted.addAll(this.rsDeleted.lstRuleInactive);
        
    }

    public File[] getRuleFiles(String rulefolder) {
        File[] ret = new File(rulefolder).listFiles(new RulesFilter(".rules"));
        System.out.println("number of file in " + rulefolder + " is: " + ret.length);

        for (int i = 0; i < ret.length; i++) {
            //System.out.println(ret[i].getName());
        }
        return ret;
    }


    public LinkedList<OpContent> GetListContent(LinkedList<RuleComponent> lstRc){
        LinkedList<OpContent> lstRet = new LinkedList<OpContent>();
        for(int i =0; i<lstRc.size(); i++){
            if(!lstRc.get(i).isHaveContent)
                continue;
            lstRet.addAll(lstRc.get(i).GetOpContent());
        }
        return lstRet;
    }

    public void print4Test(){
        System.out.println("active " + this.lstRuleActive.size());
        for(int i =0; i<this.lstRuleActive.size(); i++){
            System.out.println("\t" + this.lstRuleActive.get(i).toString());
        }
        System.out.println("Inactive " + this.lstRuleInactive.size());
        for(int i =0; i<this.lstRuleInactive.size(); i++){
            System.out.println("\t" + this.lstRuleInactive.get(i).toString());
        }
        //check modifier option
    }

}
