/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTL_Creator;

import ParseTree.ParseTree;
import ParseTree.RegexTree;
import RegexEngine.BlockConRep;
import RegexEngine.ReEngine;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import bram.BRAM;

import nfa.NFA;

/**
 * 1. BRam structure
 *
 */
/**
 *
 * @author heckarim
 */
public class RTL_Creator_v1 {

    public int noBRam;
    public BRAM[] arrayBRam;
    public LinkedList<String>[] arrayListPcre; // each element will containt set of pcre of BRam.
    public boolean no_CRB;

    public RTL_Creator_v1() {
    }

    public void setNoCRB(boolean bo) {
        this.no_CRB = bo;
    }

    public void setNumberBRam(int n) {
        this.noBRam = n;
        //create array BRam
        this.arrayBRam = new BRAM[n];
        //update BRam Order
        for (int i = 0; i < n; i++) {
            this.arrayBRam[i] = new BRAM(i);
        }
        this.arrayListPcre = new LinkedList[n];
        for (int i = 0; i < n; i++) {
            this.arrayListPcre[i] = new LinkedList<String>();
        }
    }

    public void addPcreBRam(int order, String pcre) {
        arrayListPcre[order].add(pcre);
    }

    public boolean constructBRam() {
        boolean ret = false;

        return ret;
    }

    public void createRTLStructure() {

        for (int i = 0; i < this.arrayListPcre.length; i++) {
            this.createBRam(i);
        }

    }

    private void createBRam(int i) {
        LinkedList<String> lpcre = this.arrayListPcre[i];
        LinkedList<ReEngine> lengine = new LinkedList<ReEngine>();
        System.out.println("\n\n RTL_Creator_v1: Process BRAM " + i + "\n");
        for (int j = 0; j < lpcre.size(); j++) {
            System.out.println("" + lpcre.get(j));
            RegexTree tree = new RegexTree(lpcre.get(j), no_CRB);
            tree.parseTree();
            NFA nfa = new NFA();
            nfa.buildNFA(tree);
            nfa.reduceRedundantState();
            ReEngine engine = new ReEngine();
            engine.buildEngine(nfa);
            lengine.add(engine);
        }
        BRAM bram = arrayBRam[i];
        bram.addEngine(lengine);
        //System.out.println("RTL_Creator_v1: Do Union charblock");
        bram.unionCharBlocks();
        //System.out.println("RTL_Creator_v1: Print Char block");
    }

    public void outputStatistic(String file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("noPCRE\tnoChar\tnoNFA\tnoStateBlock\tnoCRB\n");
            for (int i = 0; i < this.arrayBRam.length; i++) {
                System.out.println("\n\n RTL_Creator_v1: OutputStatistic BRAM " + i + "\n");
                BRAM bram = this.arrayBRam[i];
                bw.write("#bram " + i + "\t");
                bw.write(bram.engineList.size() + "\t");
                bw.write(bram.listBlockChar.size() + "\t");

                //count nfa state
                int nfa = 0;
                for (int j = 0; j < bram.engineList.size(); j++) {
                    ReEngine re = bram.engineList.get(j);
                    nfa += re.nfa.lState.size();
                }
                bw.write(nfa + "\t");
                //count no state crb
                int state = 0, crb = 0;
                for (int j = 0; j < bram.engineList.size(); j++) {
                    ReEngine re = bram.engineList.get(j);
                    for (int k = 0; k < re.listBlockState.size(); k++) {
                        if (re.listBlockState.get(k).isConRep) {
                            crb++;
                            BlockConRep bcr = (BlockConRep) re.listBlockState.get(k);

                        } else {
                            state++;
                        }
                    }
                }
                bw.write(state + "\t" + crb + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
