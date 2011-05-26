/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

/**
 *
 * @author heckarim
 */
public class PacketAddress {

    public static int _single = 0;
    public static int _multiple = 1;
    public static int _range = 4;
    public static int _mulrange = 2; /// 192.168.1.0/24, ...
    public static int _any = 3;

    public static int _src = 0;
    public static int _dst = 1;
    public String data; // input data for parse address
    public String defaultAddress;
    public String address;
    private int id;
    private int type;

    public PacketAddress(String address,int type) {
        this.data = address;
        this.type = type;
        this.parseConfigName();
        this.parseAddress();
    }

    /**
     * 
     */
    private void parseAddress() {
        //any
        if (this.data.compareToIgnoreCase("any") == 0) {
            this.id = _any;
            if(this.type ==_src)
                this.defaultAddress = Configuration._default_SrcAddress;
            if(this.type ==_dst)
                this.defaultAddress = Configuration._default_DstAddress;
        } else{
            this.id = _single;
            this.address  = this.data;
        }

        //todo
    }

    private void parseConfigName() {
        //parse Conifgure name;
       // System.out.println("PacketAddress: " + this.data + " == " + "$HOME_NET");
        //System.out.println(this.data.compareToIgnoreCase("$HOME_NET"));
        if (this.data.compareToIgnoreCase("$HOME_NET") == 0) {
            this.data = Configuration.HOME_NET;
        } else if (this.data.compareToIgnoreCase("$EXTERNAL_NET") == 0) {
            this.data = Configuration.EXTERNAL_NET;
        } else if (this.data.compareToIgnoreCase("$DNS_SERVERS") == 0) {
            this.data = Configuration.DNS_SERVERS;
        } else if (this.data.compareToIgnoreCase("$HTTP_SERVERS") == 0) {
            this.data = Configuration.HTTP_SERVERS;
        } else if (this.data.compareToIgnoreCase("$SQL_SERVERS") == 0) {
            this.data = Configuration.SQL_SERVERS;
        } else if (this.data.compareToIgnoreCase("$TELNET_SERVERS") == 0) {
            this.data = Configuration.TELNET_SERVERS;
        } else if (this.data.compareToIgnoreCase("$SSH_SERVERS") == 0) {
            this.data = Configuration.SSH_SERVERS;
        } else if (this.data.compareToIgnoreCase("$SMTP_SERVERS") == 0) {
            this.data = Configuration.SMTP_SERVERS;
        }else if (this.data.compareToIgnoreCase("$AIM_SERVERS") == 0) {
            this.data = Configuration.AIM_SERVERS;
        }
    }

    /**
     *
     * @return
     */
    public String getMatchAddress(){
        //todo
        if(this.id == _any){
            return this.defaultAddress;
        }
        else if(this.id == _single)
            return this.address;
        else
            return this.defaultAddress;
    }

    String getAddress() {
        return this.defaultAddress;
    }
}
