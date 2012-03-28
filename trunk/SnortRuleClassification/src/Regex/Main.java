package Regex;



/**
 *
 * @author heckarim
 */
public class Main {

    public static void main(String[] args) {
        Main control = new Main();
        control.action();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest() {
        String rule1 = "/^.{20}([a-z]|0x00[a-z]|\\x00{2}[a-z]|\\x00{2}\\x01[a-z])/smi";
        String rule2 = "/a[bc]{,3}c/smi";

        PcreRule rule = new PcreRule(rule1);
        rule.formatConvenientCR(Refer._mode_no_CR);
        System.out.println("After format CRB :" + rule.getPattern());

        //buildHDLMain  builder = new buildHDLMain(null);
        // builder.togetherFolder = ".\\output\\review\\";
        //builder.pcreList.add(rule1);
        //builder.pcreList.add(rule2);
        //builder.doBuildList();
    }

    public void doOutput() {
        //Todo
    }

    public void action() {
        //ParseTree temp=new ParseTree("(g.a|a.t).((a.g|a.a.a)*)");
        //ParseTree temp=new ParseTree("(ga|at)((ag|aaa)*)");
        //String rule = "/b*c(a|b)*[ac]d/";
        //String rule = "/(ga|at)((ag|aaa)*)/";
        //String rule = "/FTPON\\d+\\s+TIME\\d+\\s+/smi";
        ///^Subject\x3A[^\r\n]*2\x2E41/smi
        //ParseTree temp=new ParseTree("b*c(a|b)*[ac]#");
        ///FROM|3A|\\s+HTTP_RAT_.*SUBJECT|3A|\\s+there\\s+is\\s+a\\s+HTTPRAT\\s+waiting\\s+4\\s+u\\s+on/sm
        //temp.printTree();
        //String rule="/\\x3Ctitle\\x3ETroya\\s+\\x2D\\s+by\\s+Sma\\s+Soft\\x3C\\x2Ftitle\\x3E/smi";
        //String rule = "/\\x2F(fn|s)\\x3F[\\r\\n]*si/smi";
        //String rule = "/a\\010[abc\\x3a]*b/smi";
        //String rule = "/abc/";
        //String rule = "/ab{5,34}c*/smi";
        // String rule = "/[\\?\\x20\\x3b\\x26]module=[a-zA-Z0-9]*[^\\x3b\\x26]/U";
        //String rule = "/\\x2F[abcd]*a/smi";
        //String rule = "/ab[^cd][\\x3A2a]/smi";
        //String rule = "/ldap\\x3A\\x2F\\x2F[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)/smi";
        //String rule = "/(form|module|report)\\s*=\\s*(\\x2e\\x2e|\\x2f|[a-z]\\x3a\\x5c)/i";
        //System.out.print (rule.substring(1, rule.length() - 2));
        //String rule = "/[\\?\\x20\\x3b\\x26]module=[a-zA-Z0-9]*[^\\x3b\\x26]/";
        //String rule = "/\\w\\d\\x3Fmodule=[ab]*[^\\x3b\\x26]/";
        //String rule = "/goaway\\?message=[^\\sno]{10,30}/smi";
        //String rule = "/^.{20}([a-z]|0x00[a-z]|\\x00{2}[a-z]|\\x00{2}\\x01[a-z])/smi";
        //System.out.print (rule);
        //String rule = "/^<window\\s+version\\s*=\\s*(\\?!(1\\.(0|2|4|5|6)))/smi";
        //String rule = "/a[a-z](m|n){120}z/smi";
        //String rule = "/^\\s*MAIL\\s+FROM\\s*\\x3A\\s*\\x3C?\\s*[^\\x3E\\s]{257}\\s*/mi";
        //String rule = "/^100013Agentsvr\\x5E\\x5EMerlin$/smi";
        // String rule = "/b*a{79}c/smi";
        String rule = "/^Location\\x3a(\\s*|\\s*\\r?\\n\\s+)URL\\s*\\x3a/smi";
        //String rule ="/^Content-Disposition\\x3a(\\s*|\\s*\\r?\\n\\s+)[^\\r\\n]*\\{[\\da-fA-F]{8}(-[\\da-fA-F]){3}-[\\da-fA-F]{12}\\}/smi";
        //C°o°n°t°e°n°t°-°D°i°s°p°o°s°i°t°i°o°n°\x3a°(\s*|\s*°\r?°\n°\s+)°[^\r\n]*°\{°[\da-fA-F]{8}°(-°[\da-fA-F]{4}){3}°-°[\da-fA-F]{12}°\} -------- smit
        //String rule ="/^Content-Disposition\\x3a(\\s*|\\s*\\r?\\n\\s+)[^\\r\\n]*\\{[\\da-fA-F]{8}(-[\\da-fA-F]{4}){3}-[\\da-fA-F]{12}\\}/smi";
        //String rule = "/ab[^\\r\\n]/smi";
        //String rule = "/^CSeq\\x3A[^\\r\\n]+[^\\x01-\\x08\\x0B1-8\\x0C\\128-\\011\\x0E-\\x1F\\126-\\127]/smi";
        //String rule = "/abc[aA-G]";
        //String rule = "/ab{3}c/smi";
        //String rule = "/(abcdefghijkl.123456789(n|p)zt*uv)|(a\\010[abc\\x3a]*b)|(abdsefgijkl.12e3s4e.*56789(n|p)zt*uv)/smi";
        //String rule = "/(ab|cd)xyz/smi";
        //String rule = "/(abc){4}/smi";
        //String rule = "/(ldap\\x3A\\x2F\\x2F[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F))|(abcdefghijkl.123456789(n|p)zt*uv)|(a\\010[abc\\x3a]*b)|(abdsefgijkl.12e3s4e.*56789(n|p)zt*uv)" +
        //      "|762e426f64793d225468697320636f6e666964656e7469616c20646f63756d65*656e762e4174746163686d656e74732e4164642822433a5c4d7920446f63756d656e74735c6d676f61743030332e646f632229" +
        //      "|4600a772082bc63b7570ac1e4406e8628a6103dec255e5643d93d833070403a90a670a0b255a3ebff8b0d824a8027576135df364375865f2460883168323e7a4c7b3b0974b09c68e03194d9608d8b0b990a745826559614b65e803888da0372bd1a9ff7f43d5f8ce7c7dc67dc5f1f47d14b2de5d01054f166f277424fb05eb115f14eafff24690246fb227ac/smi";
        System.out.println("Rule: " + rule);
        PcreRule pcre = new PcreRule(rule);
        ElementParser parse = new ElementParser(pcre.getPattern(),pcre.getModifier());
        parse.parseElement();
        parse.print();
        System.out.println("Finish");
    }
}
