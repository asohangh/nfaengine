/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import ParseTree.RegexTree;
import java.util.LinkedList;

import mip.pcre.pcre_v2.PCREPattern;
import nfa.NFA;

/**
 *
 * @author heckarim
 */
public class ReEngineGroup {

    public LinkedList<ReEngine> lengine;
    public LinkedList<Prefix> lprefix;
    public LinkedList<Infix> linfix;
    public BlockMemory memory;
    public int id;
    public int noChar = 0;

    public ReEngineGroup() {
        this.id = -1;
        memory = new BlockMemory();
        lprefix = new LinkedList<Prefix>();
        linfix = new LinkedList<Infix>();
        lengine = new LinkedList<ReEngine>();
    }

    public ReEngineGroup(int id) {
        this.id = id;
        memory = new BlockMemory();
        lprefix = new LinkedList<Prefix>();
        linfix = new LinkedList<Infix>();
        lengine = new LinkedList<ReEngine>();
    }

    public int getNoChar(){
        return this.noChar;
    }
    public void addPrefix(LinkedList<String> lsprefix) {
        this.buildPrefix(lsprefix);
        //prefix
        for (int i = 0; i < this.lprefix.size(); i++) {
            this.memory.insertChar(this.lprefix.get(i).listBlockChar);
        }
    }

    public void addInfix(LinkedList<String> lsinfix) {
        this.buildInfix(lsinfix);
        //infix
        for (int i = 0; i < this.linfix.size(); i++) {
            this.memory.insertChar(this.linfix.get(i).listBlockChar);
        }
    }

    public void addEngine(LinkedList<String> lsengine) {
        this.buildEngine(lsengine);
        //engine
        for (int i = 0; i < this.lengine.size(); i++) {
            this.memory.insertChar(this.lengine.get(i).listBlockChar);

        }
    }

    public void buildPrefix(LinkedList<String> lsprefix) {
        for (int i = 0; i < lsprefix.size(); i++) {
            String s = lsprefix.get(i);
            PCREPattern rule = new PCREPattern(s);
            RegexTree tree = new RegexTree(rule);
            tree.parseTree();
            NFA nfa = new NFA();
            nfa.buildNFA(tree);
            Prefix pre = new Prefix(i, nfa);
            pre.buildPrefix();
            lprefix.add(pre);
        }
        //update order
        for (int i = 0; i < this.lprefix.size(); i++) {
            this.lprefix.get(i).order = i;
            this.lprefix.get(i).prefixID = i;
        }
    }

    public void buildInfix(LinkedList<String> lsinfix) {

        for (int i = 0; i < lsinfix.size(); i++) {
            String s = lsinfix.get(i);
            PCREPattern rule = new PCREPattern(s);
            RegexTree tree = new RegexTree(rule);
            tree.parseTree();
            NFA nfa = new NFA();
            nfa.buildNFA(tree);
            Infix in = new Infix(i, nfa);
            in.buildInfix();
            linfix.add(in);
        }
        //update order
        for (int i = 0; i < this.linfix.size(); i++) {
            this.linfix.get(i).order = i;
            this.linfix.get(i).infixID = i;
        }
    }

    public void buildEngine(LinkedList<String> lsengine) {
        int sizeChar = 0;
        for (int i = 0; i < lsengine.size(); i++) {
            String s = lsengine.get(i);
            PCREPattern rule = new PCREPattern(s);
            sizeChar += rule.getNoChar();
            RegexTree tree = new RegexTree(rule);
            tree.parseTree();
            System.out.println("REEngineGrup BuildEngine: pcre: " + tree.rule.getRule());
            NFA nfa = new NFA();
            nfa.buildNFA(tree);
            ReEngine engine = new ReEngine();
            engine.buildEngine(nfa);
            engine.updateGroupID(this.id);
            lengine.add(engine);
        }
        //update order
        for (int i = 0; i < this.lengine.size(); i++) {
            this.lengine.get(i).order = i;
        }
        this.noChar += sizeChar;
    }

    private void updatememory() {
        //prefix
        for (int i = 0; i < this.lprefix.size(); i++) {
            this.memory.insertChar(this.lprefix.get(i).listBlockChar);
        }
        //infix
        for (int i = 0; i < this.linfix.size(); i++) {
            this.memory.insertChar(this.linfix.get(i).listBlockChar);
        }
        //engine
        for (int i = 0; i < this.lengine.size(); i++) {
            this.memory.insertChar(this.lengine.get(i).listBlockChar);
        }
    }

    public void print() {

        System.out.println("PCRE Egnien Group " + id);
        //Frint Prefix
        System.out.println("Group " + id + " : Prefix");
        for (int i = 0; i < this.lprefix.size(); i++) {
            this.lprefix.get(i).print();
        }
        //Print infix
        System.out.println("Group " + id + " : Infix");
        for (int i = 0; i < this.linfix.size(); i++) {
            this.linfix.get(i).print();
        }
        //Print engine
        System.out.println("Group " + id + " : Engine");
        for (int i = 0; i < this.lengine.size(); i++) {
            this.lengine.get(i).print();
        }
        //print blockmemory
        System.out.println("Group " + id + " : Blockmemory");
        this.memory.print();
    }

    public Infix getInfix(int id) {
        Infix ret = null;
        for (int i = 0; i < this.linfix.size(); i++) {
            if (this.linfix.get(i).id == id) {
                ret = this.linfix.get(i);
            }
        }
        return ret;
    }

   
}
