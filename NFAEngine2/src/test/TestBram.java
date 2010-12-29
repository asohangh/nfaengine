/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import BRAM.BRAM;
import NFA.NFA;
import ParseTree.ParseTree;
import RegexEngine.BlockConRep;
import RegexEngine.BlockState;
import RegexEngine.ReEngine;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class TestBram {
    public String genfolder = System.getProperty("user.dir") + File.separator + "GenHDL" + File.separator;

    public static void main(String[] args) {
        TestBram control = new TestBram();
        control.doTest();
        //control.doOutput();
        //control.doTest();
    }
    public void doTest1(){
        BlockState bs = new BlockState();
        bs.printTest();
        ((BlockConRep)bs).printTest();
    }
    public void doTest() {
        //String rule = "/a*b?|c+(d|e)/smi";
        //String rule="/\\x3Ctitle\\x3ETroya\\s+\\x2D\\s+by\\s+Sma\\s+Soft\\x3C\\x2Ftitle\\x3E/smi";
        int size;
        String[] rule = new String[10];
        rule[0] = "/^ab\\xabdabe/smi";
        //rule[1] = "/a(b|c)(b){6}e/smi";
        rule[1] = "/a(b|c)(b(a|b)(c|d)){6}e/smi";
        rule[2] = "/e*\\xab\\nf/smi";
        size = 3;
        LinkedList<ReEngine> lengine = new LinkedList<ReEngine>();
        //Creat Regexengine
        for (int i = 0; i < size; i++) {
            System.out.println("\n\n" + rule[i]);
            ParseTree tree = new ParseTree(rule[i]);
            //tree.generateDotFile(null, null);
            NFA nfa = new NFA();
            nfa.buildNFA(tree);
            //nfa.generateDotFile("nfa_origin.dot", null);
            nfa.reduceRedundantState();
            //nfa.generateDotFile("nfa_reduce.dot", null);
            //Creat ReGexEngien
            ReEngine engine = new ReEngine();
            engine.buildEngine(nfa);
            //engine.generateDotFile("ReEngine.dot", null);
            //System.out.println("Original blockChar list : ");
            //engine.printBlockChar();
            //engine.reduceBlockChar();
           // System.out.println("REduced blockChar list : ");
            //engine.printBlockChar();
            lengine.add(engine);
        }
        BRAM bram = new BRAM(0);
        bram.addEngine(lengine);
        System.out.println("Begin to union charblock");
        bram.unionCharBlocks();
        bram.printBlockCharBram();

        //generate each engien
            for (int j = 0; j < bram.engineList.size(); j++) {
                bram.engineList.get(j).buildHDL(genfolder);
            }
        bram._outputFolder = this.genfolder;
        bram.fillEntryValue();
        bram.buildHDL();
        bram.buildCOE();
        bram.buildXCO();
    }
}
