/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.parsetree_v2;

import mip.pcre.pcre_v2.Element;
import mip.pcre.pcre_v2.PCREPattern;
import mip.pcre.pcre_v2.Refer;


/*
 * 	Author: Heckarim, version 1
 */

/*
 * 			Chaim Frenkel: "Perl's grammar can not be reduced to BNF.
 * 				The work of parsing perl is distributed between yacc, the lexer, smoke and mirrors."
 */

/* The BNF will be apply:
 *
<pcre>     ::= concat | (concat '|' pcre)
<concat>   ::= rep | (rep '°' concat)
<rep>      ::= atom | atom '*' | atom '?' | atom '+' | conrep
<atom>     ::= char | '(' pcre ')'
<conrep>   ::= atom '{ }'
<char>     ::= alphanumeric | class character "[]" | escape char
 */

/*
 * Supported::
 * 			Operator: *, and, +, ?, {}, |, ()
 * 			Leaf:    a, ., [], \xFF, \d, \w, \s, \n, \r, \t
 * 			Modifier: smi
 *                      Operator: backreference, ^, $, ...
 * 			Leaf: \000 ...
 * NotSupport::
 *                      assertion
 * 			
 */
public class ParseTree {

    private PCREPattern rule;
    private int index = 0;

    public ParseTree(PCREPattern other) {
        this.rule = other;
    }
    //Building Nodev2 Function nest.

    public Node buildLeafNode(Element e) {
        Node re = new Node(e);
        return re;
    }

    public Node buildOrNode(Node node1, Node node2) {
        Node re = new Node("" + '|', Refer._op_or);
        re.left = node1;
        re.right = node2;
        return re;
    }

    public Node buildAndNode(Node node1, Node node2) {
        Node re = new Node("" + Refer._char_and, Refer._op_and);
        re.left = node1;
        re.right = node2;
        return re;
    }

    public Node buildRepNode(Node temp, Element e) {
        Node re = new Node(e);
        re.left = temp;
        return re;
    }

    /**
     * BNF:     pcre    ::= concat | (concat '|' expr)
     * @param rule
     * @return
     */
    public Node parsePcre() {
        Node temp = parseConcat(rule);
        if (temp == null) {
            PCRE.Refer.println("Something wrong, parse Pcre", null);
            return null;
        }
        while (rule.isNotEnd(index) && rule.getElement(index).isOptional) {
            index++;
            Node othernode = parseConcat(rule);
            temp = this.buildOrNode(temp, othernode);
        }
        return temp;
    }

    /**
     * BNF:     concat	::= rep | (rep '\176' concat)
     * @param rule
     * @return
     */
    public Node parseConcat(PCREPattern rule) {
        Node temp = parseRep(rule);
        if (temp == null) {
            PCRE.Refer.println("Somethig wrong, parseConcat", null);
            return null;
        }
        while (rule.isNotEnd(index) && rule.getElement(index).isConcat) {
            index++;
            Node othernode = parseRep(rule);
            temp = this.buildAndNode(temp, othernode);
        }
        return temp;
    }

    /**
     * BNF:     rep		::= atom '*' | atom '?' | atome '+' | atom '{...}'
     * @param rule
     * @return
     */
    public Node parseRep(PCREPattern rule) {
        Node temp = parseAtom(rule);
        if (temp == null) {
            System.out.println("Somethingworng rep");
            return null;
        }
        while (rule.isNotEnd(index) && rule.getElement(index).isRepetition) {
            Element e = rule.getElement(index);
            if(e.id == Refer._op_constraint){
                temp = buildRepCRNode(temp,e, rule.getElement(index-1));
            }
            else{
            temp = buildRepNode(temp, rule.getElement(index));
            }
            
            index++;
        }
        return temp;
    }

    /**
     * BNF:     atom    ::= char | '(' expr ')'
     * @param rule
     * @return
     */
    public Node parseAtom(PCREPattern rule) {
        Node temp = null;
        if (rule.isNotEnd(index)) {
            Element e = rule.getElement(index);
            if (e.isParentheis) {
                PCREPattern other = new PCREPattern(e);
                ParseTree parsetree = new ParseTree(other);
                temp = parsetree.parsePcre();
                index++;
            } else if (e.isAtom) {
                temp = buildLeafNode(e);
                index++;
            }
        }
        return temp;
    }

    private Node buildRepCRNode(Node temp, Element e1, Element e2) {
        Node re = new Node(e1,e2);
        re.left = temp;
        return re;
    }
}
