/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEngine;

import ParseTree.ParseTree;
import PCRE.Refer;
import ParseTree.RegexTree;
import hdl_generator.HDL_CRB_Generator_v1;

import java.io.IOException;
import java.util.LinkedList;

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
    public String pattern;  // pcre pattern which belong to this operator.
    public String value;    // value of nfa edge
    // extra space for block char
    public LinkedList<BlockChar> lChar;

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
        this.lChar = new LinkedList<BlockChar>();
        this.parseValue(edge);
        //need to insert lChar to list block char of engine
        this.engine.listBlockChar.addAll(this.lChar);
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
        String rule = this.pattern;
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
            nbc.lState.add(this);
            this.lChar.add(nbc);
        }
    }

    @Override
    public void printTest() {
        System.out.println("this is BlockConRep");
    }

}
