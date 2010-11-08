/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import BRAM.BRAM;
import NFA.NFA;
import ParseTree.ParseTree;
import PrefixShare.PrefixShare;
import RegexEngine.BlockConRep;
import RegexEngine.BlockState;
import RegexEngine.ReEngine;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class TestBramwPrefix {

    public static void main(String[] args) {
        TestBramwPrefix control = new TestBramwPrefix();
        control.doTest();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest1() {
        BlockState bs = new BlockState();
        bs.printTest();
        ((BlockConRep) bs).printTest();
    }

    public void doTest() {
        //String rule = "/a*b?|c+(d|e)/smi";
        //String rule="/\\x3Ctitle\\x3ETroya\\s+\\x2D\\s+by\\s+Sma\\s+Soft\\x3C\\x2Ftitle\\x3E/smi";
        int size;
        String[] rule = new String[10];
        rule[0] = "/abcdabe/smi";
        rule[1] = "/ade/smi";
        rule[2] = "/e*f/smi";
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
        //create and insert prefix engine;
        String prefix;
        String[] subfix = new String[10];

        prefix = "/abc/smi";
        subfix[0] = "/x|y+z/smi";
        subfix[1] = "/o(p|q)?/smi";
        subfix[2] = "/mnp/smi";
        subfix[3] = "/rtsu/smi";

        PrefixShare ps = new PrefixShare();
        ps.addPrefixPcre(prefix);
        for(int i =0; i<4; i++){
            ps.addSubfixPcre(subfix[i]);
        }
        ps.buildEngine();
        //and add combine engine to this list of Bram engine
        lengine.add(ps.engine);
        //Process bram.
        BRAM bram = new BRAM(0);
        bram.addEngine(lengine);
        System.out.println("Begin to union charblock");
        bram.unionCharBlocks();
        bram.printBlockCharBram();
    }
}

