package rule;

/**
 *
 * @author heckarim
 */
public class RulePcre {

    String pcre;
    String modify;
    String sid;
    int Contriant;
    boolean isExist = false;          // some rule don't have pcre;

    RulePcre() {
        pcre = "";
        sid = "";
        modify = "";
        Contriant = 0;
        isExist = false;
    }

    public void parsePcre(String rule) {
        String[] rsplit = Reference.splitByChar(rule, ';');

        String temp;
        for (int i = 0; i < rsplit.length; i++) {
            //System.out.println(rsplit[i]+"  ");

            if (rsplit[i].trim().startsWith("pcre:")) {
                temp = (rsplit[i].trim()).replaceAll("pcre:", "");
                temp = temp.replaceAll("\"", "");
                temp = temp.trim(); // something like /<pcre content>/[<modifier>]
                //split into pcre and modify
                String[] tsplit = Reference.splitByChar(temp, '/');
                if (tsplit.length == 0) {
                    return;
                }

                this.pcre = tsplit[0];
                if (tsplit.length > 1) {
                    this.modify = tsplit[1];
                }


            }
            if (rsplit[i].trim().startsWith("sid:")) {
                this.sid = rsplit[i].trim().replaceAll("sid:", "");
            }
        }

        if (this.pcre != "") {
            this.isExist = true;
        }
    }

    public String toString() {
        if (this.isExist == false) {
            return "";
        }
        return "/" + pcre + "/" + modify;
    }

    public void countContriant() {
        this.Contriant++;
    }
}
