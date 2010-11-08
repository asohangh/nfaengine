/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import NFA.NFA;
import ParseTree.ParseTree;

/**
 *
 * @author heckarim
 */
public class TestNFA {

    public static void main(String[] args) {
        TestNFA control = new TestNFA();
        control.doTest();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest() {
        String rule = "/a*b?|c+(d|e)/smi";

        ParseTree tree=new ParseTree(rule);
        System.out.println("pcre is: "+tree.rule.getPattern() +" -------- "+tree.rule.getModifier())	;
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
        
    }
}
