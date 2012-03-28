/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Statistic;

import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import VRTSignature.RuleDatabase;

/**
 *
 * @author heckarim
 */
public class v1_Payload {
    private final RuleDatabase db;

    v1_Payload(RuleDatabase db) {
        this.db = db;
    }

    public void action(){
        //PCRE statistic
        String file = "pcre.xls";
        v1_pcre pcre = new v1_pcre(db,file);
        pcre.action();
    }

    void outputStats(String file) throws IOException, WriteException {
        // the first step is to create a writable workbook using the factory method on the Workbook class.
        WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
        //1. Rule id references.
       // this.outExcelReferences(workbook, "references");


        //close the all opened connections
        workbook.write();
        workbook.close();
    }
}
