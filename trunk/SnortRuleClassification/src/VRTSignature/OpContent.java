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
public class OpContent extends RuleOption {

    LinkedList<RuleOption> lstOpModifier;  // incase of existing modifier option for this OpConent, pack it here.

    OpContent(String sop, RuleComponent rule) {
        super(sop, rule);
        //System.out.println("Optioncontent: " +sop);
        this.optionValue = this.optionValue.substring(1, this.optionValue.length() - 1);
        this.lstOpModifier = new LinkedList<RuleOption>();
        this.isContent = true;
    }

    OpContent(String opname, String opval, RuleComponent rule) {
        super(opname,opval, rule);
        //System.out.println("Optioncontent: " +sop);
        this.optionValue = opval;
        this.lstOpModifier = new LinkedList<RuleOption>();
        this.isContent = true;
    }

    /**
     *
     * @param op
     * @return  
     *          true if equal
     *          care:
     *                  +, nocase
     *                  +, value of content
     *          don'care:
     *                  +, orther modifier option
     */
    public boolean compareTo(OpContent op) {
        //if nocase
        boolean nocase = false;
        for (int i = 0; i < this.lstOpModifier.size(); i++) {
            RuleOption ro = this.lstOpModifier.get(i);
            if (ro.optionName.compareToIgnoreCase("nocase") == 0) {
                nocase = true;
                break;
            }
        }
        if (nocase) {
            if (this.optionValue.compareToIgnoreCase(op.optionValue) != 0) {
                return false;
            }
        } else if (this.optionValue.compareTo(op.optionValue) != 0) {
            return false;
        }

        return true;
    }

    
    /**
     * 
     * @return
     * 
     * format:  content:"abc"
     */
    public String getOptionString(){
        String ret;
        ret = this.optionName + ":\"" + this.optionValue + "\"";
        return ret;
    }


    @Override
    /**
     * format  content:"abc"; nocase:; ....
     */
    public String toString() {
        String ret;
        ret = this.optionName + ":\"" + this.optionValue + "\"; ";
        for (int i = 0; i < this.lstOpModifier.size(); i++) {
            ret = ret + this.lstOpModifier.get(i).toString() + "; ";
        }
        //ret = "(" +ret + ")";
        return ret;
    }

    public String getStringModifier() {
        String s = "";
        for (int i = 0; i < this.lstOpModifier.size(); i++) {
            s += this.lstOpModifier.get(i).toString();
        }
        return s;
    }

    /**
     *
     * @return
     *
     *      number of character inside value field
     *  
     *      content:"|04|rx11|05|e6532|03|com";
     */
    public int countCharacter() {
        int ret = 0;

        for (int i = 0; i < this.optionValue.length();) {
            char ch = this.optionValue.charAt(i);
            if (ch == '|') {
                String s = "";
                for (i++; i < this.optionValue.length(); i++) {
                    if (this.optionValue.charAt(i) == '|') {
                        break;
                    } else {
                        s += this.optionValue.charAt(i);
                    }
                }
                ret += s.trim().split(" ").length;
                i++;
            } else {
                ret++;
                i++;
            }
        }
        return ret;
    }
}
