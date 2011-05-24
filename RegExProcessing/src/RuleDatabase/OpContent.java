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
public class OpContent extends RuleOption {

    LinkedList<RuleOption> lstOpModifier;  // incase of existing modifier option for this OpConent, pack it here.

    OpContent(String sop, RuleComponent rule) {
        super(sop, rule);
        //System.out.println("Optioncontent: " +sop);
        this.value = this.value.substring(1, this.value.length() - 1);
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
            if (ro.option.compareToIgnoreCase("nocase") == 0) {
                nocase = true;
                break;
            }
        }
        if (nocase) {
            if (this.value.compareToIgnoreCase(op.value) != 0) {
                return false;
            }
        } else if (this.value.compareTo(op.value) != 0) {
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
        ret = this.option + ":\"" + this.value + "\"";
        return ret;
    }


    @Override
    /**
     * format  content:"abc"; nocase:; ....
     */
    public String toString() {
        String ret;
        ret = this.option + ":\"" + this.value + "\"; ";
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

        for (int i = 0; i < this.value.length();) {
            char ch = this.value.charAt(i);
            if (ch == '|') {
                String s = "";
                for (i++; i < this.value.length(); i++) {
                    if (this.value.charAt(i) == '|') {
                        break;
                    } else {
                        s += this.value.charAt(i);
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
