/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VRTSignature;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author heckarim
 */
public class RuleDatabase {
    // Configuration

    int checkDeletedRule = 0;
    //
    String currentDir = System.getProperty("user.dir");
    String ruleDir = currentDir + File.separator + "rules.2.9" + File.separator;
    String outputDir = currentDir + File.separator + "output.2.9" + File.separator;
    public LinkedList<RuleSet> lstSnortRuleSet; // with exception of Deleted.rules and Local.rules.
    RuleSet rsDeleted;
    RuleSet rsLocal;    //todo, current rslocal don't include in active or inactive rule.
    public LinkedList<RuleComponent> lstRuleAll;
    public LinkedList<RuleComponent> lstRuleInactive;
    public LinkedList<RuleComponent> lstRuleActive;
    public LinkedList<RuleComponent> lstRuleDeleted;

    public RuleDatabase() {
        lstSnortRuleSet = new LinkedList<RuleSet>();
        lstRuleActive = new LinkedList<RuleComponent>();
        lstRuleInactive = new LinkedList<RuleComponent>();
        lstRuleAll = new LinkedList<RuleComponent>();
        lstRuleDeleted = new LinkedList<RuleComponent>();
    }

    public RuleDatabase(String rulefolder) {
        this.ruleDir = rulefolder;

        lstSnortRuleSet = new LinkedList<RuleSet>();
        lstRuleActive = new LinkedList<RuleComponent>();
        lstRuleInactive = new LinkedList<RuleComponent>();
        lstRuleAll = new LinkedList<RuleComponent>();
        lstRuleDeleted = new LinkedList<RuleComponent>();
    }

    public void setRuleDir(String rulefolder) {
        this.ruleDir = rulefolder;
    }

    public void setOutputFolder(String outputfolder) {
        this.outputDir = outputfolder;
    }

    public void buildDatabase() {
        File[] arrayFile = this.getRuleFiles(ruleDir);
        //Create Snort Rule Set
        for (int i = 0; i < arrayFile.length; i++) {
            System.out.println(arrayFile[i]);
            int status = 0; //status of rule, 1: normal, -1 delte
            //parseRuleFile(arrayFile[i]);
            if (!(this.checkDeletedRule == 0) && arrayFile[i].getName().compareToIgnoreCase("deleted.rules") == 0) {
                status = -1;
                this.rsDeleted = new RuleSet(status, arrayFile[i]);

                continue;
            }
            if (arrayFile[i].getName().compareToIgnoreCase("local.rules") == 0) {
                this.rsLocal = new RuleSet(arrayFile[i]);
                continue;
            }

            RuleSet rs = new RuleSet(arrayFile[i]);
            this.lstSnortRuleSet.add(rs);
        }
        //Include all rulecomponent to list
        //list of rulecomponent
        for (int i = 0; i < this.lstSnortRuleSet.size(); i++) {
            RuleSet rs = this.lstSnortRuleSet.get(i);
            this.lstRuleActive.addAll(rs.lstRuleActive);
            this.lstRuleInactive.addAll(rs.lstRuleInactive);
            this.lstRuleAll.addAll(rs.lstRuleAll);
        }
        //lisf of deleted rule;
        if (this.rsDeleted != null) {
            this.lstRuleDeleted.addAll(this.rsDeleted.lstRuleInactive);
        }

    }

    public File[] getRuleFiles(String rulefolder) {
        File[] ret = new File(rulefolder).listFiles(new RulesFilter(".rules"));
        System.out.println("number of file in " + rulefolder + " is: " + ret.length);

        /*for (int i = 0; i < ret.length; i++) {
        //System.out.println(ret[i].getName());
        }*/
        return ret;
    }
    /*
    private LinkedList<OpContent> GetListContent(LinkedList<RuleComponent> lstRc) {
    LinkedList<OpContent> lstRet = new LinkedList<OpContent>();
    for (int i = 0; i < lstRc.size(); i++) {
    if (!lstRc.get(i).isHaveContent) {
    continue;
    }
    lstRet.addAll(lstRc.get(i).GetOpContent());
    }
    return lstRet;
    }
     */

    public void print4Test() {
        System.out.println("active " + this.lstRuleActive.size());
        for (int i = 0; i < this.lstRuleActive.size(); i++) {
            System.out.println("\t" + this.lstRuleActive.get(i).toString());
        }
        System.out.println("Inactive " + this.lstRuleInactive.size());
        for (int i = 0; i < this.lstRuleInactive.size(); i++) {
            System.out.println("\t" + this.lstRuleInactive.get(i).toString());
        }
        //check modifier option
    }

    public void restoreDatabase(String filename) {
    	this.restoreDatabase(this.outputDir, filename);
    }
    public void restoreDatabase(String folder, String filename) {
        try {
            String fileurl = folder + filename;
            Workbook workbook = Workbook.getWorkbook(new File(fileurl));
            Sheet sheet = workbook.getSheet("RuleSets");
            Sheet data = workbook.getSheet("signature");
            if (sheet == null) {
                return;
            }
            //restore ruleset 
            int index = 1;
            while (index < sheet.getRows()) {
                String rulesetname = sheet.getCell(0, index).getContents();
                int findex = Integer.parseInt(sheet.getCell(1, index).getContents());
                int lindex = Integer.parseInt(sheet.getCell(2, index).getContents());
                RuleSet rs = new RuleSet(rulesetname);
                for (int i = findex; i <= lindex; i++) {

                    LinkedList<String> lstdata = this.getDataRow(data, i);
                    RuleComponent rc = new RuleComponent(lstdata, rs);
//                    rs.parseBackupData(lstdata);
                    rs.addRule(rc);
                }
                if (rs.name.compareToIgnoreCase("deleted") == 0) {//deleted rules
                    this.lstRuleDeleted.addAll(rs.lstRuleAll);
                } else {
                    this.lstSnortRuleSet.add(rs);
                    this.lstRuleAll.addAll(rs.lstRuleAll);
                    this.lstRuleActive.addAll(rs.lstRuleActive);
                    this.lstRuleInactive.addAll(rs.lstRuleInactive);
                }
                index++;
            }

        } catch (IOException ex) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Backup Database into xls format for highly update.
     */
    public void backupDatabase(String folder, String filename){
    	try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(folder +  filename ));
            //fist pages is for rule summary
            WritableSheet sheet = workbook.createSheet("RuleSets", 0);
            int row = 1;
            Label cell = new Label(0, 0, "");
            int index = 1;
            for (int i = 0; i < this.lstSnortRuleSet.size(); i++) {
                RuleSet rs = this.lstSnortRuleSet.get(i);
                //first: ruleset name;
                cell = new Label(0, row, rs.getName());
                sheet.addCell(cell);
                //second: first index
                cell = new Label(1, row, Integer.toString(index));
                sheet.addCell(cell);
                //third: second index
                index = index + rs.lstRuleAll.size();
                cell = new Label(2, row, Integer.toString(index - 1));
                sheet.addCell(cell);
                row++;
            }
            //second pages is for rule summary
            row = 1;
            index = 1;
            
            sheet = workbook.createSheet("signature", 1);
            for (int i = 0; i < this.lstSnortRuleSet.size(); i++) {
                RuleSet rs = this.lstSnortRuleSet.get(i);
                for (int j = 0; j < rs.lstRuleAll.size(); j++) {
                    RuleComponent rc = rs.lstRuleAll.get(j);
                    int col = 0;
                    //active or inactive
                    cell = new Label(col, row, rc.isActive() ? "1" : "0");
                    sheet.addCell(cell);
                    col++;
                    //action
                    cell = new Label(col, row, rc.header.action);
                    sheet.addCell(cell);
                    col++;
                    //protocol
                    cell = new Label(col, row, rc.header.protocol);
                    sheet.addCell(cell);
                    col++;
                    //srcAddress
                    cell = new Label(col, row, rc.header.srcAddress);
                    sheet.addCell(cell);
                    col++;
                    //srcPort
                    cell = new Label(col, row, rc.header.srcPort);
                    sheet.addCell(cell);
                    col++;
                    //direction
                    cell = new Label(col, row, rc.header.direction);
                    sheet.addCell(cell);
                    col++;
                    //dstAddress
                    cell = new Label(col, row, rc.header.dstAddress);
                    sheet.addCell(cell);
                    col++;
                    //dstPort
                    cell = new Label(col, row, rc.header.dstPort);
                    sheet.addCell(cell);
                    col++;

                    //option
                    for (int k = 0; k < rc.lstOption.size(); k++) {
                        RuleOption ro = rc.lstOption.get(k);
                        cell = new Label(col, row, ro.optionName);
                        sheet.addCell(cell);
                        col++;

                        cell = new Label(col, row, ro.optionValue);
                        sheet.addCell(cell);
                        col++;
                    }
                    row++;
                }
            }
            workbook.write();
            workbook.close();
        } catch (WriteException wx) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, wx);
        } catch (IOException ex) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void backupDatabase(String filename) {
        this.backupDatabase(this.outputDir, filename);

    }

    private LinkedList<String> getDataRow(Sheet data, int index) {
        LinkedList<String> ldata = new LinkedList<String>();
        //System.out.println("row  " + index);
        Cell[] row = data.getRow(index);
        //System.out.println("row  " + index + " " + row.length);
        for (int i = 0; i < row.length; i++) {
            ldata.add(row[i].getContents());
        }
        return ldata;
    }
}
