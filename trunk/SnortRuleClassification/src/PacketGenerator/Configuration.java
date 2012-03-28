/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PacketGenerator;

/**
 *
 * @author heckarim
 */
public class Configuration {

    public static String _default_SrcAddress = "192.168.56.1";
    public static String _default_DstAddress = "192.168.56.101";
    public static String HOME_NET = "any";
    public static String EXTERNAL_NET = "any";
    public static String DNS_SERVERS = HOME_NET;
    public static String SMTP_SERVERS = HOME_NET;
    public static String HTTP_SERVERS = HOME_NET;
    public static String SQL_SERVERS = HOME_NET;
    public static String TELNET_SERVERS = HOME_NET;
    public static String SSH_SERVERS = HOME_NET;
    public static String AIM_SERVERS = HOME_NET;
    //public static String AIM_SERVERS = "[64.12.24.0/23,64.12.28.0/23,64.12.161.0/24,64.12.163.0/24,64.12.200.0/24,205.188.3.0/24,205.188.5.0/24,205.188.7.0/24,205.188.9.0/24,205.188.153.0/24,205.188.179.0/24,205.188.248.0/24]";
    //============================================================
    public static String HTTP_PORTS = "[80,311,591,593,901,1220,1414,2301,2381,2809,3128,3702,7777,7779,8000,8008,8028,8080,8118,8123,8180,8243,8280,8888,9443,9999,11371]";
    public static String SHELLCODE_PORTS = "!80";
    public static String ORACLE_PORTS = "1024:";
    public static String SSH_PORTS = "22";
}
