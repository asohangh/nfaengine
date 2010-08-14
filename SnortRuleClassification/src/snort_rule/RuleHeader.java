/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snort_rule;

/**
 *
 * @author heckarim
 */
public class RuleHeader {

    String action;
    String protocol;
    String srcAddress;
    String srcPort;
    String dstAddress;
    String dstPort;
    String value;
    String direction;

    RuleHeader(String s) {
        this.value = s;
        ParseHeader(s);
    }

    public void ParseHeader(String sheader) {
        /* String header = "";
        char c;
        for (int i = 0; i < rule.length(); i++) {
        if ((c = rule.charAt(i)) == '(') {
        break;
        }
        header = header + c;
        }
        header = header.trim();
        if (header.isEmpty()) {
        return;
        }
         */
        //System.out.println("parse header" + sheader);
        this.value = sheader;
        String[] s = sheader.split(" ");

        action = s[0];
        protocol = s[1];
        srcAddress = s[2];
        srcPort = s[3];
        direction = s[4];
        dstAddress = s[5];
        dstPort = s[6];
    }

    @Override
    public String toString() {
        String ret;
        ret = this.action + " " + this.srcAddress + " " + this.srcPort + " " + this.direction + " " + this.dstAddress + " " + this.dstPort;
        return ret;
    }
}
