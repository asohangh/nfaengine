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

    LinkedList<RuleOption> lstOpModifier;
    OpContent(String sop) {
        
        super(sop);
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
