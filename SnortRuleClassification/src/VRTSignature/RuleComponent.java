/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VRTSignature;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RuleComponent {

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
    private boolean isActive = false;
    private boolean isDeleted = false;

    RuleComponent(String s, RuleSet ruleset) {
        this.lstOption = new LinkedList<RuleOption>();
        this.value = s;
        this.ruleSet = ruleset;
        this.ruleStatus = new RuleStatus();
        ParseRuleComponent(this.value);
        this.isValid = true;
    }

    RuleComponent(LinkedList<String> ls, RuleSet ruleset) {

        this.lstOption = new LinkedList<RuleOption>();
        this.ruleSet = ruleset;
        this.ruleStatus = new RuleStatus();
        parseBackupData(ls);
        //todo this.setVaule(ls);
        this.isValid = true;

        String status = ls.get(0);
        if (status.compareToIgnoreCase("1") == 0) {
            this.isActive = true;
        } else if (status.compareToIgnoreCase("-1") == 0) {
            this.isDeleted = true;
        }

    }

    private void ParseRuleComponent(String rule) {
        String sheader;
        int index;

        //parse Header of rule;
        //System.out.println(rule);
        index = rule.indexOf("(");
        sheader = rule.substring(0, index);
        this.header = new RuleHeader(sheader.trim(), this);

        //parse rule content
        // bo phan header cua rule va 2 dau ()
        rule = rule.substring(index + 1, rule.length() - 1);
        String[] arrayOption = References.splitByChar(rule, ';');

        for (int i = 0; i < arrayOption.length; i++) {
            String sop = arrayOption[i];
            if (sop.startsWith("uricontent")) {
                this.isHaveUriContent = true;
            }
            if (sop.startsWith("content")) {
                OpContent op = new OpContent(sop, this);
                this.lstOption.add(op);
                this.isHaveContent = true;
                //increase counter for this option.
                this.ruleStatus.SetOption(op.optionName);
            } else if (sop.startsWith("pcre")) {
                PCRE op = new PCRE(sop, this);
                this.lstOption.add(op);
                this.isHavePCRE = true;
                this.ruleStatus.SetOption(op.optionName);
            } else {
                RuleOption op = new RuleOption(sop, this);
                this.lstOption.add(op);
                this.ruleStatus.SetOption(op.optionName);
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
        this.sid = this.getOpSID().optionValue;
    }

    public LinkedList<OpContent> GetOpContent() {
        LinkedList<OpContent> lstRet = new LinkedList<OpContent>();
        for (int i = 0; i < this.lstOption.size(); i++) {
            if (lstOption.get(i).isContent) {
                lstRet.add((OpContent) lstOption.get(i));
            }
        }
        if (lstRet.isEmpty()) {
            return null;
        } else {
            return lstRet;
        }
    }

    private RuleOption getOpSID() {
        RuleOption op = null;
        for (int i = 0; i < this.lstOption.size(); i++) {
            if (lstOption.get(i).optionName.compareToIgnoreCase("sid") == 0) {
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
    public RuleOption getRuleOption(String option) {
        RuleOption ret = null;
        for (int i = 0; i < this.lstOption.size(); i++) {
            if (lstOption.get(i).optionName.compareToIgnoreCase(option) == 0) {
                ret = lstOption.get(i);
                break;
            }
        }
        return ret;
    }

    public RuleOption getFirstOpPcre() {
        RuleOption op = null;
        for (int i = 0; i < this.lstOption.size(); i++) {
            if (lstOption.get(i).isPCRE) {
                op = (PCRE) lstOption.get(i);
                break;
            }
        }
        return op;
    }

    public LinkedList<PCRE> getOpPcre() {
        LinkedList<PCRE> rPcre = new LinkedList<PCRE>();
        for (int i = 0; i < this.lstOption.size(); i++) {
            if (lstOption.get(i).isPCRE) {
                rPcre.add((PCRE) lstOption.get(i));
            }
        }
        return rPcre;
    }

    public boolean ApplyMask(OptionMask mask) {
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

    public String getDPIRules() {
        String ret;
        ret = this.header.toString() + " (";
        for (int i = 0; i < this.lstOption.size(); i++) {
            RuleOption ro = this.lstOption.get(i);
            //System.out.println("Op: " + ro.optionName + " ---- " + ro.optionValue);

            if (References.isInOptionGroup(lstOption.get(i).optionName, References._opDPIModifier)) {
                ret += lstOption.get(i).getOptionString() + "; ";
            }
        }
        ret = ret + ")";
        //System.out.println(ret);
        return ret;
    }

    void setInactive() {
        this.isActive = false;
    }

    void setActive() {
        this.isActive = true;
    }

    boolean isActive() {
        return this.isActive;
    }

    private void parseBackupData(LinkedList<String> ls) {
        String sheader;
        int index;
        //parse Header of rule;
        LinkedList<String> lHeader = new LinkedList<String>();
        lHeader.addAll(ls.subList(1, 8));
        this.header = new RuleHeader(lHeader, this);
        //parse rule content
        LinkedList<String> lOption = new LinkedList<String>();
        lOption.addAll(ls.subList(8, ls.size()));
        for (int i = 0; i < lOption.size(); i = i + 2) {
            String opname = lOption.get(i);
            String opval = lOption.get(i + 1);
            //String
            if (opname.compareToIgnoreCase("uricontent") == 0) {
                this.isHaveUriContent = true;
            }
            if (opname.compareToIgnoreCase("content") == 0) {
                OpContent op = new OpContent(opname, opval, this);
                this.lstOption.add(op);
                this.isHaveContent = true;
                //increase counter for this option.
                this.ruleStatus.SetOption(op.optionName);
            } else if (opname.compareToIgnoreCase("pcre") == 0) {
                PCRE op = new PCRE(opname, opval, this);
                this.lstOption.add(op);
                this.isHavePCRE = true;
                this.ruleStatus.SetOption(op.optionName);
            } else {
                RuleOption op = new RuleOption(opname, opval, this);
                this.lstOption.add(op);
                this.ruleStatus.SetOption(op.optionName);
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
        this.sid = this.getOpSID().optionValue;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    void setDeleted() {
        this.isDeleted = true;
    }

    public String getDPIRulesAny() {
        String ret;
        ret = "alert udp any any -> any any" + " (";
        for (int i = 0; i < this.lstOption.size(); i++) {
            RuleOption ro = this.lstOption.get(i);
            //System.out.println("Op: " + ro.optionName + " ---- " + ro.optionValue);

            if (References.isInOptionGroup(lstOption.get(i).optionName, References._opDPIModifier)) {
                ret += lstOption.get(i).getOptionString() + "; ";
            }
        }
        ret = ret + ")";
        //System.out.println(ret);
        return ret;
    }
}
