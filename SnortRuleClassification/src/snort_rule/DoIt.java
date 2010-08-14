/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package snort_rule;

/**
 *
 * @author heckarim
 */
public class DoIt {

    public static void main(String[] args){
        new DoIt().action();
    }


    private void action() {
        RuleDatabase rd = new RuleDatabase();
        rd.BuildDatabase();
        //rd.print4Test();
        Statistic0 st = new Statistic0(rd);
                st.DoScript();

    }
}
