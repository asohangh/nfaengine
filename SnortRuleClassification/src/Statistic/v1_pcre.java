/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Statistic;

import java.io.File;
import java.lang.ref.Reference;
import java.util.LinkedList;
import snort_rule.OptionMask;
import snort_rule.PCRE;
import snort_rule.References;
import snort_rule.RuleDatabase;

/**
 *
 * @author heckarim
 */
public class v1_pcre {

    private final String outputfolder = System.getProperty("user.dir") + File.separatorChar + "output.2.9" + File.separatorChar + "stats" + File.separatorChar;
    private RuleDatabase db;
    private String outputfile;
    private String rules;
    LinkedList<PCRE> lspcre;

    v1_pcre(RuleDatabase db, String file) {
        this.db = db;
        this.outputfile = file;
    }

    public static void main(String args[]) {
        v1_pcre pcre = new v1_pcre(null, null);
        pcre.initialize();

        pcre.action();
    }

    private void initialize() {
        String rulefolder = System.getProperty("user.dir") + File.separatorChar + "rules.2.9" + File.separatorChar;
        //db = new RuleDatabase(rulefolder);
        //db.BuildDatabase();
        //validate output folder
        this.validatefolder(this.outputfolder);
        //exatract pcre list
        this.lspcre = References.ReadPcreFromFile(outputfolder + "allpcre.pcre");
    }

    void action() {
        // Statistic on modifier:
        this.statisticmodifier();
        //statistic operator;
        this.statisticoperator();

    }

    private void validatefolder(String outputfolder) {
        File out = new File(outputfolder);
        if (out.mkdirs()) {
            System.out.println("Create folder ok");
        } else {
            System.out.println("Error on creating folder");
        }
    }

    private void statisticmodifier() {
        //get all modifier
        LinkedList<String> lstring = new LinkedList<String>();
        for (int i = 0; i < this.lspcre.size(); i++) {
            lstring.add(lspcre.get(i).modify);
        }
        //arraycount
        int[] count = new int[snort_rule.References._pcreModifier.length];
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }
        //count
        for (int i = 0; i < lstring.size(); i++) {
            String mod = lstring.get(i);
            for (int j = 0; j < count.length; j++) {
                if (mod.contains(References._pcreModifier[j])) {
                    count[j]++;
                }
            }
        }
        //
        for (int j = 0; j < count.length; j++) {
            String s = References._pcreModifier[j];
            System.out.println(s + " : " + count[j]);
        }
        //TODO
        //out to files
        
    }

    private LinkedList<PCRE> getAllPCRE() {
        OptionMask mask = new OptionMask();
        mask.SetPermit("pcre");

        LinkedList<PCRE> rPcre = new LinkedList<PCRE>();
        for (int j = 0; j < db.lstRuleAll.size(); j++) {
            if (db.lstRuleAll.get(j).ApplyMask(mask)) {
                //bw.write(rs.lstRuleAll.get(j).value + "\n");
                LinkedList<PCRE> tPcre = db.lstRuleAll.get(j).getOpPcre();
                rPcre.addAll(tPcre);
            }
        }
        return rPcre;
    }

    public LinkedList<PCRE> reducePCRE(){
        LinkedList<PCRE> rPcre = new LinkedList<PCRE>();
        for(int i =0; i<this.lspcre.size();i++){
            PCRE pcre = lspcre.get(i);
            boolean exist = false;
            for(int j=0;j<rPcre.size();j++){
                PCRE com = rPcre.get(j);
                if(pcre.CompareModifier(com) && pcre.CompareTo(com)){
                    exist = true;
                    break;
                }
            }
            if(!exist)
                rPcre.add(pcre);
        }
        References.WritePcreToFile(rPcre, this.outputfolder+"reduce.pcre");
        return rPcre;
    }

    private void statisticoperator() {
        //backreference
        this.statsBackreference();
    }

    private void statsBackreference() {
        LinkedList<String> lbr = this.getBackreference("abc");
    }

    private LinkedList<String> getBackreference(String string) {
        LinkedList<String> ls = new LinkedList<String>();


        return ls;
    }

}
