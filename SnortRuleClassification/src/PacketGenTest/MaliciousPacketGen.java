package PacketGenTest;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import PacketGenerator.Packet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
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
public class MaliciousPacketGen {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;
    private RuleDatabase db;
    LinkedList<OpContent> lcontent;
    LinkedList<PCRE> lpcre;
    LinkedList<PCRE> lconstraint;

    public static void main(String[] args) throws IOException, WriteException {
        MaliciousPacketGen ex = new MaliciousPacketGen();
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
        //this.genMzFile50(outputfolder + "pack.mz");

        //this.genMzFilePercent(this.outputfolder + "pack_25.mz", 1, 3);
        //this.genMzFilePercent(this.outputfolder + "pack_50.mz", 1, 1);
        //this.genMzFilePercent(this.outputfolder + "pack_75.mz", 3, 1);
        this.genMzFilePercent(this.outputfolder + "pack_100.mz", 1, 0);
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
            for (int i = 0; i < this.lcontent.size(); i++) {
                OpContent op = this.lcontent.get(i);
                Packet pack = new Packet(op.getRule().header, op, 0);
                System.out.println(pack.genPacketIntruction());
                bw.write("echo " + i + " -- " + op.getSID() + "\n");
                bw.write(pack.genPacketIntruction() + "\n");

            }
            for (int i = 0; i < this.lpcre.size(); i++) {
                PCRE pcre = this.lpcre.get(i);
                Packet pack = new Packet(pcre.getRule().header, pcre, 1);
                System.out.println(pack.genPacketIntruction());

                bw.write("echo " + (base + i) + " -- " + pcre.getSID() + "\n");
                bw.write(pack.genPacketIntruction() + "\n");
            }

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(MaliciousPacketGen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param filename
     * @param m   number of malicious packet
     * @param g   number of backgourn packet
     * note:
     *      +, if we want to generate 20% : m=1  g=4.
     */
    private void genMzFilePercent(String filename, int m, int g) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            int base = this.lcontent.size();
            int index;


            index = 0;
            while (index < this.lcontent.size()) {
                for (int i = 0; i < m && index <this.lpcre.size(); i++, index++) {
                    OpContent op = this.lcontent.get(index);
                    Packet pack = new Packet(op.getRule().header, op, 0);
                    System.out.println(pack.genPacketIntruction());
                    bw.write("echo " + index + " -- " + op.getSID() + "\n");
                    bw.write(pack.genPacketIntruction() + "\n");
                }
                for (int i = 0; i < g; i++) {
                    bw.write(this.genNormalPacket() + "\n");
                }
            }
            index = 0;
            while (index < this.lpcre.size()) {
                for (int i = 0; i < m && index <this.lpcre.size(); i++, index++) {
                    PCRE pcre = this.lpcre.get(index);
                    Packet pack = new Packet(pcre.getRule().header, pcre, 1);
                    System.out.println(pack.genPacketIntruction());

                    bw.write("echo " + (base + index) + " -- " + pcre.getSID() + "\n");
                    bw.write(pack.genPacketIntruction() + "\n");
                }
                for (int i = 0; i < g; i++) {
                    bw.write(this.genNormalPacket() + "\n");
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(MaliciousPacketGen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String genNormalPacket() {
        String[] sam = new String[4];
        sam[0] = "HECKARIM";
        sam[1] = "BIGWORM";
        sam[2] = "TANTAI";
        sam[3] = "TMIP";
        Random ran = new Random();
        String ints;
        int index = ran.nextInt(3);
        if (ran.nextBoolean()) {
            ints = this.genMzTCP1(sam[index]);
        } else {
            ints = this.genMzUDP1(sam[index]);
        }
        return ints;
    }

    public String genMzTCP1(String sample) {
        String ret = "";
        String iface = "vboxnet0";
        //create payload
        String p = "";
        int size = sample.length();
        for (int i = 0; i < 999; i++) {
            p += Integer.toHexString((int) sample.charAt(i % size)) + ":";
        }
        p += "00";
        //create instruction
        ret += "mz " + iface + " ";
        //src address and des address
        ret += "-A 192.168.56.1 -B 192.168.56.101 ";
        //protocol
        ret += "-t tcp ";
        ret += "sp=2" + ",dp=10" + ",p=" + p;
        return ret;
    }

    public String genMzUDP1(String sample) {
        String ret = "";
        String iface = "vboxnet0";

        //create payload
        String p = "";
        int size = sample.length();
        for (int i = 0; i < 999; i++) {
            p += Integer.toHexString((int) sample.charAt(i % size)) + ":";
        }
        p += "00";
        //create instruction
        ret += "mz " + iface + " ";
        //src address and des address
        ret += "-A 192.168.56.1 -B 192.168.56.101 ";
        //protocol
        ret += "-t udp ";
        ret += "sp=2" + ",dp=10" + ",p=" + p;
        return ret;
    }
}
