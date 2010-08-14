/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snort_rule;

/**
 *
 * @author heckarim
 */
public class PCRE extends RuleOption {

    String pcre;
    String modify;
    // String sid;

    PCRE(String sop) {
        super(sop);
        this.isPCRE = true;
        pcre = "";
        modify = "";
        this.ParsePcre(sop);
    }

    public void ParsePcre(String rule) {

        String temp = null;
        if (rule.startsWith("pcre:")) {
            temp = (rule.replaceAll("pcre:", ""));
            temp = temp.trim().substring(1, temp.length() - 1);
            //bo " va "
            temp = temp.trim(); // something like /<pcre content>/[<modifier>]
            //split into pcre and modify
            String[] tsplit = References.splitByChar(temp, '/');
            if (tsplit.length == 0) {
                return;
            }

            this.pcre = tsplit[0];
            if (tsplit.length > 1) {
                this.modify = tsplit[1];
            }
        }
    }

    @Override
    public String toString() {

        return "pcre: ////" + pcre + "////" + modify;
    }
}
