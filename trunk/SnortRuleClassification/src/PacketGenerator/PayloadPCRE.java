/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

import PcreParseTree.ParseTree;
import PcrePatternGenerator.Contructor;
import java.util.regex.Pattern;
import VRTSignature.PCRE;

/**
 *
 * @author heckarim
 */
public class PayloadPCRE {

    public PCRE pcre;
    public String data;
    public String hex;

    PayloadPCRE(PCRE pCRE) {
        this.pcre = pCRE;
        this.createStringPayload();
    }

    String getHexPayload() {
        System.out.println("PayloadPCRE: ... ");
        System.out.println(this.pcre.getOptionValue());
        System.out.println(this.data);
        this.createPayloadHex();
        System.out.println(this.hex);
        return this.hex;
    }

    private void createStringPayload() {
        ParseTree ps = new ParseTree(pcre.getOptionValue());
        Contructor con = new Contructor(ps);
        PcrePatternGenerator.Pattern pt = con.BuildPattern(true);
        this.data = pt.data;
    }

    public void createPayloadHex() {
        String ret = "";
        for (int i = 0; i < this.data.length(); i++) {
            char ch = this.data.charAt(i);
            ret += Integer.toHexString((int) ch) + ":";
        }
        ret = ret.substring(0, ret.length() - 1);
        this.hex = ret;
    }
}
