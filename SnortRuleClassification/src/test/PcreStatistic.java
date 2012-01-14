/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import PcreParseTree.PcreRule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import VRTSignature.*;
import VRTSignature.PCRE;

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
        lpcre = VRTSignature.References.ReadPcreFromFile(outputfolder + "all.pcre.ref");


        //=================================================
        // count modifier.
        this.doCountModifier();
        //=================================================
        // count operator.
        this.doCountOperator();
        //=================================================
        // count 
        this.doCountConstraint();
        //=================================================
        // Reduce pcre
        //this.doReducePcre();
        //=================================================
        //prefix partition
        //this.doCheckPrefix();
        //this.doGetPrefix();
        // this.doPrefixPartition();
        //=================================================
        //Simple pcre
        //this.doGetSimplePcre();


    }

    /**
     *
     */
    public void doReducePcre(LinkedList<PCRE> lpcre) {
        LinkedList<PCRE> rpcre = new LinkedList<PCRE>();
        for (int i = 0; i < lpcre.size(); i++) {
            boolean same = false;
            PCRE temp = lpcre.get(i);
            for (int j = 0; j < rpcre.size(); j++) {
                if (temp.compareRegexTo(rpcre.get(j))) {
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
        int[] modifier = new int[VRTSignature.References._pcreModifier.length];
        LinkedList<LinkedList<PCRE>> mlpcre = new LinkedList<LinkedList<PCRE>>();//user for store linkedlist of pcre belong to dedicated modifier
        for (int i = 0; i < modifier.length; i++) {
            LinkedList<PCRE> ltemp = new LinkedList<PCRE>();
            mlpcre.add(ltemp);
        }

        for (int i = 0; i < lpcre.size(); i++) {
            PCRE pcre = lpcre.get(i);
            for (int j = 0; j < modifier.length; j++) {
                if (pcre.modify.contains(VRTSignature.References._pcreModifier[j])) {
                    modifier[j]++;
                    mlpcre.get(j).add(pcre);
                }
            }
        }
        // write to output
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "modifier.all.pcre.ref"));
            //write each rule
            for (int j = 0; j < modifier.length; j++) {
                bw.write("\n\n#Modifier: " + VRTSignature.References._pcreModifier[j] + "\n\n");
                LinkedList<PCRE> ltemp = mlpcre.get(j);
                for (int i = 0; i < ltemp.size(); i++) {
                    bw.write(ltemp.get(i).toString() + "\n");
                }
            }
            bw.write("\n\n# Result: \n");
            for (int j = 0; j < modifier.length; j++) {
                bw.write(VRTSignature.References._pcreModifier[j] + "\t " + modifier[j] + "\n");
                System.out.println(VRTSignature.References._pcreModifier[j] + ": " + modifier[j]);
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
                int c = lpcre.get(i).countPrefix(lpcre.get(j));
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
/*
    private void doGetPrefix() {
        LinkedList<String> ls = new LinkedList<String>();
        int[] count = new int[4000];
        for (int i = 0; i < lpcre.size(); i++) {
            int t = lpcre.size() - i;
            System.out.println(t);
            int max = 0;

            for (int j = i + 1; j < lpcre.size(); j++) {

                String s = lpcre.get(i).getPrefix(lpcre.get(j));
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
     * 
     */
/*
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
                String s = lpcre.get(i).getPrefix(lpcre.get(j));
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
                if (ls.get(i).length() > ols.get(j).length()) {
                    ok = true;
                    ols.add(j, ls.get(i));
                    break;
                }
            }
            if (!ok) {
                ols.addLast(ls.get(i));
            }
        }
        // second, partipate pcre, pcres is add to link list corespond to its prefix's position on ols
        // after insertion, pcre is remove from lpcre
        LinkedList<LinkedList<PCRE>> list = new LinkedList<LinkedList<PCRE>>();
        for (int i = 0; i < ols.size(); i++) {
            list.add(new LinkedList<PCRE>());
        }
        for (int i = 0; i < ols.size(); i++) {
            String cp = ols.get(i);
            for (int j = 0; j < lpcre.size();) {
                if (lpcre.get(j).regex.startsWith(cp)) {
                    list.get(i).add(lpcre.get(j));
                    lpcre.remove(j);
                } else {
                    j++;
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "prefix.reduce.pcre.ref"));
            // write pcre
            for (int i = 0; i < ols.size(); i++) {
                if (list.get(i).size() <= 1) {
                    continue;
                }
                bw.write("\n#Prefix=" + ols.get(i) + "\n");

                bw.write("#noPcre " + list.get(i).size() + "\n");
                bw.write("#noChar " + ols.get(i).length() + "\n\n");

                for (int j = 0; j < list.get(i).size(); j++) {
                    bw.write(list.get(i).get(j) + "\n");
                }
            }
            // write result:
            bw.write("\n#Result:\n");
            for (int i = 0; i < ols.size(); i++) {
                if (list.get(i).size() <= 1) {
                    continue;
                }
                bw.write("\n#Prefix=" + ols.get(i) + "\n");

                bw.write("\t#noPcre " + list.get(i).size() + "\n");
                bw.write("\t#noChar " + ols.get(i).length() + "\n\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
*/
    private void doGetSimplePcre() {
        LinkedList<PCRE> spcre = new LinkedList<PCRE>();
        for (int i = 0; i < lpcre.size(); i++) {
            boolean same = false;
            PCRE pcre = lpcre.get(i);
            if (References.isSimplyPcre(pcre)) {
                spcre.add(pcre);
            }
        }

        References.WritePcreToFile(spcre, outputfolder + "simplepcre.ref");
    }

    /**
     *
     * @param pcre
     * @return
     *  * ? + | {} and ^ $
     *  0 1 2 3 4  5   6 7
     */
    public int[] countPcreOperator(String pcre, String modifier) {
        int[] ret = new int[15];
        for (int j = 0; j < 15; j++) {
            ret[j] = 0;
        }

        int i = 0;
        int count = 0;
        boolean cando = false;
        //^
        if (modifier.contains("t")) {
            ret[6]++;
        }
        i = 0;
        while (i < pcre.length()) {
            char chr = pcre.charAt(i);
            //System.out.println(chr);
            switch (chr) {
                case Refer._char_and:
                    ret[5]++;
                    i++;
                    break;
                case '^':
                    ret[6]++;
                    i++;
                    break;
                case '$':
                    ret[6]++;
                    i++;
                    break;
                case '|':
                    ret[3]++;
                    cando = false;
                    i++;
                    break;
                case '*':
                    ret[0]++;
                    i++;
                    break;
                case '+':
                    ret[2]++;
                    i++;
                    break;
                case '?':
                    ret[1]++;
                    i++;
                    break;
                case ')':
                    cando = true;
                    i++;
                    break;
                case '{':
                    ret[4]++;
                    int last = Refer.getIndexOBlock(pcre.substring(i), '{', '}');
                    cando = true;
                    i = i + last + 1;
                    break;
                case '[':
                    last = Refer.getIndexOBlock(pcre.substring(i), '[', ']');
                    i = i + last + 1;
                    break;
                case '(':
                    i++;
                    break;
                case '\\':
                    if (pcre.charAt(i + 1) == 'x' || pcre.charAt(i + 1) == 'X') {  // \xFF

                        i = i + 4;

                    } else if (pcre.charAt(i + 1) >= '0' && pcre.charAt(i + 1) <= '9') {	// \000
                        i = i + 4;
                    } else {	// \?

                        i = i + 2;
                    }
                    break;
                case '.':
                default://is character;
                    i++;
                    break;
            }
        }
        //System.out.println("After: " + pcre.regex);
        return ret;
    }

    /**
     *
     *      * ? + | {} and ^ $
     *      0 1 2 3 4  5   6 7
     */
    private void doCountOperator() {
        int[] count = new int[15];
        for (int i = 0; i < 15; i++) {
            count[i] = 0;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "all.pcre.operator.count"));
            for (int i = 0; i < lpcre.size(); i++) {
                PcreRule p = new PcreRule(lpcre.get(i).toString());
                int[] ret = this.countPcreOperator(p.getRegex(), p.getModifier());
                for (int j = 0; j < 15; j++) {
                    count[j] += ret[j];
                }
            }
            bw.write("*\t?\t+\t|\t{}\tand\t^\t$\n");
            for (int i = 0; i < 15; i++) {
                bw.write(count[i] + "\t");
            }
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param pcre
     * @return
     * exact  between atleas atmost
     * 0        1       2       3
     */
    public int[] countConstraint(String s) {
        int[] ret = new int[4];

        for (int i = 1; i < s.length() - 2; i++) {
            char ch = s.charAt(i);
            if (ch == '{') {
                char ch0 = s.charAt(i - 1);
                if (ch0 != '\\') {
                    int index = References.getIndexOBlock(s.substring(i), '{', '}');
                    String ss = s.substring(i + 1, i + index);
                    System.out.println(ss);
                    if (ss.startsWith(",")) {
                        ret[3]++;
                    } else if (ss.endsWith(",")) {
                        ret[2]++;
                    } else if (Character.isDigit(ss.charAt(0))) {
                        if (ss.indexOf(",") >= 0) {
                            ret[1]++;
                        } else {
                            ret[0]++;
                        }
                    }
                } else {
                    continue;
                }
            }
        }

        return ret;
    }

    private void doCountConstraint() {
        int[] count = new int[4];
        for (int i = 0; i < 4; i++) {
            count[i] = 0;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfolder + "all.pcre.constraint.count"));
            for (int i = 0; i < lpcre.size(); i++) {
                PcreRule p = new PcreRule(lpcre.get(i).toString());
                int[] ret = this.countConstraint(p.getRegex());
                for (int j = 0; j < 4; j++) {
                    count[j] += ret[j];
                }
            }
            bw.write("exact  between atleas atmost\n");
            for (int i = 0; i < 4; i++) {
                bw.write(count[i] + "\t");
            }
            bw.write("\n");
            //write number of rep
            int[] atleast = new int[3000];
            int[] exactly = new int[3000];
            for (int i = 0; i < 3000; i++) {
                atleast[i] = 0;
                exactly[i] = 0;
            }
            for (int j = 0; j < lpcre.size(); j++) {
                PcreRule p = new PcreRule(lpcre.get(j).toString());
                String s = p.getRegex();

                for (int i = 1; i < s.length() - 2; i++) {
                    char ch = s.charAt(i);
                    if (ch == '{') {
                        char ch0 = s.charAt(i - 1);
                        if (ch0 != '\\') {
                            int index = References.getIndexOBlock(s.substring(i), '{', '}');
                            String ss = s.substring(i + 1, i + index);
                            System.out.println(ss);
                            if (ss.startsWith(",")) {//atmost
                            } else if (ss.endsWith(",")) {//atleast
                                int in = Integer.parseInt(ss.substring(0, ss.length() - 1));
                                atleast[in]++;
                            } else if (Character.isDigit(ss.charAt(0))) {
                                if (ss.indexOf(",") >= 0) {//between
                                } else { // exactly.
                                    int in = Integer.parseInt(ss);
                                    exactly[in]++;
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }

            }
            bw.write("\ncount \t Atleast \t exactly\n");

            for (int i = 0; i < 3000; i++) {
                if (atleast[i] != 0 || exactly[i] != 0) {
                    bw.write(i + "\t" + atleast[i] + "\t" + exactly[i] + "\n");
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(PcreStatistic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
