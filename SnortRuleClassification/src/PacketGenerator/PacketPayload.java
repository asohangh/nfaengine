/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

/**
 *
 * @author heckarim
 */
public class PacketPayload {

    public static int _content = 0;
    public static int _pcre = 1;
    int id;
    PayloadContent pcon;
    PayloadPCRE ppcre;

    public PacketPayload(PayloadContent con) {
        this.id = _content;
        this.pcon = con;
    }

    public PacketPayload(PayloadPCRE pcre) {
        this.ppcre = pcre;
        this.id = _pcre;
    }
    /**
     * Return bye array of payload
     *
     * AB:10:23:23
     * 
     *
     */
    public String getPayloadChar(){
        String ret =null;
        return ret;

    }

    public String getPayloadHex() {
        if(this.id == this._content){
            return this.pcon.getHexPayload();
        }else
            //return "abcefg";
            return this.ppcre.getHexPayload();

    }
}
