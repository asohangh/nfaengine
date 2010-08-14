package rule;

/**
 *
 * @author heckarim
 */
public class RuleContent {

    public String content;
    String alloption = "content; nocase; rawbytes; depth; offset; distance; within; http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header; http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode; fast_pattern; uricontent; urilen; isdataat; file_data; byte_test; byte_jump; ftpbounce; asn1; cvs; dce_iface; dce_opnum; dce_stub_data";
    String[] option = alloption.split("; ");
    boolean isExist;

    public RuleContent() {
        content = "";
        isExist = false;
    }

    public String parseContent(String rule) {
        String ret = "";
        int index;
        for (index = 0; index < rule.length(); index++) {
            if (rule.charAt(index) == '(') {
                rule = rule.substring(index + 1, rule.length()); // bo phan header cua rule va 2 dau ()
            }
        }
        //rule = rule.substring(index + 1, rule.length() ); // bo phan header cua rule va 2 dau ()
        String[] s = Reference.splitByChar(rule, ';');
        for (int i = 1; i < s.length; i++) {
            String temp = s[i];
            //System.out.println(temp);
            if (this.isInContent(temp)) {
                ret = ret + temp + "; ";
            }
        }
        //ret=ret+"; "
        this.content = ret;
        return ret;
    }

    public boolean isInContent(String s) {
        for (int i = 0; i < option.length; i++) {
//        if (s.startsWith("content:") || s.startsWith("nocase") || s.startsWith("rawbytes")
//                || s.startsWith("depth") || s.startsWith("offset") || s.startsWith("distance")
//                || s.startsWith("within") || s.startsWith("http_client_body") || s.startsWith("http_uri")
//                || s.startsWith("uricontent")) {
//            return true;
//        }
            if (s.startsWith(option[i])) {
                isExist = true;
                return true;
            }
        }
        return false;
    }

    public void print() {
        System.out.println(this.content);
    }

    public String toString() {
        return this.content;
    }
}
