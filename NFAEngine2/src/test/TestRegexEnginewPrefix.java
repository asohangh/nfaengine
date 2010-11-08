/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import NFA.NFA;
import ParseTree.ParseTree;
import RegexEngine.ReEngine;

/**
 *
 * @author heckarim
 */
public class TestRegexEnginewPrefix {

    public static void main(String[] args) {
        TestRegexEnginewPrefix control = new TestRegexEnginewPrefix();
        control.doTest();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest() {
        //String rule = "/a*b?|c+(d|e)/smi";
        //String rule="/\\x3Ctitle\\x3ETroya\\s+\\x2D\\s+by\\s+Sma\\s+Soft\\x3C\\x2Ftitle\\x3E/smi";
        String rule = "/abc(a|b|c){3,5}dabe/smi";

        ParseTree tree = new ParseTree(rule);
        System.out.println("pcre is: " + tree.rule.getPattern() + " -------- " + tree.rule.getModifier());
        tree.printTree();
        String s = "e";
        s = tree.patternOfPCRE(tree.root);
        System.out.println(s);
        tree.generateDotFile(null, null);

        NFA nfa = new NFA();
        nfa.buildNFA(tree);

        // nfa.updateID();
        System.out.println("Original NFA:");
        //nfa.print();
        nfa.generateDotFile("nfa_origin.dot", null);

        nfa.reduceRedundantState();
        nfa.generateDotFile("nfa_reduce.dot", null);
        //System.out.println("Modified NFA:");
        //Creat ReGexEngien
        ReEngine engine = new ReEngine();
        engine.buildEngine(nfa);
        engine.generateDotFile("ReEngine.dot", null);
        System.out.println("Original blockChar list : ");
        engine.printBlockChar();
        engine.reduceBlockChar();
        System.out.println("REduced blockChar list : ");
        engine.printBlockChar();

    }
}
