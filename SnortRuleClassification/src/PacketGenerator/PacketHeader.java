/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PacketGenerator;

import snort_rule.RuleHeader;

/**
 *
 * @author heckarim
 */
public class PacketHeader {
    public PacketAddress srcAddress;
    public PacketAddress dstAddress;
    public PacketPort srcPort;
    public PacketPort dstPort;
    public String protocol;
    public RuleHeader rheader;
    public  PacketHeader(RuleHeader rheader){

        this.rheader = rheader;
        this.parseHeader();
        //System.out.println("Packetheader: " + this.srcPort);
    }

    private void parseHeader() {

        this.srcAddress = new PacketAddress(this.rheader.srcAddress, 0);
        this.dstAddress = new PacketAddress(this.rheader.dstAddress, 1);
        this.srcPort = new PacketPort(this.rheader.srcPort, 0);
        this.dstPort = new PacketPort(this.rheader.dstPort,1);
        this.protocol = this.rheader.protocol;


    }

}
