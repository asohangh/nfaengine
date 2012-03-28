/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class NormalPacketGen {

    private String outputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator;

    public static void main(String[] args) {
        NormalPacketGen nor = new NormalPacketGen();

        nor.action();
    }

    private void action() {

        this.generateMzFile(this.outputfolder + "normal.mz");
    }

    private void generateMzFile(String file) {
        Random ran = new Random();
        int size = 1000;

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            String[] sam = new String[4];
            sam[0] = "HECKARIM";
            sam[1] = "BIGWORM";
            sam[2] = "TANTAI";
            sam[3] = "TMIP";
            for (int i = 0; i < size; i++) {
                String ints;
                int index = ran.nextInt(3);
                if (ran.nextBoolean()) {
                    ints = this.genMzTCP1(sam[index]);
                } else {
                    ints = this.genMzUDP1(sam[index]);
                }
                bw.write(ints + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(NormalPacketGen.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            p += Integer.toHexString((int) sample.charAt(i%size)) + ":";
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
