/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Statistic;

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
import jxl.write.*;
import jxl.write.Number;
import snort_rule.*;

/**
 *
 * @author heckarim
 */
public class v1_statistic {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        v1_statistic ex = new v1_statistic();
        ex.Action();
    }

    private void Action() throws IOException, WriteException {
        String rulefolder = System.getProperty("user.dir") + File.separator + "rules.2.9" + File.separator;
        db = new RuleDatabase(rulefolder);
        db.BuildDatabase();
        //General statistic.
        String statsGen = outputfolder + "General.v1.stats.xls";
        v1_General gen =new v1_General(db);
        gen.outputStats(statsGen);
        //Header statistic.
        String statsHeader = outputfolder + "Header.v1.stats.xls";
        v1_Header head =new v1_Header(db);
        head.outputStats(statsHeader);
        
        //Payload statistic.
        String statsPayload = outputfolder + "Header.v1.stats.xls";
        v1_Payload pay = new v1_Payload(db);
        //pay.outputStats(statsPayload);
        pay.action();
    }

}
