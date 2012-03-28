/*
 * hehehe To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RuleDatabase;

import java.io.File;

/**
 *
 * @author heckarim
 */
public class DoIt {

    public static void main(String[] args){
        new DoIt().action();
    }

    private void action() {
        String basefolder = System.getProperty("user.dir") + File.separator + "rules.2.9" + File.separator;
        //RuleDatabase rd = new RuleDatabase();
        RuleDatabase rd = new RuleDatabase(basefolder);
        
        rd.BuildDatabase();
        //rd.print4Test();
        Statistic0 st = new Statistic0(rd);
                st.DoScript();
    }
}
