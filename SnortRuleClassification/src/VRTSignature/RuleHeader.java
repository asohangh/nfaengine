/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VRTSignature;

import java.util.LinkedList;

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

    RuleHeader(LinkedList<String> lHeader, RuleComponent rule) {
        //todo this.setValue(lHeader);
        ParseBackupData(lHeader);
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

    private void ParseBackupData(LinkedList<String> s) {
        //System.out.println(s.size());
        action = s.get(0);
        protocol = s.get(1);
        srcAddress = s.get(2);
        srcPort = s.get(3);
        direction = s.get(4);
        dstAddress = s.get(5);
        dstPort = s.get(6);
    }
}
