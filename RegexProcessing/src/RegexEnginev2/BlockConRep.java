/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import ParseTree.ParseTree;
import PCRE.Refer;
import ParseTree.Node;
import ParseTree.RegexTree;
import hdl_generator.HDL_CRB_Generator_v1;

import java.io.IOException;
import java.util.LinkedList;

import mip.pcre.pcre_v2.PCREPattern;
import nfa.NFA;
import nfa.NFAEdge;

/**
 *
 * @author heckarim
 */

/*
 *  BlockConRep is independent, just care:
 *      +, content op Constraint repetition operator.
 *      +, list of accept char for routing
 *      +, incoming and outcoming for routing.
 */
public class BlockConRep extends BlockState {
    // essential attribute of Conrep

    public int m // large repet value
            , n // smaller repetition value
            , g // g=0 if atleast, orther it = 1;
            , k;
    public int id;
    public String pattern;  // pcre pattern which belong to this operator.
    public String modifier;
    public String value;    // value of nfa edge
    // extra space for block char
    public LinkedList<BlockChar> lChar;
    public LinkedList<BlockState> lState;

    public BlockConRep(NFAEdge edge, ReEngine engine) {
        //common attribute
        this.comming = new LinkedList<BlockState>();
        this.going = new LinkedList<BlockState>();
        this.isConRep = true;
        this.isStart = this.isEnd = false;
        this.engine = engine;
        this.acceptChar = null;
        // brand new attribute
        this.value = edge.value;
        this.id = edge.id;
        this.modifier = edge.modifier;
        this.lChar = new LinkedList<BlockChar>();
        this.lState = new LinkedList<BlockState>();
        this.parseValue(edge);
        this.constructEngine();
    }

    public void parseValue(NFAEdge edge) {
        String s[] = edge.value.split(",");
        this.m = Integer.parseInt(s[1]);
        this.n = Integer.parseInt(s[0]);
        int max = Math.max(n, m);
        this.k = (int) (Math.floor(Math.log(max) / Math.log(2)) + 1);
        if (edge.id == Refer._op_atleast) {
            this.g = 0;
        } else {
            this.g = 1;
        }
        this.pattern = edge.value.replaceFirst(n + "," + m + ",", "");
        // extract list of Char from pattern and insert to lChar.
        System.out.println("CRB: parseValue: " + edge.value);
        System.out.println("CRB: parseValue, pattern, modifier: " + this.pattern + "," + this.modifier);
    }

    @Override
    public void printTest() {
        System.out.println("this is BlockConRep");
    }

    private void constructEngine() {
        String rule = "/" + this.pattern + "/" + this.modifier;
        RegexTree tTree = new RegexTree(rule);
        tTree.parseTree();
        NFA tnfa = new NFA();
        tnfa.buildNFA(tTree);
        tnfa.reduceRedundantState();
        ReEngine tengine = new ReEngine();
        tengine.buildEngine(tnfa);
        //copy listBlockChar of tengine to lChar.
        for (int i = 0; i < tengine.listBlockChar.size(); i++) {
            BlockChar bc = tengine.listBlockChar.get(i);
            BlockChar nbc = new BlockChar();
            nbc.engine = this.engine;
            nbc.id = bc.id;
            nbc.value = bc.value;
            nbc.value256 = bc.value256;
            nbc.lState.add(this);
            this.lChar.add(nbc);
        }
    }

    /**
     *
     * @param old   : to be replaced
     * @param mew   : replace
     */
    @Override
    public void replaceChar(BlockChar old, BlockChar mew) {
        if (this.lChar.indexOf(old) == -1) {
            System.out.println("BlockConstraint: Error replace char ");
            return;
        }
        this.lChar.remove(old);
        if (this.lChar.indexOf(mew) == -1) {
            this.lChar.add(mew);
        }
        mew.addState(this);
    }

    @Override
    public void print() {
        System.out.println("BlockConrep engine: " + this.engine.order + " - oder: " + order);

        // accept char:
        System.out.print("Acceptchar : ");
        for (int i = 0; i < this.lChar.size(); i++) {
            BlockChar ch = this.lChar.get(i);
            System.out.println("\t id: " + ch.id + " - order " + ch.order + " - " + ch.value);
        }
        System.out.print("\n");

        //comming state:
        if (comming != null) {
            System.out.print("Coming : ");
            for (int i = 0; i < this.comming.size(); i++) {
                System.out.print(this.comming.get(i).order + " - ");
            }
            System.out.print("\n");
        }
        // going state:
        if (this.going != null) {
            System.out.print("\nGoing : ");
            for (int i = 0; i < this.going.size(); i++) {
                System.out.print(this.going.get(i).order + " - ");
            }
            System.out.print("\n");
        }
    }
}
