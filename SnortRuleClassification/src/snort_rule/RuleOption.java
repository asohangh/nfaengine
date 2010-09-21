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

    String option;          //option name
    String value;           // value of option.

    RuleComponent rule;     // rule which it belong to.

    boolean isContent = false; // if option is OpContent
    boolean isPCRE = false;    // if option is PCRE
    boolean isContentModifier = false;  // if it is modifier for OpConent.

    public RuleOption() {
        option = "";
        value = "";
        this.rule = null;
    }

    RuleOption(String sop, RuleComponent rule) {
        //System.out.println("parse Option: " + sop);
        this.rule = rule;
        if (sop.indexOf(":") != -1) {
            String[] split = sop.split(":");
            this.option = split[0].trim();
            this.value = split[1].trim();
        } else {
            this.option = sop;
            this.value = "";
        }
        //check if it is content modifier option
        if (References.CheckContentModifier(option)) {
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
        ret = this.option + ":" + this.value;
        return ret;
    }
}