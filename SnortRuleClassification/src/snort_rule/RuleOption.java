/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snort_rule;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RuleOption {

    String optionName;          //option name
    String value;           // value of option.

    RuleComponent rule;     // rule which it belong to.

    boolean isContent = false; // if option is OpContent
    boolean isPCRE = false;    // if option is PCRE
    boolean isContentModifier = false;  // if it is modifier for OpConent.

    public RuleOption() {
        optionName = "";
        value = "";
        this.rule = null;
    }

    /**
     *
     * @return
     *  0: if no rule
     *  other: have rule
     */
    public String getSID(){
        if(rule!= null)
            return this.rule.sid;
        else
            return "0";
    }

    /**
     *
     * @return
     * return rule which it belong to.
     * 
     */
    public RuleComponent getRule(){
        return this.rule;
    }


    /**
     *
     * @return
     */
    public String getvalue(){
        return this.value;
    }

    RuleOption(String sop, RuleComponent rule) {
        //System.out.println("parse Option: " + sop);
        this.rule = rule;
        if (sop.indexOf(":") != -1) {
            String[] split = sop.split(":");
            this.optionName = split[0].trim();
            this.value = split[1].trim();
        } else {
            this.optionName = sop;
            this.value = "";
        }
        //check if it is content modifier option
        if (References.CheckContentModifier(optionName)) {
            this.isContentModifier = true;
        }

    /*if(option.compareToIgnoreCase("content") == 0)
    this.isContent = true;
    if(option.compareToIgnoreCase("pcre") == 0)
    this.isPCRE = true;
     */
    }

    public String toString() {
        String ret;
        ret = this.optionName + ":" + this.value;
        return ret;
    }
}