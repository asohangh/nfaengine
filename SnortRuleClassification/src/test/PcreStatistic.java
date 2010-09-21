/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import snort_rule.*;
import snort_rule.PCRE;

/**
 *
 * @author heckarim
 *
 */
public class PcreStatistic {

    LinkedList<PCRE> lpcre;
    String outputfolder;

    public static void main(String[] args) {
        new PcreStatistic().action();
    }

    public void action() {

        //Read pcre from output file.
        outputfolder = System.getProperty("user.dir") + File.separator + "output" + File.separator;
        lpcre = snort_rule.References.ReadPcreFromFile(outputfolder + "reduce.pcre.ref");


        //=================================================
        // count modifier.
        //this.doCountModifier();
        //=================================================
        // Reduce pcre
        // this.doReducePcre();
        //=================================================
        //prefix partition
        //this.doCheckPrefix();
        //this.doGetPrefix();
        this.doPrefixPartition();

    }

    /**
     *
     */
    public void doReducePcre() {
        LinkedList<PCRE> rpcre = new LinkedList<PCRE>();
        for (int i = 0; i < lpcre.size(); i++) {
            boolean same = false;
            PCRE temp = lpcre.get(i);
            for (int j = 0; j < rpcre.size(); j++) {
                if (temp.CompareTo(rpcre.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                rpcre.add(temp);
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "reduce.pcre.ref"));
            //write each rule

            for (int i = 0; i < rpcre.size(); i++) {
                PCRE pcre = rpcre.get(i);
                bw.write(pcre.toString() + "\n");

            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void doCountModifier() {
        int[] modifier = new int[snort_rule.References._pcreModifier.length];
        LinkedList<LinkedList<PCRE>> mlpcre = new LinkedList<LinkedList<PCRE>>();//user for store linkedlist of pcre belong to dedicated modifier
        for (int i = 0; i < modifier.length; i++) {
            LinkedList<PCRE> ltemp = new LinkedList<PCRE>();
            mlpcre.add(ltemp);
        }

        for (int i = 0; i < lpcre.size(); i++) {
            PCRE pcre = lpcre.get(i);
            for (int j = 0; j < modifier.length; j++) {
                if (pcre.modify.contains(snort_rule.References._pcreModifier[j])) {
                    modifier[j]++;
                    mlpcre.get(j).add(pcre);
                }
            }
        }
        // write to output
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "modifier.reduce.pcre.ref"));
            //write each rule
            for (int j = 0; j < modifier.length; j++) {
                bw.write("\n\n#Modifier: " + snort_rule.References._pcreModifier[j] + "\n\n");
                LinkedList<PCRE> ltemp = mlpcre.get(j);
                for (int i = 0; i < ltemp.size(); i++) {
                    bw.write(ltemp.get(i).toString() + "\n");
                }
            }
            bw.write("\n\n# Result: \n");
            for (int j = 0; j < modifier.length; j++) {
                bw.write(snort_rule.References._pcreModifier[j] + "\t " + modifier[j] + "\n");
                System.out.println(snort_rule.References._pcreModifier[j] + ": " + modifier[j]);
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doCheckPrefix() {
        int[] same = new int[400];
        for (int i = 0; i < lpcre.size(); i++) {
            int t = lpcre.size() - i;
            System.out.println(t);
            int max = 0;

            for (int j = 0; j < lpcre.size(); j++) {
                if (i == j) {
                    continue;
                }
                int c = lpcre.get(i).CountPrefix(lpcre.get(j));
                if (c >= max) {
                    max = c;
                }
            }
            same[max]++;
        }
        for (int i = 0; i < same.length; i++) {
            if (same[i] != 0) {
                System.out.println(i + ": " + same[i]);
            }
        }
    }

    private void doGetPrefix() {
        LinkedList<String> ls = new LinkedList<String>();
        int[] count = new int[4000];
        for (int i = 0; i < lpcre.size(); i++) {
            int t = lpcre.size() - i;
            System.out.println(t);
            int max = 0;

            for (int j = i + 1; j < lpcre.size(); j++) {

                String s = lpcre.get(i).GetPrefix(lpcre.get(j));
                if (s.length() >= 8) {
                    int c = ls.indexOf(s);
                    if (c != -1) {//have
                        count[c]++;
                    } else { //don't have
                        ls.add(s);
                        count[ls.size() - 1]++;
                    }
                }
            }
        }

        for (int i = 0; i < ls.size(); i++) {
            System.out.println(i + " . " + count[i] + " .      " + ls.get(i));
        }
    }

    private void doPrefixPartition() {
        //first get list of prefix
        // after this step, ls will containt list of prefix which is share among pcres
        // at least two pcre is needed and minimum share is 8 character.

        LinkedList<String> ls = new LinkedList<String>();
        int[] count = new int[4000];
        for (int i = 0; i < lpcre.size(); i++) {
            int t = lpcre.size() - i;
            System.out.println(t);

            for (int j = i + 1; j < lpcre.size(); j++) {
                String s = lpcre.get(i).GetPrefix(lpcre.get(j));
                if (s.length() >= 8) {
                    int c = ls.indexOf(s);
                    if (c != -1) {//have
                        count[c]++;
                    } else { //don't have
                        ls.add(s);
                        count[ls.size() - 1]++;
                    }
                }
            }
        }

        //Try to group pcre base on current list of prefix,
        // 1 priority is length.

        //firstly, order it: max lenght -> minimum lenght and pack it in ols link list.
        LinkedList<String> ols = new LinkedList<String>();

        for (int i = 0; i < ls.size(); i++) {
            boolean ok = false;
            for (int j = 0; j < ols.size(); j++) {
                if(ls.get(i).length()>ols.get(j).length()){
                    ok = true;
                    ols.add(j,ls.get(i));
                    break;
                }
            }
            if(!ok)
                ols.addLast(ls.get(i));
        }
        // second, partipate pcre, pcres is add to link list corespond to its prefix's position on ols
        // after insertion, pcre is remove from lpcre
        LinkedList<LinkedList<PCRE>> list = new LinkedList<LinkedList<PCRE>>();
        for(int i =0; i<ols.size(); i++){
            list.add(new LinkedList<PCRE>());
        }
        for(int i =0; i<ols.size(); i++){
            String cp = ols.get(i);
            for(int j =0; j<lpcre.size();){
               if(lpcre.get(j).regex.startsWith(cp)){
                   list.get(i).add(lpcre.get(j));
                   lpcre.remove(j);
               }else
                   j++;
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "prefix.reduce.pcre.ref"));
            // write pcre
            for (int i = 0; i < ols.size(); i++) {
                if(list.get(i).size() <= 1)
                    continue;
                bw.write("\n#Prefix="+ols.get(i) +"\n");

                bw.write("#noPcre " + list.get(i).size() + "\n");
                bw.write("#noChar " + ols.get(i).length() + "\n\n");
                
                for(int j =0; j<list.get(i).size();j++){
                    bw.write(list.get(i).get(j) +"\n");
                }
            }
            // write result:
            bw.write("\n#Result:\n");
            for (int i = 0; i < ols.size(); i++) {
                if(list.get(i).size() <= 1)
                    continue;
                bw.write("\n#Prefix="+ols.get(i) +"\n");

                bw.write("\t#noPcre " + list.get(i).size() + "\n");
                bw.write("\t#noChar " + ols.get(i).length() + "\n\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
