/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RuleComponent extends BaseClass{

    public RuleSet ruleSet;
    public RuleHeader header;
    public LinkedList<RuleOption> lstOption;
    public String value; //Content of Rule
    public RuleStatus ruleStatus;
    public String sid; //indentifier of rule

    boolean isValid = false;
    boolean isHaveContent = false;
    boolean isHaveUriContent = false;
    boolean isHavePCRE = false;

    RuleComponent(String s, RuleSet ruleset) {
        this.lstOption = new LinkedList<RuleOption>();
        this.value = s;
        this.ruleSet = ruleset;
        this.ruleStatus = new RuleStatus();
        ParseRuleComponent(this.value);
        this.isValid = true;
    }

    private void ParseRuleComponent(String rule) {
        String sheader;
        int index;

        //parse Header of rule;
        //System.out.println(rule);
        index = rule.indexOf("(");
        sheader = rule.substring(0, index);
        this.header = new RuleHeader(sheader.trim(),this);

        //parse rule content
        // bo phan header cua rule va 2 dau ()
        rule = rule.substring(index + 1, rule.length() - 1);
        String[] arrayOption = References.splitByChar(rule, ';');
        
        for (int i = 0; i < arrayOption.length; i++) {
            String sop = arrayOption[i];
            if(sop.startsWith("uricontent"))
                this.isHaveUriContent = true;
            if (sop.startsWith("content")) {
                OpContent op = new OpContent(sop,this);
                this.lstOption.add(op);
                this.isHaveContent = true;
                this.ruleStatus.SetOption(op.option);
            } else if (sop.startsWith("pcre")) {
                PCRE op = new PCRE(sop,this);
                this.lstOption.add(op);
                this.isHavePCRE = true;
                this.ruleStatus.SetOption(op.option);
            } else {
                RuleOption op = new RuleOption(sop,this);
                this.lstOption.add(op);
                this.ruleStatus.SetOption(op.option);
            }  
        }
        //need to cheeck and add modifier option in to OpContnet
        for (int i = 0; i < lstOption.size();) {
            if (lstOption.get(i).isContent) {
                OpContent op = (OpContent) lstOption.get(i);
                i++;
                for (; i < lstOption.size(); i++) {
                    if (lstOption.get(i).isContentModifier) {
                        op.lstOpModifier.add(lstOption.get(i));
                    } else {
                        break;
                    }
                }
            } else {
                i++;
            }
        }
        //get SID
        this.sid = this.getOpSID().value;
    }

    public LinkedList<OpContent> GetOpContent(){
        LinkedList<OpContent> lstRet = new LinkedList<OpContent>();
        for(int i =0; i < this.lstOption.size(); i++){
            if(lstOption.get(i).isContent)
                lstRet.add((OpContent)lstOption.get(i));
        }
        if(lstRet.isEmpty())
            return null;
        else
            return lstRet;
    }


    private RuleOption getOpSID(){
        RuleOption op = null;
        for(int i =0; i < this.lstOption.size(); i++){
            if(lstOption.get(i).option.compareToIgnoreCase("sid")==0){
                op = lstOption.get(i);
                break;
            }
        }
        return op;
    }

    /**
     *  this function just use for option which appear one time.
     * @param option
     * @return
     */
    public RuleOption getRuleOption(String option){
        RuleOption ret = null;
        for(int i =0; i < this.lstOption.size(); i++){
            if(lstOption.get(i).option.compareToIgnoreCase(option) == 0){
                ret = lstOption.get(i);
                break;
            }
        }
        return ret;
    }


    public RuleOption getOpPcre(){
        RuleOption op = null;
        for(int i =0; i < this.lstOption.size(); i++){
            if(lstOption.get(i).isPCRE){
                op = (PCRE) lstOption.get(i);
                break;
            }
        }
        return op;
    }
    public boolean ApplyMask(OptionMask mask){
        return this.ruleStatus.CompareMask(mask);
    }

    @Override
    public String toString() {
        String ret;
        ret = this.header.toString() + " (";
        for (int i = 0; i < this.lstOption.size(); i++) {
            ret += lstOption.get(i).toString() + "; ";
        }
        ret = ret + ")";
        return ret;
    }

}
