/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rule_v1;

/**
 *
 * @author user
 */
public class Summary {

    String ruleSet;
    Counter cntchar;
    Counter cntsub;
    Counter totalchar;
    Counter totalsub;    
    int totalRule;
    int reducedRule;    
    int totalPcre;
    int totalPcreNoRep;
    int totalPcresupported;
    int totalContent;
    int reducedContent;
    int totalContenthavePCRE;    
    int [] rulehaveOption;
    int [] countOption;

    Summary() {
        cntchar = new Counter();
        cntsub = new Counter();
        totalchar = new Counter();
        totalsub = new Counter();
        reducedContent = 0;
        reducedRule = 0;
        rulehaveOption = new int [60];
        totalRule = 0;
        totalPcresupported = 0;
        totalPcre = 0;
        totalPcreNoRep = 0;
        totalContent = 0;
        countOption = new int [60];
    }
}
