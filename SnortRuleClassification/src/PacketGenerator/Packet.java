/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

import java.util.Random;
import snort_rule.OpContent;
import snort_rule.PCRE;
import snort_rule.RuleComponent;
import snort_rule.RuleHeader;
import snort_rule.RuleOption;

/**
 *
 * @author heckarim
 */
public class Packet {

    PacketHeader header;
    PacketPayload payload;
    String iface = "vboxnet0";
    Random ran = new Random();

    public void setInterface(String in) {
        this.iface = in;
    }

    public Packet(RuleHeader head, RuleOption op, int id) {

        this.header = new PacketHeader(head);
        System.out.println(head.value);
        if (id == 0) {//content
            this.payload = new PacketPayload(new PayloadContent((OpContent) op));
        } else {
            this.payload = new PacketPayload(new PayloadPCRE((PCRE) op));
        }
    }
    //mz eth0 -t ip -A 10.1.0.1-10.1.255.254 -B 255.255.255.255 p=ca:fe:ba:be

    public String genPacketIntruction() {
        if (this.header.protocol.compareToIgnoreCase("icmp") == 0) {
            return genICMPIntruction();
        } else {
            return genMZInstruction();
        }
    }

    public String genMZInstruction() {

        String ret = "";
        ret += "mz " + iface + " ";

        //src address and des address
        ret += "-A " + this.header.srcAddress.getAddress()
                + " -B " + this.header.dstAddress.getAddress() + " ";
        //protocol
        ret += "-t " + this.header.protocol + " ";
        ret += "sp=" + this.header.srcPort.getPort() + ",dp=" + this.header.dstPort.getPort() + ",p=" + this.payload.getPayloadHex();

        return ret;
    }

    private String genICMPIntruction() {
        String ret = "";
        ret += "ping " + this.header.dstAddress.getAddress() + " -I " + iface + " ";
        ret += "-c 1 -p ";
        ret += this.payload.getPayloadHex().replaceAll(":", "");

        return ret;
    }

    public String genPacketIntruction(int min, int max) {
        if (this.header.protocol.compareToIgnoreCase("icmp") == 0) {
            return genICMPIntruction(min, max);
        } else {
            return genMZInstruction(min, max);
        }
    }

    private String genICMPIntruction(int min, int max) {
        String ret = "";
        String[] sam = new String[4];
        sam[0] = "HECKARIM";
        sam[1] = "BIGWORM";
        sam[2] = "TANTAI";
        sam[3] = "TMIP";
        int len;
        int index = ran.nextInt(3);
        String sample = sam[index];
        //create payload
        String p = "";
        if (min == max) {
            len = min;
        } else {
            len = min + ran.nextInt(Math.abs(max - min));
        }
        int size = sample.length();
        p = this.payload.getPayloadHex();
        int psize = p.length();
        if (psize < len) {
            p += ":";
            for (int i = 0; i < (len - psize); i++) {
                p += Integer.toHexString((int) sample.charAt(i % size)) + ":";
            }
            p += "00";
        }
        //create packet
        ret += "ping " + this.header.dstAddress.getAddress() + " -I " + iface + " ";
        ret += "-c 1 -p ";
        ret += p.replaceAll(":", "");

        return ret;
    }

    private String genMZInstruction(int min, int max) {
        String ret = "";
        String[] sam = new String[4];
        sam[0] = "HECKARIM";
        sam[1] = "BIGWORM";
        sam[2] = "TANTAI";
        sam[3] = "TMIP";
        int len;
        int index = ran.nextInt(3);
        String sample = sam[index];
        //create payload
        String p = "";
        if (min == max) {
            len = min;
        } else {
            len = min + ran.nextInt(Math.abs(max - min));
        }
        int size = sample.length();
        p = this.payload.getPayloadHex();
        int psize = p.length();
        if (psize < len) {
            p += ":";
            for (int i = 0; i < (len - psize); i++) {
                p += Integer.toHexString((int) sample.charAt(i % size)) + ":";
            }
            p += "00";
        }
        ///create mz instruction
        ret += "mz " + iface + " ";

        //src address and des address
        ret += "-A " + this.header.srcAddress.getAddress()
                + " -B " + this.header.dstAddress.getAddress() + " ";
        //protocol
        ret += "-t " + this.header.protocol + " ";
        ret += "sp=" + this.header.srcPort.getPort() + ",dp=" + this.header.dstPort.getPort() + ",p=" + p;
        System.out.println("genMZInstruction(int min, int max) : " + ret);
        return ret;
    }
}
