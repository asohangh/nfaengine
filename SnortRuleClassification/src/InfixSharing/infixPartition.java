/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package InfixSharing;

import Regex.Element;
import Regex.ElementAtom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class infixPartition {

    private String outfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator + "infix" + File.separator;
    private String inputfolder = System.getProperty("user.dir") + File.separator + "output.2.9"
            + File.separator + "prefix" + File.separator;
    LinkedList<ShareableUnit> linfix1 = new LinkedList<ShareableUnit>();
    LinkedList<ReInfix> linfix2 = new LinkedList<ReInfix>();
    LinkedList<pcre> lregex = new LinkedList<pcre>();

    public static void main(String[] args) throws IOException {
        infixPartition pre = new infixPartition();
        pre.action();
    }

    private void action() throws IOException {
        //read from file
        String filename = "icnc";
        filename += ".pcre.prefix";
        LinkedList<String> lsprefix = this.readprefix(inputfolder + filename);
        LinkedList<String> lstring = this.readfromfile(inputfolder + filename);
        System.out.println("Read file ok");

        //convert to list of pcre
        for (int i = 0; i < lstring.size(); i++) {
            String rule = lstring.get(i);
            pcre pc = new pcre(rule);
            this.lregex.add(pc);
        }
        System.out.println("conver to pcre ok");

        // do partition
        this.doPartition();
        this.outtofile(outfolder + filename + ".infix",lsprefix);

    }

    private LinkedList<String> readfromfile(String string) throws IOException {
        LinkedList<String> ls = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(string));
        String s;
        boolean read = false;
        while ((s = br.readLine()) != null) {
            if (s.compareToIgnoreCase("#prefix") == 0) {
                read = false;
            } else if (s.compareToIgnoreCase("#engine") == 0) {
                read = true;
            } else {
                if (read) {
                    ls.add(s);
                }
            }
        }
        br.close();
        return ls;
    }

    private void printResult() {
        int reduce = 0;
        int base = 0;
        for (int i = 0; i < this.linfix1.size(); i++) {
            ShareableUnit pre = this.linfix1.get(i);
            System.out.println(i + " " + pre.sizeShare + " ===========================================================");
            pre.print();
            reduce += pre.sizeShare;
            base += pre.sizeAtom;
        }
        // cal no of no sharing pcre
        int noAtom = 0;
        int noNoShare = 0;
        for (int i = 0; i < this.lregex.size(); i++) {
            noAtom += this.lregex.get(i).getNoAtom();
            if (this.linfix1.isEmpty()) {
                noNoShare++;
            }
        }
        System.out.println(" No Non sharing Pcre: " + noNoShare);
        System.out.println(" No  Pcre: " + this.lregex.size());
        System.out.println(" No Prefix: " + this.linfix1.size());
        System.out.println(" No sharing atoms: " + reduce);
        System.out.println(" No sharing base  atoms: " + base);
        System.out.println(" No atoms: " + noAtom);
    }

    private void doPartition() {
        //Stage 1: extract all shareable unit
        for (int i = 0; i < this.lregex.size(); i++) {
            pcre pe = this.lregex.get(i);
            this.linfix1.addAll(pe.getshareablepattern());
        }
        System.out.println("State 1: ");
        /*
        for (int i = 0; i < this.linfix1.size(); i++) {
        ShareableUnit in = this.linfix1.get(i);
        in.print();
        //System.out.println(in.getString());
        }*/
        System.out.println("Stage 1 Done");
        //Stage 2: get level 2 sharing.
        for (int i = 0; i < this.linfix1.size(); i++) {
            ShareableUnit in1 = this.linfix1.get(i);
            for (int j = i + 1; j < this.linfix1.size(); j++) {
                ShareableUnit in2 = this.linfix1.get(j);
                LinkedList<Element> el = this.getMaxShare(in1, in2);
                //System.out.println("maxshare size : " + el.size());
                if (el.size() >= 7) {
                    ReInfix in = new ReInfix(el, in1, in2);
                    in1.inlist.add(in);
                    in2.inlist.add(in);
                    this.linfix2.add(in);
                }
            }
        }/*
        for (int i = 0; i < this.linfix2.size(); i++) {
        ReInfix in = this.linfix2.get(i);
        in.print();
        }*/
        System.out.println("State 2 done");
        //Stage 3 reduce same lv2
        LinkedList<ReInfix> ltemp = new LinkedList<ReInfix>();
        if (!this.linfix2.isEmpty()) {
            ltemp.add(this.linfix2.getFirst());
        }
        for (int i = 1; i < this.linfix2.size(); i++) {
            ReInfix in1 = this.linfix2.get(i);
            boolean add = true;
            for (int j = 0; j < ltemp.size(); j++) {
                ReInfix in2 = ltemp.get(j);
                if (in1.compareto(in2)) {
                    for (int k = 0; k < in1.lshareable.size(); k++) {
                        ShareableUnit share = in1.lshareable.get(k);
                        if (!in2.lshareable.contains(in1.lshareable.get(k))) {
                            in2.lshareable.add(in1.lshareable.get(k));
                        }
                        share.inlist.remove(in1);
                        share.inlist.add(in2);
                    }
                    add = false;
                }
            }
            if (add) {
                ltemp.add(in1);
            }
        }
        this.linfix2 = ltemp;
        /* System.out.println("list reduce");
        for (int i = 0; i < this.linfix2.size(); i++) {
        ReInfix in = this.linfix2.get(i);
        in.print();
        }*/
        System.out.println("State 3 done");
        //Stage 4: order linifx2
        for (int i = 0; i < this.linfix2.size(); i++) {
            ReInfix in = this.linfix2.get(i);
            in.countShare();
        }
        // arrange prefix list
        LinkedList<ReInfix> lsort = new LinkedList<ReInfix>();
        if (this.linfix2.size() > 0) {
            lsort.add(this.linfix2.getFirst());
        }
        for (int i = 1; i < this.linfix2.size(); i++) {
            ReInfix pre = this.linfix2.get(i);
            boolean insert = false;
            for (int j = 0; j < lsort.size(); j++) {
                if (pre.sizeShare >= lsort.get(j).sizeShare) {
                    insert = true;
                    lsort.add(j, pre);
                    break;
                }
            }
            if (!insert) {
                lsort.addLast(pre);
            }
        }
        this.linfix2 = lsort;
        //chose max

        for (int i = 0; i < this.linfix2.size(); i++) {
            ReInfix pre = this.linfix2.get(i);
            //remove other prefix in regex list
            for (int j = 0; j < pre.lshareable.size(); j++) {
                ShareableUnit pe = pre.lshareable.get(j);
                for (int k = 0; k < pe.inlist.size(); k++) {
                    ReInfix pePrefix = pe.inlist.get(k);
                    if (pePrefix != pre) {
                        System.out.println("deleteinfix " + pePrefix.getString());
                        if (this.linfix2.remove(pePrefix)) {
                            System.out.println("remove ok");
                        }
                    }
                }
                //remove all other prefix
                pe.inlist.removeAll(pe.inlist);
                pe.inlist.add(pre);
            }
        }
        /*System.out.println("choose max");
        for (int i = 0; i < this.linfix2.size(); i++) {
        ReInfix in = this.linfix2.get(i);
        in.print();
        }*/

        /*
        for (int i = 0; i < this.linfix2.size(); i++) {
        ReInfix in = this.linfix2.get(i);
        in.print();
        }*/
        System.out.println("State 4 done");
        //Stage 5:  output
        //update id
        for (int i = 0; i < this.linfix2.size(); i++) {
            this.linfix2.get(i).id = i;
        }
        //replace element list with infix element
        for (int i = 0; i < this.linfix2.size(); i++) {
            ReInfix infix = this.linfix2.get(i);
            for (int j = 0; j < infix.lshareable.size(); j++) {
                ShareableUnit share = infix.lshareable.get(j);
                share.replace(infix);
            }
        }
        for (int i = 0; i < this.linfix1.size(); i++) {
            ShareableUnit share = this.linfix1.get(i);
            share.replaceRegex();
        }

        System.out.println("\n\n\nPrint list of share");
        for (int i = 0; i < this.linfix2.size(); i++) {
            ReInfix in = this.linfix2.get(i);
            in.print();
        }
        System.out.println("\n Print list of regex");
        for (int i = 0; i < this.lregex.size(); i++) {
            System.out.println(this.lregex.get(i).getString());
        }
        // write result
        int count = 0;
        int atom = 0;
        System.out.println("no of infix" + this.linfix2.size());
        for (int i = 0; i < this.linfix2.size(); i++) {
            count += this.linfix2.get(i).sizeShare;
            atom += this.linfix2.get(i).sizeAtom;
        }
        System.out.println("no of atom " + atom);
        System.out.println("no of share " + count);

    }

    private LinkedList<Element> getMaxShare(ShareableUnit in1, ShareableUnit in2) {
        LinkedList<Element> elist = new LinkedList<Element>();
        int min = (in1.lelement.size() < in2.lelement.size()) ? in1.lelement.size() : in2.lelement.size();
        int max = 0;
        int size1 = in1.lelement.size();
        int size2 = in2.lelement.size();
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                LinkedList<Element> tlist = new LinkedList<Element>();
                for (int k = 0; ((k + i) < size1) && ((k + j) < size2); k++) {
                    Element e1 = in1.lelement.get(i + k);
                    Element e2 = in2.lelement.get(k + j);
                    if (e1.compareto(e2)) {
                        tlist.add(e1);
                        //System.out.println("e1:" + e1.getString() + " e2: " + e2.getString());
                    } else {
                        if (tlist.size() > max) {
                            max = tlist.size();
                            elist = tlist;
                            if (max >= 4) {
                                // System.out.println("max " + max);
                            }
                        }
                        tlist = new LinkedList<Element>();
                    }
                }
                if (tlist.size() > max) {
                    max = tlist.size();
                    elist = tlist;
                }
            }
        }/*
        System.out.println("\ngetmax share: ");
        System.out.println("share 1: " + in1.getString());
        System.out.println("share 2: " + in2.getString());
        System.out.print("Showtime : ");
        for (int i = 0; i < elist.size(); i++) {
        System.out.print(elist.get(i).getString());
        }
        System.out.println("");*/
        return elist;
    }

    private void outtofile(String file, LinkedList<String> ls) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        //System.out.println("\n\n\nPrint list of share");
        bw.write("#prefix\n");
        for (int i = 0; i < ls.size(); i++) {
            bw.write(ls.get(i) + "\n");
        }
        bw.write("#infix\n");
        for (int i = 0; i < this.linfix2.size(); i++) {
            ReInfix in = this.linfix2.get(i);
            bw.write(in.getInfixString() + "\n");
        }
        //System.out.println("\n Print list of regex");
        bw.write("#engine\n");
        for (int i = 0; i < this.lregex.size(); i++) {
            bw.write(this.lregex.get(i).getRuleString() + "\n");
        }


        bw.flush();
        bw.close();
    }

    private LinkedList<String> readprefix(String string) throws FileNotFoundException, IOException {
        LinkedList<String> ls = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(string));
        String s;
        boolean read = false;
        while ((s = br.readLine()) != null) {
            if (s.compareToIgnoreCase("#prefix") == 0) {
                read = true;
            } else if (s.compareToIgnoreCase("#engine") == 0) {
                read = false;
            } else {
                if (read) {
                    ls.add(s);
                }
            }
        }
        br.close();
        return ls;
    }
}
