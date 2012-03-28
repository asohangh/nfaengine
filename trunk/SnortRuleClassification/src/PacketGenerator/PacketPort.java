/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

import java.util.Random;

/**
 *
 * @author heckarim
 */
public class PacketPort {

    public static int _range = 0;
    public static int _single = 1;
    public static int _list = 2;
    public static int _largerthan = 3; //80:
    public static int _not = 4; // !80
    public static int _any = 5;
    String data;
    
    int type;
    int defaultPort = 80;
    int[] listport;
    int port;
    int port1;

    public PacketPort(String data, int type) {
        this.data = data;
        this.type = type;
        this.parseConfigName();
        this.parsePort();
    }

    public void parseConfigName() {
        /**
         *          *
        public static String HTTP_PORTS = "[80,311,591,593,901,1220,1414,2301,2381,2809,3128,3702,7777,7779,8000,8008,8028,8080,8118,8123,8180,8243,8280,8888,9443,9999,11371]";
        public static String SHELLCODE_PORTS = "!80";
        public static String ORACLE_PORTS = "1024:";
        public static String SSH_PORTS = "22";

         */
        if (this.data.compareToIgnoreCase("$HTTP_PORTS") == 0) {
            this.data = Configuration.HTTP_PORTS;
        } else if (this.data.compareToIgnoreCase("$SHELLCODE_PORTS") == 0) {
            this.data = Configuration.SHELLCODE_PORTS;
        } else if (this.data.compareToIgnoreCase("$ORACLE_PORTS") == 0) {
            this.data = Configuration.ORACLE_PORTS;
        } else if (this.data.compareToIgnoreCase("$SSH_PORTS") == 0) {
            this.data = Configuration.SSH_PORTS;
        } else {
            System.out.println("NO port configuration   " + this.data);
        }
    }

    /**
     * note:
     *      +, [80,88:] this case is = [80,88]
     */
    public void parsePort() {
        if (this.data.compareToIgnoreCase("any") == 0) {
            this.type = this._any;

        } else if (this.data.startsWith("[")) {
            //[80,331,56]
            this.data = this.data.substring(1, this.data.length()-1);
            this.type = this._list;
            String[] sPort = this.data.split(",");
            this.listport = new int[sPort.length];
            for (int i = 0; i < sPort.length; i++) {
                int index = sPort[i].indexOf(':');
                if(index != -1){
                    sPort[i] = sPort[i].substring(0,index);
                }
                this.listport[i] = Integer.parseInt(sPort[i]);
            }
        } else if (this.data.indexOf(":") != -1) {
            //port range
            String split[] = this.data.split(":");
            if (split.length == 1) {
                //80:
                this.type = this._largerthan;
                this.port = Integer.parseInt(split[0]);
            } else {
                //80:100
                this.type = this._range;
                this.port = Integer.parseInt(split[0]);
                this.port1 = Integer.parseInt(split[1]);
            }
        } else if (this.data.startsWith("!")) {
            //!80
            this.port = Integer.parseInt(this.data.substring(1));
            this.type = this._not;
        } else {
            this.port = Integer.parseInt(this.data);
            this.type = this._single;
        }

    }

    public String getPort() {
        String ret = "";
        Random rand = new Random();
        int p;
        if (this.type == this._any) {
            p = rand.nextInt(5926);
            p += 1;
        } else if (this.type == this._largerthan) {
            p = rand.nextInt(5926);
            p += this.port;
        } else if (this.type == this._list) {
            if(this.listport.length == 1)
                p = 0;
            else
                p = rand.nextInt(this.listport.length - 1);
            p = this.listport[p];
        } else if (this.type == this._not) {
            p = this.port + 13;
        } else if (this.type == PacketPort._range) {
            p = rand.nextInt(port1 - port);
            p += this.port;
        } else if (this.type == this._single) {
            p = this.port;
        } else {
            p = 0;
        }

        ret = p + "";

        return ret;
    }
}
