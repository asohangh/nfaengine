/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestingResult;

import PacketGenerator.Packet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.write.WriteException;
import snort_rule.OpContent;
import snort_rule.OptionMask;
import snort_rule.PCRE;
import snort_rule.References;
import snort_rule.RuleDatabase;

/**
 *
 * @author heckarim
 */
public class CheckResult {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<OpContent> lcontent;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        CheckResult ex = new CheckResult();
        ex.action();


        String test = "$HOME_NET";
        /*       if(test.compareToIgnoreCase("$HOME_NET")==0){
        System.out.print("ilove u");

        }else
        System.out.println("diff: " + test.compareToIgnoreCase("$HOME_NET"));
         */
    }

    private void action() {
        db = new RuleDatabase();
        db.rulefolder = System.getProperty("user.dir") + File.separator + "rules.2.9.t" + File.separator;
        db.BuildDatabase();
        this.lcontent = this.getContent();
        this.lpcre = this.getPCRE();

        //this.outputExcel();
        //this.outputRules(this.outputfolder + "nids.rules");
        //this.genPacket();
        //this.genMzFile(outputfolder + "pack.mz");

        this.processResult(outputfolder+"aler.out");

    }

    /**
     *  filter all rule wich our soft ware suppport
     */
    private LinkedList<PCRE> getPCRE() {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");
        LinkedList<PCRE> lpcre = new LinkedList<PCRE>();

        // Extract all pcre from rule set to simple.pcre

        for (int j = 0; j < db.lstRuleAll.size(); j++) {
            if (db.lstRuleAll.get(j).ApplyMask(mask)) {
                //bw.write(rs.lstRuleAll.get(j).value + "\n");
                PCRE pcre = (PCRE) db.lstRuleAll.get(j).getFirstOpPcre();
                lpcre.add(pcre);
            }
        }
        return lpcre;
    }

    public LinkedList<OpContent> getContent() {
        LinkedList<OpContent> lcontent = new LinkedList<OpContent>();
        OptionMask mask = new OptionMask();
        mask.SetForbid(References._opContentModifier);
        mask.SetDontCare("nocase");
        mask.SetForbid("uricontent");
        mask.SetDontCare("rawbytes");
        mask.SetDontCare("fast_pattern");
        mask.SetPermit("content");
        mask.SetForbid("pcre");

        for (int j = 0; j < db.lstRuleAll.size(); j++) {
            if (db.lstRuleAll.get(j).ApplyMask(mask)) {
                //have one content
                if (db.lstRuleAll.get(j).ruleStatus.GetOptionStatus("content").count == 1) {
                    //only one content
                    lcontent.add(db.lstRuleAll.get(j).GetOpContent().getFirst());
                }
            }
        }
        return lcontent;
    }

    private void processResult(String file) {
        LinkedList<MatchRule> lconmatch= new LinkedList<MatchRule>();
        LinkedList<MatchRule> lpcrematch= new LinkedList<MatchRule>();
        
        for(int i=0;  i<this.lcontent.size(); i++){
            lconmatch.add(i, new MatchRule(this.lcontent.get(i).getSID()));
        }
        for(int i=0;  i<this.lpcre.size(); i++){
            lpcrematch.add(i, new MatchRule(this.lpcre.get(i).getSID()));
        }
        try {
            // read file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s=br.readLine())!=null){
               String [] split = s.split(":");
               if(split.length >=2){
                   this.insertResult(lconmatch,split[1]);
                   this.insertResult(lpcrematch,split[1]);
               }
            }

            br.close();

            //wriet result
            BufferedWriter bw = new BufferedWriter(new FileWriter(file+".match"));
            bw.write("#CONTENT\n");
            bw.write("#CONTENT -match\n");
            for(int i=0; i<lconmatch.size(); i++){
                if(lconmatch.get(i).count !=0){
                    bw.write(lconmatch.get(i).sid +"\n");
                }
            }
            bw.write("#CONTENT -not match\n");
            for(int i=0; i<lconmatch.size(); i++){
                if(lconmatch.get(i).count ==0){
                    bw.write(lconmatch.get(i).sid +"\n");
                }
            }

            bw.write("#PCRE\n");
            bw.write("#PCRE -match\n");
            for(int i=0; i<lpcrematch.size(); i++){
                if(lpcrematch.get(i).count !=0){
                    bw.write(lpcrematch.get(i).sid +"\n");
                }
            }
            bw.write("#PCRE -not match\n");
            for(int i=0; i<lpcrematch.size(); i++){
                if(lpcrematch.get(i).count ==0){
                    bw.write(lpcrematch.get(i).sid +"\n");
                }
            }

            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(CheckResult.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private void insertResult(LinkedList<MatchRule> lconmatch,String sid) {
        for(int i=0;i<lconmatch.size();i++){
            if(lconmatch.get(i).sid.compareToIgnoreCase(sid) ==0){
                lconmatch.get(i).count++;
                break;
            }

        }
    }
}
