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
import snort_rule.*;

/**
 *
 * @author heckarim
 */
/**
 *
 * Purpose of this Class:
 *      1. Parse output of Extractor 16 05 2011.
 *      2. Give function for other class.
 * 
 */
public class Parser_16_05_2011 {

    private String inputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private String outputfolder = inputfolder;
    private String excelfile;
    private RuleDatabase db;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;
    Workbook workbook = null;

    public static void main(String[] args) throws IOException, WriteException, BiffException {
        Parser_16_05_2011 ex = new Parser_16_05_2011();
        ex.Action();
    }

    
    Parser_16_05_2011(String file) {
        try {
            this.excelfile = file;
            this.workbook = Workbook.getWorkbook(new File(excelfile));

        } catch (IOException ex) {
            Logger.getLogger(Parser_16_05_2011.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(Parser_16_05_2011.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Parser_16_05_2011() {
        
    }

    public LinkedList<String> getPCREbyRuleset(String rulename, int support) {
        if (workbook == null) {
            return null;
        }
        else
            return this.getPCREbyRuleset(this.workbook, rulename, support);
    }

    private void Action() throws IOException, WriteException, BiffException {
        excelfile = this.inputfolder + "snort2.9.extraction.xls";
        Workbook workbook = Workbook.getWorkbook(new File(excelfile));

        LinkedList<String> lpcre = this.getPCREbyRuleset(workbook, "voip", 1);
        if (lpcre == null) {
            System.out.println("null");
        } else {
            for (int i = 0; i < lpcre.size(); i++) {
                System.out.println(lpcre.get(i));
            }
        }
    }

    /**
     *
     * @param rulename
     * @param support
     *      0: no
     *      1: yes
     *      2: all
     * @return
     */
    public LinkedList<String> getPCREbyRuleset(Workbook book, String rulename, int support) {
        LinkedList<String> lpcre = new LinkedList<String>();
        Sheet sheet = book.getSheet(rulename);
        if (sheet == null) {
            return null;
        }
        //begin to read
        int index = 1;
        while (index < sheet.getRows()) {
            String pStatus = sheet.getCell(3, index).getContents();
            String pcre = sheet.getCell(4, index).getContents();
            if (support == 1) {
                if (pStatus.compareToIgnoreCase("1") == 0) {
                    lpcre.add(pcre);
                }
            } else if (support == 0) {
                if (pStatus.compareToIgnoreCase("0") == 0) {
                    lpcre.add(pcre);
                }
            } else {
                lpcre.add(pcre);
            }
            index++;
        }
        return lpcre;
    }
}
