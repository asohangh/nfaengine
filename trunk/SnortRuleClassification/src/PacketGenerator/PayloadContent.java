/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

import VRTSignature.OpContent;

/**
 *
 * @author heckarim
 */
public class PayloadContent {

    String hex = "";
    String data;
    OpContent op;

    public PayloadContent(OpContent opContent) {
        this.op = opContent;
        System.out.println("PayloadContent: ... ");
        System.out.println(this.op.getOptionValue());
        this.createPayloadString();
    }
    //*VER1.22|28|REI|29|

    public void createPayloadString() {
        String value = this.op.getOptionValue();
        this.data = "";
        for (int i = 0; i < value.length();) {
            char ch = value.charAt(i);
            if (ch == '|') {
                String s = "";
                for (i++; i < value.length(); i++) {
                    if (value.charAt(i) == '|') {
                        break;
                    } else {
                        s += value.charAt(i);
                    }
                }
                i++;
                String split[] = s.trim().split(" ");
                for(int j =0; j<split.length;j++){
                    this.data += (char)Integer.parseInt(split[j],16);
                }
            } else {
                this.data +=ch;
                i++;
            }
        }
    }
    public void createPayloadHex(){
        String ret ="";
        for(int i =0;i<this.data.length(); i++){
            char ch = this.data.charAt(i);
            ret += Integer.toHexString((int)ch)+":";
        }
        ret = ret.substring(0,ret.length()-1);
        this.hex = ret;
    }

    public String getStringPayload(){
        return this.data;
    }
    public String getHexPayload() {
        this.createPayloadHex();
        System.out.println("PayloadContent: ... ");
        System.out.println(this.op.getOptionValue());
        System.out.println(this.data);
        System.out.println(this.hex);
        return this.hex;
    }
}
