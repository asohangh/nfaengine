/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PrefixSharing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.naming.Reference;

/**
 *
 * @author heckarim
 */
public class prefixPartition {

    private String outfolder = System.getProperty("user.dir") + File.separator +
            "output.2.9" + File.separator + "prefix" + File.separator;
    LinkedList<RePrefix> prelist = new LinkedList<RePrefix>();
    LinkedList<pcre> lregex = new LinkedList<pcre>();

    public static void main(String[] args) throws IOException {
        prefixPartition pre = new prefixPartition();
        pre.action();
    }

    private void action() throws IOException {
        //read from file
        String inputfolder = System.getProperty("user.dir") + File.separator + "output.2.9" +
                File.separator + "extract" + File.separator;
        String filename = "4dung";
        filename +=".pcre";
        LinkedList<String> lstring = this.readfromfile(inputfolder + filename);
        System.out.println("Read file ok");
        //convert to list of pcre
        for (int i = 0; i < lstring.size(); i++) {
            String rule = lstring.get(i);
            pcre pc = new pcre(rule);
            this.lregex.add(pc);

        }
        System.out.println("conver to pcre ok");
        int noatom = 0;
        for(int i =0; i<this.lregex.size(); i++){
            noatom += this.lregex.get(i).getNoAtom();
        }
        System.out.println("noAtom: " + noatom);
        // do partition
        this.doPartition();
        // out to file
        this.outtofile(outfolder + filename + ".prefix");
    }

    private LinkedList<String> readfromfile(String string) throws IOException {
        LinkedList<String> ls = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(string));
        String s;
        while ((s = br.readLine()) != null) {
            ls.add(s);
        }
        br.close();
        return ls;
    }

    private void doPartition() {
        //State 1: each rule have its own common prefix.
        for (int i = 0; i < this.lregex.size(); i++) {
            for (int j = i + 1; j < this.lregex.size(); j++) {
                RePrefix pre = this.lregex.get(i).getMaxprefix(this.lregex.get(j));
                if (pre != null) {
                    this.lregex.get(i).addPrefix(pre);
                    this.lregex.get(j).addPrefix(pre);
                    this.prelist.add(pre);
                }
            }
        }
        System.out.println("<doPartition> State 1 done");
        //this.printResult();
        //State 2: reduce common prefix
        LinkedList<RePrefix> lsprefix = new LinkedList<RePrefix>();
        if (this.prelist.size() >= 1) {
            lsprefix.add(this.prelist.getFirst());
        }
        for (int i = 1; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
            boolean flag = false;
            for (int j = 0; j < lsprefix.size(); j++) {
                RePrefix pre1 = lsprefix.get(j);
                if (pre1.compareto(pre)) {
                    //if the same so combine these prefix, pre -> pre1
                    //check and add pcre of pre to pre1 list
                    for (int k = 0; k < pre.lregex.size(); k++) {
                        if (pre1.lregex.indexOf(pre.lregex.get(k)) == -1) {//add new regex
                            pre1.lregex.add(pre.lregex.get(k));
                        }
                    }
                    //replace all prelist of pcre to pre1.
                    for (int k = 0; k < pre1.lregex.size(); k++) {
                        pre1.lregex.get(k).prelist.remove(pre);
                        pre1.lregex.get(k).prelist.add(pre1);
                    }
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                lsprefix.add(pre);
            }
        }
        this.prelist = lsprefix;
        System.out.println("<doPartition> State 2 done");
        //print reuslt
        // this.printResult();

        //Stage 3: keep max share, remove remain
        // count max share
        for (int i = 0; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
            pre.countShare();
        }
        // arrange prefix list
        LinkedList<RePrefix> lsort = new LinkedList<RePrefix>();
        if (this.prelist.size() > 0) {
            lsort.add(this.prelist.getFirst());
        }
        for (int i = 1; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
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
        this.prelist = lsort;
        // choose max, remove other
        for (int i = 0; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
            //remove other prefix in regex list
            for (int j = 0; j < pre.lregex.size(); j++) {
                pcre pe = pre.lregex.get(j);
                for (int k = 0; k < pe.prelist.size(); k++) {
                    RePrefix pePrefix = pe.prelist.get(k);
                    if (pePrefix != pre) {
                        this.prelist.remove(pePrefix);
                    }
                }
                //remove all other prefix
                pe.prelist.removeAll(pe.prelist);
                pe.prelist.add(pre);
            }
        }
        System.out.println("Stage 3 done");
        //Stage 4: replace prefix
        for (int i = 0; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
            //config id
            pre.id = i;
            pre.replacePrefix();
        }

        this.printResult();
    }

    private void printResult() {
        int reduce = 0;
        int base = 0;
        for (int i = 0; i < this.prelist.size(); i++) {
            RePrefix pre = this.prelist.get(i);
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
            if (this.prelist.isEmpty()) {
                noNoShare++;
            }
        }
        for (int i = 0; i < this.lregex.size(); i++) {
            System.out.println(this.lregex.get(i).getString());
        }

        System.out.println(" No Non sharing Pcre: " + noNoShare);
        System.out.println(" No  Pcre: " + this.lregex.size());
        System.out.println(" No Prefix: " + this.prelist.size());
        System.out.println(" No sharing atoms: " + reduce);
        System.out.println(" No sharing base  atoms: " + base);
        System.out.println(" No atoms: " + noAtom);
    }

    private void outtofile(String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        //write prefix
        bw.write("#prefix" + "\n");
        for (int i = 0; i < this.prelist.size(); i++) {
            bw.write(this.prelist.get(i).getPrefixString() + "\n");
        }
        //write engine
        bw.write("#engine" + "\n");
        for (int i = 0; i < this.lregex.size(); i++) {
            bw.write(this.lregex.get(i).getRuleString() + "\n");
        }
        bw.flush();
        bw.close();
        ;
    }
}
