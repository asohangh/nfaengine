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

    String option;
    String value;
    boolean isContent = false;
    boolean isPCRE = false;
    boolean isContentModifier = false;

    RuleOption() {
        option = "";
        value = "";
    }

    RuleOption(String sop) {
        //System.out.println("parse Option: " + sop);
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