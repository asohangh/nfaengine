package ParseTree;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.text.Document;
import PCRE.Element;
import PCRE.PcreRule;
import PCRE.Refer;

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
 * NotSupport::
 * 			Operator: backreference, ^, $, ...
 * 			Leaf: \000 ...
 */
public class ParseTree {

    public Node root;			// Root of the tree
    public PcreRule rule;                   // DataStructure for Pcre rule.
    public static String _default_folder = System.getProperty("user.dir") + System.getProperty("file.separator");
    public static String _default_file_name = "parsetree.dot";

    //Building Node Function nest.
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

    //Parse tree function nest.
    public ParseTree(String rule) {
        this.rule = new PcreRule(rule);
        this.root = parsePcre(this.rule);

        this.rule.testPartten = patternOfPCRE(this.root); // set test pattern
    }

    /**
     * BNF:     pcre    ::= concat | (concat '|' expr)
     * @param rule
     * @return
     */
    public Node parsePcre(PcreRule rule) {
        Node temp = parseConcat(rule);
        if (temp == null) {
            PCRE.Refer.println("Something wrong, parse Pcre", null);
            return null;
        }
        while (rule.isNotEnd() && rule.getElement().isOpt) {
            rule.nextElement();
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
    public Node parseConcat(PcreRule rule) {
        Node temp = parseRep(rule);
        if (temp == null) {
            PCRE.Refer.println("Somethig wrong, parseConcat", null);
            return null;
        }
        while (rule.isNotEnd() && rule.getElement().isConcat) {
            rule.nextElement();
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
    public Node parseRep(PcreRule rule) {
        Node temp = parseAtom(rule);
        if (temp == null) {
            System.out.println("Somethingworng rep");
            return null;
        }
        while (rule.isNotEnd() && rule.getElement().isRep) {
            temp = buildRepNode(temp, rule.getElement());
            rule.nextElement();
        }
        return temp;
    }

    /**
     * BNF:     atom    ::= char | '(' expr ')'
     * @param rule
     * @return
     */
    public Node parseAtom(PcreRule rule) {
        Node temp = null;
        if (rule.isNotEnd() && rule.getElement().isAtom) {
            if (rule.getElement().id != Refer._op_parent) {
                Element eTemp = rule.getNextElement();
                if (eTemp != null && eTemp.id == Refer._op_constraint) {
                    Element ele = rule.getElement();
                    ele.value = "/" + ele.value + "/" + this.rule.getModifier();
                    temp = buildLeafNode(ele);
                    rule.nextElement();
                } else {
                    temp = buildLeafNode(rule.getElement());
                    rule.nextElement();
                }
            } else if (rule.getNextElement() != null && rule.getNextElement().id == Refer._op_constraint) {
                Element ele = rule.getElement();
                ele.value = "/" + ele.value + "/" + this.rule.getModifier();
                temp = buildLeafNode(ele);
                rule.nextElement();
            } else {
                PcreRule other = new PcreRule();
                other.setPattern(rule.getElement().value);
                temp = parsePcre(other);
                rule.nextElement();
            }
        }
        return temp;
    }

    /*
     *      PRINT TREE FUNCTION NEST.
     */
    public void printTree() {
        PCRE.Refer.println("Print Tree: ", null);
        this.printTreeRecursive(null, root, 0);
    }

    public void printTree(Document doc) {
        PCRE.Refer.println("Print Tree: ", doc);
        this.printTreeRecursive(doc, root, 0);
    }

    public void printTreeRecursive(Document doc, Node root, int tab) {
        if (root == null) {
            return;
        }
        for (int i = 0; i < tab; i++) {
            PCRE.Refer.print("    ", doc);
        }

        PCRE.Refer.print(tab + ".", doc);
        PCRE.Refer.println(root.value + "," + Refer.convert[root.id], doc);

        if (root.left == null && root.right == null) {
            return;
        }
        if (root.left != null) {
            this.printTreeRecursive(doc, root.left, tab + 1);
        }
        if (root.right != null) {
            this.printTreeRecursive(doc, root.right, tab + 1);
        }
    }

    public void generateDotFile(String name, String folder) {
        BufferedWriter bw = null;
        try {
            if (null == folder || folder.isEmpty()) {
                folder = _default_folder;
            }
            if (null == name || name.isEmpty()) {
                name = _default_file_name;
            }
            bw = new BufferedWriter(new FileWriter(folder + name));
            bw.write("digraph \"parse Tree\" {"
                    + "\ngraph [ranksep=.2,rankdir=TD];"
                    + "\nnode [shape=circle,fontname=Arial,fontsize=14];"
                    + "\nnode [width=1,fixedsize=true];"
                    + "\nedge [fontname=Arial,fontsize=14];"
                    + "\n-1 [width=0.2,shape=point color=red];"
                    + "\n-1 -> 0 [ color=red];");
            this.generateDotFile_recursive(this.root, bw, 0);
            bw.write("\n}\n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private int generateDotFile_recursive(Node root, BufferedWriter bw, int order) throws IOException {
        if (root == null) {
            return order;
        }
        String colornode = "blue";
        String coloredge = "green";
        if (Refer.isOperator(root.id)) {
            colornode = "red";
        }
        int neworder = order + 1;
        if (Refer.isOperator(root.id)) {
            bw.write("\n" + order + " [label=\"[" + Refer.convert[root.id] + "]\" color=" + colornode + "];");
        } else {
            bw.write("\n" + order + " [label=\"" + root.value + "\" color=" + colornode + "];");
        }

        if (root.left == null && root.right == null) {
            return neworder;
        }

        if (root.left != null) {
            bw.write("\n" + order + " -> " + neworder + " [color=" + coloredge + "];");
            neworder = this.generateDotFile_recursive(root.left, bw, neworder);
        }
        if (root.right != null) {
            bw.write("\n" + order + " -> " + neworder + " [color=" + coloredge + "];");
            neworder = this.generateDotFile_recursive(root.right, bw, neworder);
        }

        return neworder;
    }

    public String patternOfPCRE(Node root) {
        String ptern = "";
        if (root == null) {
            return "";
        }

        ptern = ptern.concat(patternOfPCRE(root.left));

        if (root.id == Refer._op_plus || root.id == Refer._op_star) {
            Random rand = new Random();
            int c = rand.nextInt(10);
            for (int i = 0; i < c; i++) {
                ptern = ptern.concat(patternOfPCRE(root.left));
            }
        }

        if (root.left == null && root.right == null) {
            //ptern = ptern.concat(root.value);
            //System.out.println(root.value +" "+  root.id);
            Random rand = new Random();
            //String temp = root.value;

            switch (root.id) {
                case Refer._char:
                    ptern = ptern.concat(root.value);
                    break;
                case Refer._ascii_hex: {
                    if (root.value.length() <= 4) {
                        char c = (char) Integer.parseInt(root.value.substring(2), 16);
                        ptern = ptern + c;
                    }
                }
                break;
                case Refer._class_digit: {

                    int c = rand.nextInt(10);
                    c = '0' + c;
                    ptern = ptern + (char) c;
                }
                break;
                case Refer._class_word: {
                    // cai nay lam an gian ti ^^
                    int c = rand.nextInt(27);
                    c = 'a' + c;
                    ptern = ptern + (char) c;
                }
                break;
                case Refer._class_dot: {
                    int c = rand.nextInt(256);
                    if (!this.rule.getModifier().contains("s")) {
                        while (((char) c) == '\n') {
                            c = rand.nextInt(256);
                        }
                    }
                    ptern = ptern + (char) c;
                }
                break;
                case Refer._class: { //[\z617a\z415a\z3039]
                    char[] BRam = new char[256];
                    for (int i = 0; i < 256; i++) {
                        BRam[i] = '0';
                    }
                    // value = [abc]
                    int from = 0, to = 0;
                    String value = root.value.substring(1, root.value.length() - 1);
                    for (int i = 0; i < value.length(); i++) {
                        int hex;
                        if (value.charAt(i) == '\\') {
                            switch (value.charAt(i + 1)) {
                                case 'x':
                                case 'X':
                                    hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                                    BRam[hex] = '1';
                                    i = i + 3;
                                    break;
                                case 'd':
                                    for (int j = 48; j <= 57; j++) {
                                        BRam[j] = '1';
                                    }
                                    i++;
                                    break;
                                case 'w':
                                    //TODO
                                    i++;
                                    break;
                                case 's': // white space \x20 = 32
                                    BRam[32] = '1';
                                    i++;
                                    break;
                                case 'n': // LF \x0A
                                    BRam[10] = '1';
                                    i++;
                                    break;
                                case 'r': // CR \x0D
                                    BRam[14] = '1';
                                    i++;
                                    break;
                                case 't': // tab \x09
                                    BRam[9] = '1';
                                    i++;
                                    break;
                                case 'z': // range
                                    from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                                    to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                                    if (this.rule.getModifier().contains("i")) {
                                        int from1 = 0, to1 = 0;
                                        if (from >= 65 && from <= 90) {
                                            from1 = from + 32;
                                        } else if (from >= 97 && from <= 122) {
                                            from1 = from - 32;
                                        }
                                        if (to >= 65 && to <= 90) {
                                            to1 = to + 32;
                                        } else if (from >= 97 && from <= 122) {
                                            to1 = to - 32;
                                        }
                                        if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
                                                || (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
                                            for (int j = from; j <= to; j++) {
                                                BRam[j] = '1';
                                            }
                                            for (int j = from1; j <= to1; j++) {
                                                BRam[j] = '1';
                                            }
                                        } else {// not letter
                                            for (int j = from; j <= to; j++) {
                                                BRam[j] = '1';
                                            }
                                        }
                                    } else { // case sensitive
                                        for (int j = from; j <= to; j++) {
                                            BRam[j] = '1';
                                        }
                                    }
                                    i = i + 3;
                                    break;
                                default: // \?
                                    hex = (int) value.charAt(i + 1);
                                    BRam[hex] = '1';
                                    i++;
                                    break;

                            }
                        } else {
                            //value.charAt(i) != '\\' ex: a
                            BRam[(int) value.charAt(i)] = '1';

                        }
                    }

                    int c = rand.nextInt(256);
                    while (BRam[c] == '0') {
                        c = rand.nextInt(256);
                    }
                    ptern = ptern + (char) c;

                } // Refer.class
                break;
                case Refer._neg_class: {
                    char[] BRam = new char[256];
                    for (int i = 0; i < 256; i++) {
                        BRam[i] = '1';
                    }
                    // value = [^abc]
                    int from = 0, to = 0;
                    String value = root.value.substring(2, root.value.length() - 1);
                    //System.out.println("value: " + value);
                    for (int i = 0; i < value.length(); i++) {
                        int hex;
                        if (value.charAt(i) == '\\') {
                            switch (value.charAt(i + 1)) {
                                case 'x':
                                case 'X':
                                    hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                                    BRam[hex] = '0';
                                    i = i + 3;
                                    break;
                                case 'd':
                                    for (int j = 48; j <= 57; j++) {
                                        BRam[j] = '0';
                                    }
                                    i++;
                                    break;
                                case 'w':
                                    //TODO
                                    i++;
                                    break;
                                case 's': // white space \x20 = 32
                                    BRam[32] = '0';
                                    i++;
                                    break;
                                case 'n': // LF \x0A
                                    BRam[10] = '0';
                                    i++;
                                    break;
                                case 'r': // CR \x0D
                                    BRam[14] = '0';
                                    i++;
                                    break;
                                case 't': // tab \x09
                                    BRam[9] = '0';
                                    i++;
                                    break;
                                case 'z': // range
                                    from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                                    to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                                    if (this.rule.getModifier().contains("i")) {
                                        int from1 = 0, to1 = 0;
                                        if (from >= 65 && from <= 90) {
                                            from1 = from + 32;
                                        } else if (from >= 97 && from <= 122) {
                                            from1 = from - 32;
                                        }
                                        if (to >= 65 && to <= 90) {
                                            to1 = to + 32;
                                        } else if (from >= 97 && from <= 122) {
                                            to1 = to - 32;
                                        }
                                        if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
                                                || (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
                                            for (int j = from; j <= to; j++) {
                                                BRam[j] = '0';
                                            }
                                            for (int j = from1; j <= to1; j++) {
                                                BRam[j] = '0';
                                            }
                                        } else {// not letter
                                            for (int j = from; j <= to; j++) {
                                                BRam[j] = '0';
                                            }
                                        }
                                    } else { // case sensitive
                                        for (int j = from; j <= to; j++) {
                                            BRam[j] = '0';
                                        }
                                    }
                                    i = i + 3;
                                    break;
                                default: // \?
                                    hex = (int) value.charAt(i + 1);
                                    BRam[hex] = '0';
                                    i++;
                                    break;

                            }
                        } else {
                            //value.charAt(i) != '\\' ex: a
                        }
                    }
                    //System.out.println(BRam[38]);
                    int c = rand.nextInt(256);
                    //System.out.println("C: " + c);
                    while (BRam[c] == '0') {
                        c = rand.nextInt(256);
                    }
                    //System.out.println("BRam[c]: " + BRam[c]);
                    ptern = ptern + (char) c;

                }
                break;
                default:
                    break;
            }
        }

        ptern = ptern.concat(patternOfPCRE(root.right));

        return ptern;
    }
}
