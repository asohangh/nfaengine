/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

/**
 *
 * @author heckarim
 */
public class RuleHeader {

    public String action;
    public String protocol;
    public String srcAddress;
    public String srcPort;
    public String dstAddress;
    public String dstPort;
    public String value;
    public String direction;
    RuleComponent rule;

    RuleHeader(String s, RuleComponent rule) {
        this.value = s;
        ParseHeader(s);
        this.rule = rule;
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

    public RuleComponent getRule() {
        return this.rule;
    }

    public String getSID() {
        if (this.rule != null) {
            return this.rule.sid;
        } else {
            return "0";
        }
    }

    @Override
    public String toString() {
        String ret;
        ret = this.action + " " + this.protocol + " " + this.srcAddress + " " + this.srcPort + " " + this.direction + " " + this.dstAddress + " " + this.dstPort;
        return ret;
    }
}
