package rule_v1;

/**
 *
 * @author heckarim
 */
public class PacketHeader {

    public String msg; // action
    public String protocol;
    public String source;
    public String portsource;
    public String portdes;
    public String des;
    public String direction; // <> ->
    public boolean isExist = false;
    public boolean same = false;

    PacketHeader() {
        this.isExist = false;
    }

    public void parseHeader(String rule) {
        String header = "";
        char c;
        for (int i = 0; i < rule.length(); i++) {
            if ((c = rule.charAt(i)) == '(') {
                break;
            }
            header = header + c;
        }
        header = header.trim();
        if (header == "") {
            return;
        }
        this.isExist = true;
        String[] s = header.split(" ");
        msg = s[0];
        protocol = s[1];
        source = s[2];
        portsource = s[3];
        direction = s[4];
        des = s[5];
        portdes = s[6];
    }

    public String getString() {
        return msg + " " + protocol + " " + source + " " + portsource + " " + direction + " " + des + " " + portdes;
    }

    public String getString1() {
        return protocol + " " + source + " " + portsource + " " + direction + " " + des + " " + portdes;
    }

    public String toString() {
        if (this.isExist == false) {
            return "...";
        }
        return msg + " " + protocol + " " + source + " " + portsource + " " + direction + " " + des + " " + portdes;
    }

    public String toString_2() {
        if (this.isExist == false) {
            return "...";
        }
        return protocol + " " + source + " " + portsource + " " + direction + " " + des + " " + portdes;
    }

    public boolean isLess(PacketHeader temp) {
        if (msg.compareToIgnoreCase(temp.msg) < 0) {
            return true;
        } else if (msg.compareToIgnoreCase(temp.msg) > 0) {
            return false;
        } else {
            if (protocol.compareToIgnoreCase(temp.protocol) < 0) {
                return true;
            } else if (protocol.compareToIgnoreCase(temp.protocol) > 0) {
                return false;
            } else {
                //return true;// TODO will be change in future
                if (source.compareToIgnoreCase(temp.source) < 0) {
                    return true;
                } else if (source.compareToIgnoreCase(temp.source) > 0) {
                    return false;
                } else {
                    if (des.compareToIgnoreCase(temp.des) < 0) {
                        return true;
                    } else if (des.compareToIgnoreCase(temp.des) > 0) {
                        return false;
                    } else {
                        return true;
                    }

                }
            }
        }
    }

    public boolean isEqual(PacketHeader pHeader) {
        /*if(this.des == pHeader.des && this.direction== pHeader.direction)
        if(this.msg==pHeader.msg && this.portdes==pHeader.portdes)
        if(this.portsource == pHeader.portsource	&& this.protocol==pHeader.protocol )
        if(this.source==pHeader.source)
        return true;*/

        if (this.toString().compareToIgnoreCase(pHeader.toString()) == 0) {
            return true;
        }
        return false;
    }
}
