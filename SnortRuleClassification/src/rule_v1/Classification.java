/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rule_v1;

import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author Admin
 */
public class Classification {

    public LinkedList<PacketHeader> listHeader;
    public LinkedList<RulePcre> listPcre;
    public LinkedList<String> listContent;
    public LinkedList<String> listStOption;
    public LinkedList<RuleComponent> listRuleComponent;
    public LinkedList<RuleComponent> listInactiveRule;
    public LinkedList<String> listOption;
    public Hashtable<String, Integer> hHeader;
    public Hashtable<String, Integer> hOption;
    String currentDir = System.getProperty("user.dir");
    String ruleFolder = currentDir + "\\rule_1\\";
    String outputFolder = currentDir + "\\Result\\";
    String oOption = outputFolder + "Option";
    String oHeader = outputFolder + "Header";
    String oRule = outputFolder + "Rule";
    String oID = outputFolder + "ID";

    Classification() throws IOException {
        this.listContent = new LinkedList<String>();
        this.listHeader = new LinkedList<PacketHeader>();
        this.listPcre = new LinkedList<RulePcre>();
        this.listOption = new LinkedList<String>();
        this.hHeader = new Hashtable<String, Integer>();
        this.hOption = new Hashtable<String, Integer>();
        this.Build();
    }

    Classification(String ruleFolder) throws IOException {
        this.listContent = new LinkedList<String>();
        this.listHeader = new LinkedList<PacketHeader>();
        this.listPcre = new LinkedList<RulePcre>();
        this.listOption = new LinkedList<String>();
        this.hHeader = new Hashtable<String, Integer>();
        this.hOption = new Hashtable<String, Integer>();
        this.ruleFolder = ruleFolder;
        this.Build();
    }

    public void Build() throws IOException {
        RuleDatabase data = new RuleDatabase(this.ruleFolder);
        data.builDatabase();
        this.listRuleComponent = data.listRuleComponent;
        this.listInactiveRule = data.listInactiveRule;
    }

    public void BuildAll() {
        for (int i = 0; i < this.listRuleComponent.size(); i++) {
            RuleComponent tar = this.listRuleComponent.get(i);
            String[] rsplit = tar.ruleContent.content.split("content:");
            for (int j = 0; j < rsplit.length; j++) {
                rsplit[j].replaceFirst(" ", "");
                if ("".compareTo(rsplit[j]) != 0) {
                    if (j != 0 && !rsplit[j - 1].endsWith("; ") && rsplit[j - 1].endsWith("uri")) {
                        rsplit[j - 1].substring(0, rsplit[j - 1].length() - 3);
                        rsplit[j] = "uricontent:" + rsplit[j];
                    } else {
                        rsplit[j] = "content:" + rsplit[j];
                    }
                }
                if (!Contain(rsplit[j]) && "".compareTo(rsplit[j]) != 0) {
                    this.listContent.add(rsplit[j]);
                    //System.out.println(this.listContent.get(j));
                }
            }

            if (!Contain(tar.ruleHeader)) {
                this.listHeader.add(tar.ruleHeader);
            }
            if (!Contain(tar.rulePcre) && "".compareTo(tar.rulePcre.toString()) != 0) {
                this.listPcre.add(tar.rulePcre);
            }
        }
    }

    public boolean Contain(PacketHeader header) {
        for (int i = 0; i < this.listHeader.size(); i++) {
            if (header.toString_2().compareTo(this.listHeader.get(i).toString_2()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean Contain(RulePcre pcre) {
        for (int i = 0; i < this.listPcre.size(); i++) {
            if (pcre.toString().compareTo(this.listPcre.get(i).toString()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean Contain(String content) {
        for (int i = 0; i < this.listContent.size(); i++) {
            if (content.compareTo(this.listContent.get(i)) == 0) {
                return true;
            }
        }
        return false;
    }

    public String[] parseContent(String content) {
        String result[] = new String[100];
        String temp = "";
        char c;
        for (int i = 0; i < content.length(); i++) {
            c = content.charAt(i);
        }
        return result;
    }

    public void Print() {
        for (int i = 0; i < this.listContent.size(); i++) {
            System.out.println(this.listContent.get(i));
        }
    }

    public void WriteFile() throws IOException {
//        String oContent = outputFolder + "Content.all";
//        String osCont = outputFolder + "Content.sum";
        BufferedWriter ow = new BufferedWriter(new FileWriter(oOption));
        BufferedWriter hw = new BufferedWriter(new FileWriter(oHeader));
        BufferedWriter rw = new BufferedWriter(new FileWriter(oRule));
        BufferedWriter iw = new BufferedWriter(new FileWriter(oID));
//        BufferedWriter oc = new BufferedWriter(new FileWriter(oContent));
//        BufferedWriter osc = new BufferedWriter(new FileWriter(osCont));

        //Content in file Option
        for (int i = 0; i < this.listContent.size(); i++) {
            ow.write((i + 1) + ": " + this.listContent.get(i) + "\n");
            this.hOption.put(this.listContent.get(i), i + 1);
        }

        //Header
        for (int i = 0; i < this.listHeader.size(); i++) {
            hw.write((i + 1) + ": " + this.listHeader.get(i).toString_2() + "\n");
            this.hHeader.put(this.listHeader.get(i).toString_2(), i + 1);
        }

        //Pcre
        for (int i = 0; i < this.listPcre.size(); i++) {
            ow.write((i + this.listContent.size() + 1) + ": " + this.listPcre.get(i).toString() + "\n");
            this.hOption.put(this.listPcre.get(i).toString(), (i + this.listContent.size() + 1));
        }

        //Rule        
        for (int i = 0; i < this.listRuleComponent.size(); i++) {
            RuleComponent tar = this.listRuleComponent.get(i);
            rw.write((i + 1) + ": " + tar.toString() + "\n");
            iw.write((i + 1) + "; " + tar.ruleHeader.msg + "; " + hHeader.get(tar.ruleHeader.toString_2()) + "; ");
//            String[] rsplit = Reference.splitByChar(tar.ruleContent.content, ';');
//            for (int j = 0; j < rsplit.length; j++) {
//                rsplit[j].replaceFirst(" ", "");
//                iw.write(hOption.get(rsplit[j]) + " ");
//            }
            listContent.add(tar.ruleContent.content);
            String[] rsplit = tar.ruleContent.content.split("content:");
            for (int j = 0; j < rsplit.length; j++) {
                rsplit[j].replaceFirst(" ", "");
                if ("".compareTo(rsplit[j]) != 0) {
                    if (j != 0 && !rsplit[j - 1].endsWith("; ") && rsplit[j - 1].endsWith("uri")) {
                        rsplit[j - 1].substring(0, rsplit[j - 1].length() - 3);
                        rsplit[j] = "uricontent:" + rsplit[j];
                    } else {
                        rsplit[j] = "content:" + rsplit[j];
                    }
                    iw.write(hOption.get(rsplit[j]) + " ");
                } else {
                    iw.write(" ");
                }
            }
            //iw.write(hOption.get(tar.ruleContent.content) + "; ");
            //iw.write(hOption.get(this.listContent.get((i + 5)%this.listContent.size())) + "; ");
            iw.write("; ");
            if ("".compareTo(tar.rulePcre.toString()) != 0) {
                iw.write(hOption.get(tar.rulePcre.toString()) + "\n");
            } else {
                iw.write("\n");
            }
        }

        rw.write("\n\t\tINACTIVE RULE\n\n");
        for (int i = 0; i < listInactiveRule.size(); i++) {
            RuleComponent tar = this.listInactiveRule.get(i);
            rw.write((i + 1) + ": " + tar.toString() + "\n");
        }

        iw.flush();
        iw.close();
        ow.flush();
        ow.close();
        hw.flush();
        hw.close();
        rw.flush();
        rw.close();
    }

    public void WriteAllContent() throws IOException {
        WriteContentinList(listInactiveRule, "inactive");
        WriteContentinList(listRuleComponent, "active");
    }

    public void WriteContentinList(LinkedList<RuleComponent> listRule, String option) throws IOException {
        String oContent = outputFolder + "Content." + option + ".all";
        String osCont = outputFolder + "Content." + option + ".sum";
        //System.out.println(listRule.toString());
        BufferedWriter oc = new BufferedWriter(new FileWriter(oContent));
        BufferedWriter osc = new BufferedWriter(new FileWriter(osCont));

        //Content in file Content
        listContent.clear();
        for (int i = 0; i < listRule.size(); i++) {
            RuleComponent tar = listRule.get(i);
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
                    }
                }
            }
        }

        //Begin to write
        oc.write("Total content in " + option + " rule: " + listContent.size() + "\n\n");
        int prelen = 0;
        int count = 0;
        for (int i = 0; i < this.listContent.size(); i++) {
            String content = this.listContent.get(i);
            int index = i;
            for (int j = i + 1; j < this.listContent.size(); j++) {
                if (content.length() < this.listContent.get(j).length()) {
                    content = this.listContent.get(j);
                    index = j;
                }
            }
            if (index != i) {
                String tmp = listContent.get(i);
                listContent.set(i, content);
                listContent.set(index, tmp);
            }
            if (prelen == 0 || prelen == listContent.get(i).length()) {
                count++;
                oc.write("\t" + this.listContent.get(i) + "\n");
                prelen = listContent.get(i).length();
            } else {
                osc.write(prelen + "\t" + count + "\n");
                oc.write("Length: " + prelen + "\t\tTotal: " + count + "\n\n");
                oc.write("\n\t" + this.listContent.get(i) + "\n");
                prelen = listContent.get(i).length();
                count = 1;
            }
        }

        osc.write(prelen + "\t" + count + "\n");
        oc.write("Length: " + prelen + "\t\tTotal: " + count + "\n");
        osc.flush();
        osc.close();
        oc.flush();
        oc.close();
    }
}
