package rule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class RuleDatabase {
    //Summary

    LinkedList<Summary> listactiveRule;
    LinkedList<Summary> listinactiveRule;
    LinkedList<Summary> listallRule;
    Summary allRule;
    Summary inactiveRule;
    Summary activeRule;
    //
    LinkedList<String> listContent = new LinkedList<String>();
    String alloption = "content; nocase; rawbytes; depth; offset; distance; within; http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header; http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode; fast_pattern; uricontent; urilen; isdataat; file_data; byte_test; byte_jump; ftpbounce; asn1; cvs; dce_iface; dce_opnum; dce_stub_data";
    String[] option = alloption.split("; ");
    String currentDir = System.getProperty("user.dir");
    String rulefolder = currentDir + "\\rule_1\\";
    String outputfolder = currentDir + "\\Result\\";
    //String outputfolder = "D:\\SnortRuleClassification\\rule\\Result\\";
    //String rulefolder ="/media/store/Work/NIDS/Snort/rule/18_08_09/rules/";
    //String outputfolder="/media/store/Work/NIDS/Snort/rule/18_08_09/output1/";
    String headeroutput = outputfolder + "header.mip";
    String pcrenorep = outputfolder + "pcrenorep.mip";
    String pcrebyruleset = outputfolder + "pcrebyruleset.mip";
    String pcrebyheader = outputfolder + "pcrebyheader.mip";
    String pcresimply = outputfolder + "pcresimply.mip";
    String filteroutput = outputfolder + "resultfilter.mip";
    //int countpcre;
    File[] arrayRuleFile;
    LinkedList<RuleComponent> listInactiveRule;
    LinkedList<RuleComponent> listRuleComponent; //store every rule component
    LinkedList<RuleComponent> listRuleComponentsuppportedPcre; //store ruleComponent which include supported prce
    LinkedList<RuleComponent> listRuleComponentNoPcreRep;//Store ruleComponent but no repetition of pcre
    LinkedList<RuleComponent> listRulehavePcre; // store every rule component which has pcre
    LinkedList<RuleComponent> listContriantPcre;
    LinkedList<RuleComponent> listNoContriantPcre;

    RuleDatabase() {
        arrayRuleFile = null;
        listRuleComponent = new LinkedList<RuleComponent>();
        listInactiveRule = new LinkedList<RuleComponent>();

        listactiveRule = new LinkedList<Summary>();
        listinactiveRule = new LinkedList<Summary>();
        inactiveRule = new Summary();
        activeRule = new Summary();
    }

    RuleDatabase(String rulefolder) {
        listRuleComponent = new LinkedList<RuleComponent>();
        listInactiveRule = new LinkedList<RuleComponent>();
        listactiveRule = new LinkedList<Summary>();
        listinactiveRule = new LinkedList<Summary>();
        inactiveRule = new Summary();
        activeRule = new Summary();
        this.rulefolder = rulefolder;
    }

    public void builDatabase() throws IOException {
        //Todo        
        parseRuleFolder();
//        for(int i = 0; i <10; i++)
//            //System.out.println("Here is Content: " + listRuleComponent.get(i).ruleContent.toString());
//            listRuleComponent.get(i).print();
        //killRepetitionPCRE();
        //File read = new File(rulefolder + "choose.mip");
        //String[] result = new ;
        //parseSid(read, result);
        //outputFilterSid();
    }

    public File[] getRuleFiles(String rulefolder) {
        File[] ret = new File(rulefolder).listFiles(new RulesFilter(".rules"));
        System.out.println("number of file in " + rulefolder + " is: " + ret.length);

        for (int i = 0; i < ret.length; i++) {
            //System.out.println(ret[i].getName());
        }
        return ret;
    }

    public void parseRuleFolder() {
        arrayRuleFile = getRuleFiles(rulefolder);
        for (int i = 0; i < arrayRuleFile.length; i++) {
            //System.out.println(arrayRuleFile[i]);
            parseRuleFile(arrayRuleFile[i]);
        }
    }

    public int MatchAction(String tar) {
        String action = "alert; log; pass; activate; dynamic; drop; reject; sdrop";
        String[] split = action.split("; ");
        for (int i = 0; i < split.length; i++) {
            if (tar.startsWith(split[i])) {
                return 1;
            } else if (tar.startsWith("# " + split[i])) {
                return -1;
            }
        }
        return 0;
    }

    /**
     *
     * @param rfile
     *
     * Nhan tham so la mot rule file, doc tung dong lay ra rule Component roi add vao listRuleComponent
     */
    public void parseRuleFile(File rfile) {
        String s = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(rfile));
            //System.out.println("parse rules file: " + rfile.getName());  
            while ((s = br.readLine()) != null) {
                //System.out.println(s);
                if (MatchAction(s) == -1) {
                    s = s.substring(2);
                    RuleComponent ruleComponent = new RuleComponent(s, rfile.getName());
                    if (ruleComponent.isExist) {
                        listInactiveRule.add(ruleComponent);
                        //listRuleComponent.size();
                    }
                } else if (MatchAction(s) == 1) {
                    RuleComponent ruleComponent = new RuleComponent(s, rfile.getName());
                    if (ruleComponent.isExist) {
                        listRuleComponent.add(ruleComponent);
                        //listRuleComponent.size();
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException t1) {
            System.err.println(t1);
        } catch (IOException t2) {
            System.err.println(t2);
        }
    }

    public void print() {
        for (int i = 0; i < listRuleComponent.size(); i++) {
            listRuleComponent.get(i).print();
        }
    }

    /*
     * Now will  DO OUTPUT:
     */
    public void killRepetitionPCRE(LinkedList<RuleComponent> listRuleComponent) {
        listRuleComponentNoPcreRep = new LinkedList<RuleComponent>();
        listRulehavePcre = new LinkedList<RuleComponent>();
        for (int i = 0; i < listRuleComponent.size(); i++) {
            RuleComponent temp = listRuleComponent.get(i);
            //Neu rule ko co pcre thi bo qua
            if (!temp.rulePcre.isExist) {
                //listRuleComponent.remove(i);
                continue;
            }
            //countpcre++;
            listRulehavePcre.add(temp);
            boolean same = false;
            for (int j = 0; j < listRuleComponentNoPcreRep.size(); j++) {
                if (listRuleComponentNoPcreRep.get(j).rulePcre.pcre.compareToIgnoreCase(temp.rulePcre.pcre) == 0) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                listRuleComponentNoPcreRep.add(temp);
            }
        }
    }

    public boolean supportPcre(String pcre) {
        char pre;
        char cur;
        boolean valid = true;
        for (int i = 0; i < pcre.length() - 1; i++) {
            pre = pcre.charAt(i);
            cur = pcre.charAt(i + 1);
            if (pre == '\\') {
                if (Integer.valueOf(cur) > 47 && Integer.valueOf(cur) < 58) {
                    valid = false;
                }
            } else if (cur == '?' && pre == '(') {
                if (i == 0 || pcre.charAt(i - 1) != '\\') {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public void listSupportedPcre() {
        listRuleComponentsuppportedPcre = new LinkedList<RuleComponent>();
        for (int i = 0; i < listRuleComponentNoPcreRep.size(); i++) {
            RuleComponent temp = listRuleComponentNoPcreRep.get(i);
            if (supportPcre(temp.rulePcre.pcre)) {
                listRuleComponentsuppportedPcre.add(temp);
            }
        }
    }

    /*public boolean ContriantPcre(String pcre) {
    char cur;
    boolean valid = false;
    for (int i = 0; i < pcre.length() - 1; i++) {
    cur = pcre.charAt(i + 1);
    if (cur == '{' & pcre.charAt(i) != '\\') {
    valid = true;
    }
    }
    return valid;
    }

    public void listContriantPcre(LinkedList<RuleComponent> rule) {
    listContriantPcre = new LinkedList<RuleComponent>();
    listNoContriantPcre = new LinkedList<RuleComponent>();
    for (int i = 0; i < rule.size(); i++) {
    RuleComponent temp = rule.get(i);
    if (ContriantPcre(temp.rulePcre.pcre)) {
    listContriantPcre.add(temp);
    } else {
    listNoContriantPcre.add(temp);
    }
    }
    }*/
    public void countrepetition(LinkedList<RuleComponent> ruleComponent, Summary activeRule) {
        //System.out.println("Begin count Repetition:");
        //BufferedReader br = new BufferedReader(new FileReader(pcrefilepath));
        LinkedList listrepet = new LinkedList();
        LinkedList listchar = new LinkedList();
        listContriantPcre = new LinkedList<RuleComponent>();
        listNoContriantPcre = new LinkedList<RuleComponent>();
        String s;
        for (int k = 0; k < ruleComponent.size(); k++) {
            s = ruleComponent.get(k).rulePcre.pcre;
            String temp = "";
            char c;
            boolean repetition = false;
            boolean chart = false;
            //boolean subRegex = false;
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                if (repetition) {
                    if ((c != ',') && (c != '{') && (c != '}') && (c > '9' || c < '0')) {
                        repetition = false;
                        temp = "";
                    } else {
                        temp += c;
                    }
                }
                if (c == '{' && i > 0 && s.charAt(i - 1) != '\\') {
                    repetition = true;
                    if (s.charAt(i - 1) != ')' || (i > 1 && s.charAt(i - 2) == '\\')) {
                        chart = true;
                    }
                }
                if (c == '}' && repetition) {
                    repetition = false;
                    ruleComponent.get(k).rulePcre.countContriant();
                    String temp1 = temp.replaceAll("}", "");
                    if (!chart) {
                        listrepet.add(temp1);
                    } else {
                        listchar.add(temp1);
                    }
                    chart = false;
                    temp = "";
                }
            }
            if (ruleComponent.get(k).rulePcre.Contriant > 0) {
                listContriantPcre.add(ruleComponent.get(k));
            } else {
                listNoContriantPcre.add(ruleComponent.get(k));
            }
        }
        //Bat dau kiem tra repetition        
        for (int i = 0; i < listrepet.size(); i++) {
            String temp = (String) listrepet.get(i);
            int temprepet;
            if (temp.indexOf(',') == -1) {
                activeRule.cntsub.exactlyrep++;
                temprepet = Integer.parseInt(temp);
                activeRule.cntsub.exactlylist[temprepet]++;
            } else if (temp.endsWith(",")) {
                activeRule.cntsub.atleastrep++;
                temprepet = Integer.parseInt(temp.replaceAll(",", "").trim());
                activeRule.cntsub.atleastlist[temprepet]++;
            } else {
                activeRule.cntsub.betweenrep++;
            }
        }
        for (int i = 0; i < listchar.size(); i++) {
            String temp = (String) listchar.get(i);
            int temprepet;
            if (temp.indexOf(',') == -1) {
                activeRule.cntchar.exactlyrep++;
                temprepet = Integer.parseInt(temp);
                activeRule.cntchar.exactlylist[temprepet]++;
            } else if (temp.endsWith(",")) {
                activeRule.cntchar.atleastrep++;
                temprepet = Integer.parseInt(temp.replaceAll(",", "").trim());
                activeRule.cntchar.atleastlist[temprepet]++;
            } else {
                activeRule.cntchar.betweenrep++;
            }
        }
        activeRule.cntsub.calAllrep();
        activeRule.cntchar.calAllrep();
    }

    public void WriteList(LinkedList<RuleComponent> listRuleComponent, Summary activeRule, Summary sumactiveRule, BufferedWriter res) throws IOException {
//        int[] countOption = new int[40];
//        int totalOpCont = 0;
//        int totalContent = 0;
//        int totalContentPCRE = 0;
//        int tmpCount = 0;
        //System.out.println("Write list : " + listRuleComponent.size());
        for (int i = 0; i < listRuleComponent.size(); i++) {
            String[] split = listRuleComponent.get(i).ruleContent.content.split("; ");
            boolean[] add = new boolean[60];
            for(int k =0; k < 60; k++)
                add[k] = false;
            for (int j = 0; j < split.length; j++) {
                String tmp = split[j];             
                for (int k = 0; k < option.length; k++) {
                    if (tmp.startsWith(option[k])) {
                        if (!add[k]) {
                            add[k] = true;
                            sumactiveRule.rulehaveOption[k]++;
                            activeRule.rulehaveOption[k]++;
                        }
                        activeRule.countOption[k]++;
                        sumactiveRule.countOption[k]++;
                        break;
                    }
                }
            }
        }
        //Content in file Content
        listContent.clear();
        for (int i = 0; i < listRuleComponent.size(); i++) {
            RuleComponent tar = listRuleComponent.get(i);
            String[] rsplit = Reference.splitByChar(tar.ruleContent.content, ';');
            for (int j = 0; j < rsplit.length; j++) {
                rsplit[j].replaceFirst(" ", "");
                if (rsplit[j].contains("content:") || rsplit[j].contains("uricontent:")) {
                    for (int n = 0; n < rsplit[j].length(); n++) {
                        if (rsplit[j].charAt(n) == '"') {
                            rsplit[j] = rsplit[j].substring(n + 1, rsplit[j].length() - 1);
                            break;
                        }
                    }
                    if (!listContent.contains(rsplit[j])) {
                        listContent.add(rsplit[j]);
                        activeRule.reducedContent++;
                        sumactiveRule.reducedContent++;
                    }
                }
            }
        }
        killRepetitionPCRE(listRuleComponent);
        listSupportedPcre();
        //BufferedWriter res = new BufferedWriter(new FileWriter(outputfolder + arrayRuleFile[i].getName()));
        countrepetition(listRuleComponentsuppportedPcre, activeRule);
        for (int i = 0; i < listRuleComponentNoPcreRep.size(); i++) {
            if (listRuleComponentNoPcreRep.get(i).ruleContent.isExist) {
                sumactiveRule.totalContenthavePCRE++;
                activeRule.totalContenthavePCRE++;
            }
        }
//        for (int j = 0; j < listNoContriantPcre.size(); j++) {
//            res.write("/" + listNoContriantPcre.get(j).rulePcre.pcre + "/"
//                    + listNoContriantPcre.get(j).rulePcre.modify + "\n");
//            //+ "\t" + listNoContriantPcre.get(j).rulePcre.Contriant + "\n");
//        }
//        res.write("\n\n");
//        for (int j = 0; j < listContriantPcre.size(); j++) {
//            res.write("/" + listContriantPcre.get(j).rulePcre.pcre + "/"
//                    + listContriantPcre.get(j).rulePcre.modify
//                    + "\t\t" + listContriantPcre.get(j).rulePcre.Contriant + "\n");
//        }


        sumactiveRule.totalContent = sumactiveRule.countOption[0];
        activeRule.totalContent += sumactiveRule.countOption[0];
        res.write("\n\nSummary:"
                + "\nTong so rule:           \t" + listRuleComponent.size()
                + "\nTong so content:        \t" + sumactiveRule.totalContent
                + "\nSo rule co content      \t" + sumactiveRule.rulehaveOption[0]
                + "\nRule co content & PCRE  \t" + sumactiveRule.totalContenthavePCRE
                + "\nTong so pcre trong file:\t" + listRulehavePcre.size()
                + "\nSo pcre da bo trung lap:\t" + listRuleComponentNoPcreRep.size()
                + "\nSo pcre dc support:     \t" + listRuleComponentsuppportedPcre.size());
//                + "\nSo Contriant:          \t" + (activeRule.cntchar.allrep + activeRule.cntsub.allrep));

        res.write("\n\nKet qua thong ke option: \n");
        for (int k = 0; k < option.length; k++) {
            if (sumactiveRule.countOption[k] == 0) {
                break;
            }
            String temp = option[k];
//            for (int t = option[k].length(); t < 25; t++) {
//                temp = temp + " ";
//            }
            res.write(temp + "\t" + sumactiveRule.ruleSet + "\t" + sumactiveRule.countOption[k] + "\t" + sumactiveRule.rulehaveOption[k] + "\n");
        }

//        res.write("\n Ket qua character repetition la: "
//                + "\n \tSo repetition:\t " + activeRule.cntchar.allrep
//                + "\n \texactly: \t" + activeRule.cntchar.exactlyrep
//                + "\n \tatleast: \t" + activeRule.cntchar.atleastrep
//                + "\n \tbetween: \t" + activeRule.cntchar.betweenrep
//                + "\n\n \tExactly list:\n");
//        for (int k = 0; k < 3000; k++) {
//            if (activeRule.cntchar.exactlylist[k] > 0) {
//                //System.out.println(i + "\t" + exactlylist[i]);
//                res.write(k + "\t\t" + activeRule.cntchar.exactlylist[k] + "\n");
//            }
//        }
//        res.write("\n\n \tAtleast list:\n");
//        for (int p = 0; p < 3000; p++) {
//            if (activeRule.cntchar.atleastlist[p] > 0) {
//                //System.out.println(i + "\t" + atleastlist[i]);
//                res.write(p + "\t\t" + activeRule.cntchar.atleastlist[p] + "\n");
//            }
//        }
//
//        // Subregex repetition
//        res.write("\n Ket qua subregex repetition la: "
//                + "\n \tSo repetition: \t" + activeRule.cntsub.allrep
//                + "\n \texactly: \t" + activeRule.cntsub.exactlyrep
//                + "\n \tatleast: \t" + activeRule.cntsub.atleastrep
//                + "\n \tbetween: \t" + activeRule.cntsub.betweenrep);
//        res.write("\n\n \tExactly list:\n");
//        for (int g = 0; g < 3000; g++) {
//            if (activeRule.cntsub.exactlylist[g] > 0) {
//                //System.out.println(i + "\t" + exactlylist[i]);
//                res.write(g + "\t\t" + activeRule.cntsub.exactlylist[g] + "\n");
//            }
//        }
//        res.write("\n\n \tAtleast list:\n");
//        for (int h = 0; h < 3000; h++) {
//            if (activeRule.cntsub.atleastlist[h] > 0) {
//                //System.out.println(i + "\t" + atleastlist[i]);
//                res.write(h + "\t\t" + activeRule.cntsub.atleastlist[h] + "\n");
//            }
//        }
//        totalOpCont = 0;
//        totalContent = 0;
//        totalContentPCRE = 0;
        sumactiveRule.totalPcre = listRulehavePcre.size();
        sumactiveRule.totalPcreNoRep = listRuleComponentNoPcreRep.size();
        sumactiveRule.totalPcresupported = listRuleComponentsuppportedPcre.size();
        sumactiveRule.totalRule = listRuleComponent.size();
        activeRule.totalRule += listRuleComponent.size();
        activeRule.totalPcre += listRulehavePcre.size();
        activeRule.totalPcreNoRep += listRuleComponentNoPcreRep.size();
        activeRule.totalPcresupported += listRuleComponentsuppportedPcre.size();
        activeRule.totalchar = activeRule.cntchar.add(activeRule.totalchar);
        activeRule.totalsub = activeRule.cntsub.add(activeRule.totalsub);
        activeRule.cntchar.clear();
        activeRule.cntsub.clear();
        listRulehavePcre.clear();
        listRuleComponent.clear();
        listRuleComponentNoPcreRep.clear();
        listRuleComponentsuppportedPcre.clear();
        listContriantPcre.clear();
        listNoContriantPcre.clear();
        //System.out.println("total : " + activeRule.rulehaveOption[0]);
    }

    public void parseRuleFolder2() throws IOException {
        //LinkedList<Summary> allrule = new LinkedList<Summary>();
        this.listallRule = new LinkedList<Summary>();
        try {
            arrayRuleFile = getRuleFiles(rulefolder);
            for (int i = 0; i < arrayRuleFile.length; i++) {
                parseRuleFile(arrayRuleFile[i]);
                BufferedWriter res = new BufferedWriter(new FileWriter(outputfolder + arrayRuleFile[i].getName()));
                Summary sumactive = new Summary();
                sumactive.ruleSet = arrayRuleFile[i].getName();
                res.write("\t\tACTIVE RULE\n\n");
                
                WriteList(listRuleComponent, activeRule, sumactive, res);
                //System.out.println("Activerule:" + activeRule.rulehaveOption[0] + "\t"
                  //      + sumactive.rulehaveOption[0] + "\t" + tem);
                listactiveRule.add(sumactive);
                res.write("\n\n\t\tINACTIVE RULE\n\n");
                Summary suminactive = new Summary();
                suminactive.ruleSet = arrayRuleFile[i].getName();

                Summary all = new Summary();
                all.ruleSet = suminactive.ruleSet;

                int tem  = listInactiveRule.size();
                WriteList(listInactiveRule, inactiveRule, suminactive, res);
                if (tem >1000){
                    System.out.println(suminactive.ruleSet);
                }
                System.out.println("Activerule:" + inactiveRule.rulehaveOption[0] + "\t"
                        + suminactive.rulehaveOption[0] + "\t" + tem);
                listinactiveRule.add(suminactive);

                for(int k =0; k<suminactive.rulehaveOption.length; k++){
                    all.rulehaveOption[k] = suminactive.rulehaveOption[k] + sumactive.rulehaveOption[k];
                    all.countOption[k] = suminactive.countOption[k] + sumactive.countOption[k];
                }
                this.listallRule.add(all);

                res.flush();
                res.close();
            }

            this.allRule = new Summary();
            for(int k =0; k < this.allRule.countOption.length; k++){
                for(int t = 0; t < this.listallRule.size(); t++){
                    this.allRule.rulehaveOption[k] += this.listallRule.get(t).rulehaveOption[k];
                    this.allRule.countOption[k] += this.listallRule.get(t).countOption[k];
                }
                
            }

            BufferedWriter res = new BufferedWriter(new FileWriter(outputfolder + "summary"));
            BufferedWriter res1 = new BufferedWriter(new FileWriter(outputfolder + "summary1"));
            res.write("\t\tACTIVE RULE\n\n");
            activeRule.ruleSet = "Active Rule";
            listactiveRule.add(activeRule);
            writeSummary(listactiveRule, res);
            res.write("\n\n\t\tINACTIVE RULE\n\n");
            inactiveRule.ruleSet = "Inactive Rule";
            listinactiveRule.add(inactiveRule);
            writeSummary(listinactiveRule, res);
            writeSummary1(res1);
            res1.flush();
            res1.close();
            res.flush();
            res.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

        public void writeSummary(LinkedList<Summary> listactiveRule, BufferedWriter res) {
        try {
            res.write("\t\tRULE:\n");
            for (int i = 0; i < listactiveRule.size() - 1; i++) {
                Summary tmp = listactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + tmp.totalRule + "\t" + listinactiveRule.get(i).totalRule + "\t"
                        + (tmp.totalRule + listinactiveRule.get(i).totalRule) + "\n");
            }
            res.write("\n\t\tCONTENT\n");
            for (int i = 0; i < listactiveRule.size(); i++) {
                Summary tmp = listactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + tmp.totalContent + "\t" + tmp.reducedContent
                        + "\t" + tmp.rulehaveOption[0] + "\t" + tmp.totalContenthavePCRE + "\n");
            }
            res.write("\n\t\tPCRE\n");
            for (int i = 0; i < listactiveRule.size(); i++) {
                Summary tmp = listactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + tmp.totalPcre + "\t" + tmp.totalPcreNoRep + "\t" + tmp.totalPcresupported + "\n");
            }
            Summary tmp = listactiveRule.getLast();
            res.write("\t\tRULE:\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalRule + "\t" + listinactiveRule.getLast().totalRule + "\t"
                        + (tmp.totalRule + listinactiveRule.getLast().totalRule) + "\n");
            res.write("\t\tCONTENT\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalContent + "\t" + tmp.reducedContent + "\t" + tmp.rulehaveOption[0] + "\t" + tmp.totalContenthavePCRE);
            res.write("\n\t\tPCRE\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalPcre + "\t" + tmp.totalPcreNoRep + "\t" + tmp.totalPcresupported + "\n");
            res.write("\n\n\nKet qua thong ke option: \n");
            for (int k = 0; k < option.length; k++) {
                //if (tmp.countOption[k] == 0) {
                  //  break;
                //}
                String temp = option[k];
                res.write(temp + "\t" + tmp.ruleSet + "\t" + tmp.countOption[k] + "\t" + tmp.rulehaveOption[k] + 
                        "\t" + this.allRule.countOption[k] + "\t" + this.allRule.rulehaveOption[k] + "\n");
            }
            
           
            //BufferedWriter res = new BufferedWriter(new FileWriter(outputfolder + "summary"));
//            res.write("\tSummary"
//                    + "\nTong so rule:           \t" + activeRule.totalRule
//                    + "\nSo rule co content:     \t" + activeRule.totalContent
//                    //+ "\nSo rule co tu khoa content\t" + activeRule
//                    + "\nSo rule co ca content va PCRE: " + activeRule.totalContenthavePCRE
//                    + "\nSo pcre:                \t" + activeRule.totalPcre
//                    + "\nSo pcre da bo trung lap:\t" + activeRule.totalPcreNoRep
//                    + "\nSo pcre supported:      \t" + activeRule.totalPcresupported);

//            res.write("\t\tSUMMARY\n\n\tRULE\n");
//            res.write(activeRule.ruleSet + "\t" + activeRule.totalRule + "\n");
//            res.write("\n\nKet qua thong ke option: \n");
//            for (int k = 0; k < option.length; k++) {
//                String temp = option[k];
//                for (int t = option[k].length(); t < 25; t++) {
//                    temp = temp + " ";
//                }
//                res.write("\t" + temp + activeRule.countOption[k] + "\n");
//                activeRule.countOption[k] = 0;
//            }

//            res.write("\nTong so repetiton:      \t" + (activeRule.totalchar.allrep + activeRule.totalsub.allrep)
//                    + "\n \texactly: \t" + (activeRule.totalchar.exactlyrep + activeRule.totalsub.exactlyrep)
//                    + "\n \tatleast: \t" + (activeRule.totalchar.atleastrep + activeRule.totalsub.atleastrep)
//                    + "\n \tbetween: \t" + (activeRule.totalchar.betweenrep + activeRule.totalsub.betweenrep));
//
//            res.write("\n Ket qua character repetition la: "
//                    + "\n \tSo repetition:\t " + activeRule.totalchar.allrep
//                    + "\n \texactly: \t" + activeRule.totalchar.exactlyrep
//                    + "\n \tatleast: \t" + activeRule.totalchar.atleastrep
//                    + "\n \tbetween: \t" + activeRule.totalchar.betweenrep
//                    + "\n\n \tExactly list:\n");
//            for (int k = 0; k < 3000; k++) {
//                if (activeRule.totalchar.exactlylist[k] > 0) {
//                    //System.out.println(i + "\t" + exactlylist[i]);
//                    res.write(k + "\t\t" + activeRule.totalchar.exactlylist[k] + "\n");
//                }
//            }
//            res.write("\n\n \tAtleast list:\n");
//            for (int p = 0; p < 3000; p++) {
//                if (activeRule.totalchar.atleastlist[p] > 0) {
//                    //System.out.println(i + "\t" + atleastlist[i]);
//                    res.write(p + "\t\t" + activeRule.totalchar.atleastlist[p] + "\n");
//                }
//            }
//
//            // Subregex repetition
//            res.write("\n Ket qua subregex repetition la: "
//                    + "\n \tSo repetition: \t" + activeRule.totalsub.allrep
//                    + "\n \texactly: \t" + activeRule.totalsub.exactlyrep
//                    + "\n \tatleast: \t" + activeRule.totalsub.atleastrep
//                    + "\n \tbetween: \t" + activeRule.totalsub.betweenrep);
//            res.write("\n\n \tExactly list:\n");
//            for (int g = 0; g < 3000; g++) {
//                if (activeRule.totalsub.exactlylist[g] > 0) {
//                    //System.out.println(i + "\t" + exactlylist[i]);
//                    res.write(g + "\t\t" + activeRule.totalsub.exactlylist[g] + "\n");
//                }
//            }
//            res.write("\n\n \tAtleast list:\n");
//            for (int h = 0; h < 3000; h++) {
//                if (activeRule.totalsub.atleastlist[h] > 0) {
//                    //System.out.println(i + "\t" + atleastlist[i]);
//                    res.write(h + "\t\t" + activeRule.totalsub.atleastlist[h] + "\n");
//                }
//            }
        } catch (IOException ex) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void writeSummary1(BufferedWriter res) {
        try {
            res.write("\t\tRULE:\n");
            for (int i = 0; i < listactiveRule.size() - 1; i++) {
                Summary tmp = listactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + tmp.totalRule + "\t" + listinactiveRule.get(i).totalRule + "\t"
                        + (tmp.totalRule + listinactiveRule.get(i).totalRule) + "\n");
            }
            res.write("\n\t\tCONTENT\n");
            for (int i = 0; i < listactiveRule.size(); i++) {
                Summary tmp = listactiveRule.get(i);
                Summary tmp1 = listinactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + (tmp.totalContent + tmp1.totalContent) + "\t" + (tmp.reducedContent + tmp1.reducedContent)
                        + "\t" + (tmp.rulehaveOption[0] + tmp1.rulehaveOption[0]) + "\t" + (tmp.totalContenthavePCRE + tmp1.totalContenthavePCRE) + "\n");
            }
            res.write("\n\t\tPCRE\n");
            for (int i = 0; i < listactiveRule.size(); i++) {
                Summary tmp = listactiveRule.get(i);
                res.write(tmp.ruleSet + "\t" + tmp.totalPcre + "\t" + tmp.totalPcreNoRep + "\t" + tmp.totalPcresupported + "\n");
            }
            Summary tmp = listactiveRule.getLast();
            res.write("\t\tRULE:\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalRule + "\t" + listinactiveRule.getLast().totalRule + "\t"
                        + (tmp.totalRule + listinactiveRule.getLast().totalRule) + "\n");
            res.write("\t\tCONTENT\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalContent + "\t" + tmp.reducedContent + "\t" + tmp.rulehaveOption[0] + "\t" + tmp.totalContenthavePCRE);
            res.write("\n\t\tPCRE\n");
            res.write(tmp.ruleSet + "\t" + tmp.totalPcre + "\t" + tmp.totalPcreNoRep + "\t" + tmp.totalPcresupported + "\n");
            res.write("\n\n\nKet qua thong ke option: \n");
            for (int k = 0; k < option.length; k++) {
                String temp = option[k];
                res.write(temp + "\t" + tmp.ruleSet + "\t" + tmp.countOption[k] + "\t" + tmp.rulehaveOption[k] +
                        "\t" + this.allRule.countOption[k] + "\t" + this.allRule.rulehaveOption[k] + "\n");
            }

        } catch (IOException ex) {
            Logger.getLogger(RuleDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void outputRuleComponent() {

        try {
            System.out.println("Processing write pcre Component...");
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcrenorep));
            LinkedList<String> rulesets = new LinkedList<String>();
            for (int i = 0; i < arrayRuleFile.length; i++) {
                rulesets.add(arrayRuleFile[i].getName());
            }

            int[] countpcre = new int[arrayRuleFile.length]; //Dem so pcre trong ruleset
            for (int i = 0; i < listRuleComponentNoPcreRep.size(); i++) {
                RuleComponent temp = listRuleComponentNoPcreRep.get(i);
                //tang so pcre trong mot ruleset len mot
                countpcre[rulesets.indexOf(temp.ruleSet)]++;
                //Viet pcre ra file
                bw.write(temp.rulePcre.toString() + "\n");

            }
            //Ghi ket qua thogn ke
            bw.write("#result: \n\n");
            int totalpcre = 0;
            for (int i = 0; i < rulesets.size(); i++) {
                bw.write("#" + rulesets.get(i) + "\t" + countpcre[i] + "\n");
                totalpcre += countpcre[i];
            }
            //Write total
            bw.write("#ToTal" + totalpcre);

            bw.flush();
            bw.close();
            System.out.println("Write xong pcre to : " + pcrenorep);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public LinkedList<PacketHeader> getHeader() {
        LinkedList<PacketHeader> pheader = new LinkedList<PacketHeader>();
        System.out.println("Begin get Header: " + listRuleComponent.size());
        //Lay danh sach ca header
        for (int i = 0; i < listRuleComponent.size(); i++) {
            RuleComponent temp = listRuleComponent.get(i);
            boolean same = false;
            //System.out.println(i);
            for (int j = 0; j < pheader.size(); j++) {
                if (pheader.get(j).isEqual(temp.ruleHeader)) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                pheader.add(temp.ruleHeader);
            }
        }
        return pheader;
    }

    public void outputHeader() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(headeroutput));
            LinkedList<PacketHeader> pheader = getHeader();
            //Ghi ra output
            if (pheader.size() != 0) {
                for (int i = 0; i < pheader.size(); i++) {
                    bw.write(pheader.get(i).toString() + "\n");
                }
                bw.write("# Resule: " + pheader.size() + " Number of rule is: " + listRuleComponent.size());

            }
            System.out.println("Get xong header, output is " + outputfolder);
            bw.flush();
            bw.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public LinkedList<RuleComponent> reduceSamePcre() {
        LinkedList<RuleComponent> ret = new LinkedList<RuleComponent>();
        boolean same;
        for (int i = 0; i < listRuleComponentNoPcreRep.size(); i++) {
            same = false;
            for (int j = 0; j < ret.size(); j++) {
                //if have the same more than 40 not add to retrun
                if (Reference.compareByPercent(listRuleComponentNoPcreRep.get(i).rulePcre.pcre, ret.get(j).rulePcre.pcre, 40)) {
                    same = true;
                    break;
                }

            }
            if (!same) {
                ret.add(listRuleComponentNoPcreRep.get(i));
            }
        }

        return ret;
    }

    public void writePCRE() throws Exception {
        BufferedWriter pcre = new BufferedWriter(new FileWriter("listPCRE"));
        for (int i = 0; i < listRuleComponentsuppportedPcre.size(); i++) {
            pcre.write(listRuleComponentsuppportedPcre.get(i).rulePcre.toString() + "\n");
        }
        pcre.flush();
        pcre.close();
    }

    public LinkedList<RuleComponent> filterofSid(String sid, LinkedList<RuleComponent> result) {
        //if (!sid.equalsIgnoreCase("")) {
        //LinkedList<RuleComponent> result = new LinkedList<RuleComponent>();
        boolean same;
        for (int i = 0; i < listRuleComponent.size(); i++) {
            same = false;
            //System.out.println(sid);
            if (sid != null && listRuleComponent != null && listRuleComponent.get(i) != null && listRuleComponent.get(i).rulePcre != null && listRuleComponent.get(i).rulePcre.sid != null && sid.contentEquals(listRuleComponent.get(i).rulePcre.sid)) {
                same = true;
                result.add(listRuleComponent.get(i));
            }
        }
        return result;
        //} else {
        // return null;
        //}
    }

    public LinkedList<RuleComponent> filterofArraySid(String[] sid, LinkedList<RuleComponent> result) {
        //System.out.println(sid.length);
        for (int i = 0; i < sid.length; i++) {
            filterofSid(sid[i], result);
        }
        return result;
    }

    public void parseSid(File rfile, String[] result) {
        //result = new String;
        int index = 0;
        String s = "";
        //String temp = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(rfile));
            System.out.println("parse sid file: " + rfile.getName());

            while ((s = br.readLine()) != null) {
                //System.out.println(s);
                //temp = temp + s;
                String[] res = Reference.splitByChar(s, ';');
                System.out.println(res.length);
                for (int i = 0; i < res.length; i++) {
                    //System.out.println(res[i]);
                    if (res[i].startsWith("sid")) {
                        //System.out.println(res[i]);
                        //System.out.println(result);
                        result[index] = res[i].replaceFirst("sid:", "");
                        //System.out.println(result[index]);
                        index++;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException t1) {
            System.err.println(t1);
        } catch (IOException t2) {
            System.err.println(t2);
        }
        //return result;
    }

    public void outputSimplyPcre() {
        try {
            System.out.println("Processing output simply pcre....");
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcresimply));
            int count = 0;
            LinkedList<RuleComponent> reduce = reduceSamePcre();
            for (int i = 0; i < reduce.size(); i++) {
                RuleComponent temp = reduce.get(i);

                if (!Reference.isSimplyPcre(temp.rulePcre)) {
                    continue;
                }

                // gio ghi ra
                bw.write(temp.rulePcre.toString() + "\n");
                count++;
            }
            bw.write("#result: " + count);
            bw.flush();
            bw.close();
            System.out.println("complete, output to : " + pcresimply);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void outputFilterSid() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filteroutput));
            BufferedWriter hw = new BufferedWriter(new FileWriter(outputfolder + "header.mip"));
            BufferedWriter pw = new BufferedWriter(new FileWriter(outputfolder + "pcre.mip"));
            String[] sid = new String[100];
            File read = new File(rulefolder + "choose.mip");
            parseSid(read, sid);
            LinkedList<RuleComponent> result = new LinkedList<RuleComponent>();
            System.out.println(sid.length);
            filterofArraySid(sid, result);
            //Ghi ra output
            if (result.size() != 0) {
                for (int i = 0; i < result.size(); i++) {
                    //System.out.println(result.get(i).rulePcre.pcre);
                    //System.out.println(result.get(i).ruleHeader);
                    if (!result.get(i).ruleHeader.same) {
                        for (int j = i + 1; j < result.size(); j++) {
                            if (result.get(i).ruleHeader.getString().contentEquals(result.get(j).ruleHeader.getString())) {
                                result.get(i).ruleHeader.same = result.get(j).ruleHeader.same = true;
                            }
                        }
                    }
                    pw.write(result.get(i).rulePcre.pcre + "\n");
                    if (result.get(i).ruleHeader.same) {
                        hw.write(stringToPrint(result.get(i).ruleHeader.getString1(), "#") + "\n");
                    } else {
                        hw.write(result.get(i).ruleHeader.getString1() + "\n");
                    }
                    bw.write(result.get(i).toString() + "\n");
                }
                bw.write("# Resule: " + result.size() + " Number of rule is: " + listRuleComponent.size());

            }
            System.out.println("Output is " + outputfolder);
            pw.flush();
            pw.close();
            hw.flush();
            hw.close();
            bw.flush();
            bw.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String stringToPrint(String msg, String regex) {
        String result = regex + msg;
        return result;
    }

    public String stringToPrint(String msg, String regex, boolean prefix) {
        if (prefix) {
            return regex + msg;
        } else {
            return msg + regex;
        }
        //return result;
    }

    void outputKind1() {
        File temp;
        for(int i = 0; i<this.arrayRuleFile.length; i++){
            temp = this.arrayRuleFile[i];
            System.out.print(temp.getName().replaceAll(".rules", "") + ", ");
        }
    }
}
