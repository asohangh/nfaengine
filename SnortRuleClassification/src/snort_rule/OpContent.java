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
public class OpContent extends RuleOption {

    LinkedList<RuleOption> lstOpModifier;  // incase of existing modifier option for this OpConent, pack it here.


    OpContent(String sop,RuleComponent rule) {
        super(sop,rule);
        //System.out.println("Optioncontent: " +sop);
        this.lstOpModifier = new LinkedList<RuleOption>();
        this.isContent = true;
    }

    @Override
    public String toString(){
        String ret;

        ret = this.option + ":" + this.value + "; ";
        for(int i =0; i<this.lstOpModifier.size(); i++){
            ret = ret + this.lstOpModifier.get(i).toString() + "; ";
        }
        ret = "(" + ret + ")";
        return ret;
    }
}
