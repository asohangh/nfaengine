package rule_v1;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class DoIt {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //RuleDatabase ruleDb=new RuleDatabase();
        //ruleDb.builDatabase();
        //ruleDb.print();

        String rule ="alert tcp $EXTERNAL_NET $HTTP_PORTS -> $HOME_NET any (msg:\"WEB-ACTIVEX Aliplay ActiveX clsid unicode access\"; flow:established,to_client; content:\"6|00|6|00|F|00|5|00|0|00|F|00|4|00|6|00|-|00|7|00|0|00|A|00|0|00|-|00|4|00|A|00|0|00|5|00|-|00|B|00|D|00|5|00|E|00|-|00|F|00|B|00|C|00|C|00|0|00|F|00|9|00|6|00|4|00|1|00|E|00|C|00|\"; nocase; pcre:\"/<\\x00o\\x00b\\x00j\\x00e\\x00c\\x00t\\x00(\\s\\x00)*([^>]\\x00)*c\\x00l\\x00a\\x00s\\x00s\\x00i\\x00d\\x00(\\s\\x00)*=\\x00(\\s\\x00)*(\\x22\\x00|\\x27\\x00|)c\\x00l\\x00s\\x00i\\x00d\\x00(\\s\\x00)*\\x3a\\x00(\\s\\x00)*({\\x00)?(\\s\\x00)*6\\x006\\x00F\\x005\\x000\\x00F\\x004\\x006\\x00-\\x007\\x000\\x00A\\x000\\x00-\\x004\\x00A\\x000\\x005\\x00-\\x00B\\x00D\\x005\\x00E\\x00-\\x00F\\x00B\\x00C\\x00C\\x000\\x00F\\x009\\x006\\x004\\x001\\x00E\\x00C\\x00(}\\x00)?\\5/si\"; metadata:policy security-ips drop; reference:bugtraq,22446; classtype:attempted-user; sid:10129; rev:3;)";
        RuleComponent rulecomp = new RuleComponent(rule,"temp");
        System.out.println("Rule is: " + rule);
        System.out.print("RuleSet: "+ rulecomp.ruleSet +"\n");
        System.out.println("Header: " + rulecomp.ruleHeader);
        System.out.println("PCRE: " + rulecomp.rulePcre);
        System.out.println("Content: " + rulecomp.ruleContent);
        RuleDatabase data = new RuleDatabase();
        Classification cls = new Classification();
        cls.BuildAll();
        cls.WriteFile();
        cls.WriteAllContent();
        //try {
            //data.parseRuleFolder2();
            //ruleDb.outputHeader();
            //ruleDb.outputRuleComponent();
            //ruleDb.outputSimplyPcre();
            //RulePcre temp=new RulePcre();
            //temp.parsePcre("alert tcp $HOME_NET any -> $EXTERNAL_NET 6666:7000 (msg:\"CHAT IRC channel notice\"; pcre:\"/^\\s*NOTICE/smi\"; metadata:policy security-ips drop; sid:6182; rev:2;)");
            //System.out.println(temp.pcre+" ... "+temp.modify+" ... "+temp.sid);
            //String [] temp=ProcessRule.splitByChar("alert tcp $HOME_NET any -> $EXTERNAL_NET 6666:7000 (msg:\"CHAT IRC channel notice\"; pcre:\"/^\\s*NOTICE/smi\"; metadata:policy security-ips drop; sid:6182; rev:2;)",';');
            //for(int i=0;i<temp.length;i++){
            //	System.out.println(temp[i]);
            //}
            /*char i = '9';
            char j = '0';
            System.out.println(Integer.valueOf(i) + "\n" + Integer.valueOf(j));*/
        //} catch (IOException ex) {
            //Logger.getLogger(DoIt.class.getName()).log(Level.SEVERE, null, ex);
        //}



        //ruleDb.outputHeader();
        //ruleDb.outputRuleComponent();
        //ruleDb.outputSimplyPcre();
        //RulePcre temp=new RulePcre();
        //temp.parsePcre("alert tcp $HOME_NET any -> $EXTERNAL_NET 6666:7000 (msg:\"CHAT IRC channel notice\"; pcre:\"/^\\s*NOTICE/smi\"; metadata:policy security-ips drop; sid:6182; rev:2;)");
        //System.out.println(temp.pcre+" ... "+temp.modify+" ... "+temp.sid);
        //String [] temp=ProcessRule.splitByChar("alert tcp $HOME_NET any -> $EXTERNAL_NET 6666:7000 (msg:\"CHAT IRC channel notice\"; pcre:\"/^\\s*NOTICE/smi\"; metadata:policy security-ips drop; sid:6182; rev:2;)",';');
        //for(int i=0;i<temp.length;i++){
        //	System.out.println(temp[i]);
        //}
        /*char i = '9';
        char j = '0';
        System.out.println(Integer.valueOf(i) + "\n" + Integer.valueOf(j));*/
    }
}
