/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import PacketGenerator.Packet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.write.WriteException;
import VRTSignature.OpContent;
import VRTSignature.OptionMask;
import VRTSignature.PCRE;
import VRTSignature.References;
import VRTSignature.RuleDatabase;

/**
 *
 * @author heckarim
 */
public class GenPacket {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<OpContent> lcontent;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        GenPacket ex = new GenPacket();
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
        db.setRuleDir(System.getProperty("user.dir") + File.separator + "rules.2.9.t" + File.separator);
        db.buildDatabase();
        this.lcontent = this.getContent();
        this.lpcre = this.getPCRE();

        //this.outputExcel();
        //this.outputRules(this.outputfolder + "nids.rules");
        //this.genPacket();
        this.genMzFile(outputfolder + "pack.1.mz");


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

    private void genPacket() {
        for (int i = 0; i < this.lcontent.size(); i++) {
            OpContent op = this.lcontent.get(i);
            Packet pack = new Packet(op.getRule().header, op, 0);
            System.out.println(pack.genMZInstruction());
        }
        for (int i = 0; i < this.lpcre.size(); i++) {
            PCRE pcre = this.lpcre.get(i);
            Packet pack = new Packet(pcre.getRule().header, pcre, 1);
            System.out.println(pack.genMZInstruction());
        }
    }

    private void genMzFile(String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
           int base = this.lcontent.size();
           /*
            for (int i = 0; i < this.lcontent.size(); i++) {
                OpContent op = this.lcontent.get(i);
                Packet pack = new Packet(op.getRule().header, op, 0);
                System.out.println(pack.genPacketIntruction());
                bw.write("echo " + i+" -- " + op.getSID() +"\n");
                bw.write(pack.genPacketIntruction() + "\n");

            }*/
            for (int i = 0; i < this.lpcre.size(); i++) {
                PCRE pcre = this.lpcre.get(i);
                Packet pack = new Packet(pcre.getRule().header, pcre, 1);
                System.out.println(pack.genPacketIntruction());

                bw.write("echo " + (base+i)+" -- " + pcre.getSID() +"\n");
                bw.write(pack.genPacketIntruction() + "\n");
            }

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(GenPacket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
