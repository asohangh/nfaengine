/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import VRTSignature.*;

/**
 *
 * @author heckarim
 */
public class GetRuleForTesting {
    private String inputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private String outputfolder = this.inputfolder;

    public static void main(String[] args) throws IOException, WriteException, BiffException {
        GetRuleForTesting ex = new GetRuleForTesting();
        ex.Action();
    }

    private void Action() {
        BufferedWriter bw = null;
        try {
            String excelfile = this.inputfolder + "snort2.9.extraction.xls";
            String ouptutfile = this.outputfolder + "voip.pcre";
            Parser_16_05_2011 extract = new Parser_16_05_2011(excelfile);
            LinkedList<String> lstring = extract.getPCREbyRuleset("voip", 1);
            bw = new BufferedWriter(new FileWriter(new File(ouptutfile)));

            for (int i =0; i<lstring.size(); i++){
                bw.write(lstring.get(i) + "\n");
                System.out.println(lstring.get(i));
            }
            
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GetRuleForTesting.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(GetRuleForTesting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

  
}
