/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package VRTSignature;

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
        String outdir = System.getProperty("user.dir") + File.separator + "rule.test" + File.separator;
        

        RuleDatabase rd = new RuleDatabase(basefolder);
        rd.setOutputFolder(outdir);
        
         // Backup database
         //rd.buildDatabase();
        //rd.backupDatabase("allsig");
        
         // Restore database
        rd.restoreDatabase(outdir, "allsig.xls");
        rd.backupDatabase(outdir, "test2");       
        //rd.print4Test();
        //Statistic0 st = new Statistic0(rd);
        //st.DoScript();
    }
}
