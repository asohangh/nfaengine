package rule;

/**
 *
 * @author heckarim
 */
public final class RuleComponent {

    PacketHeader ruleHeader;            //Header paket
    RulePcre rulePcre;                  //Rule Pcre
    RuleContent ruleContent;            //Rule content
    String ruleSet;//Rule nay nam trong ruleSet nao.
    //int countpcre;
    boolean isExist;//Co  ton tai hay ko

    RuleComponent(String rule, String ruleSet) {
        this.ruleSet = ruleSet;
        this.ruleHeader = new PacketHeader();
        this.rulePcre = new RulePcre();
        this.ruleContent = new RuleContent();
        //this.countpcre = 0;
        this.isExist = false;
        parseRuleContent(rule);

        if (this.ruleHeader.isExist) {
            this.isExist = true;
        }
        //Todo : may be not need to keep rulePCre if it don't exits
    }

    public void parseRuleContent(String rule) {
        //boolean havepcre=false;

        //Get header

        this.ruleHeader.parseHeader(rule);

        //getpcre

        this.rulePcre.parsePcre(rule);

        //get content

        this.ruleContent.parseContent(rule);

    }

    public void print() {
        System.out.println(this.ruleSet + " <--> " + this.ruleHeader.toString() + " <--> " + this.rulePcre.toString());
    }

    @Override
    public String toString() {
        return this.ruleHeader + "; " + this.ruleContent + "; " + this.rulePcre;
    }
}
