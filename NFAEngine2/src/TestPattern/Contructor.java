/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPattern;

import PCRE.Refer;
import ParseTree.Node;
import ParseTree.ParseTree;
import java.util.Random;

/**
 *
 * @author heckarim
 */
public class Contructor {

    ParseTree pTree;
    boolean match = true;
    Random rand;

    public Contructor(ParseTree pTree) {
        this.pTree = pTree;
        this.rand = new Random();
    }

    /**
     * note:
     *      match isn't true -> create pattern which not match
     * @param miss
     * @return
     */
    public Pattern BuildPattern(boolean match) {
        Pattern ret;
        this.match = match;
        String pat = this.buildNode(pTree.root);
        if (match) {
        } else {
            //todo: currently, strangh forward strategy: insert random character at some position.
            this.makeMissMatch(pat);
        }

        ret = new Pattern(pat);
        ret.isMatch = match;
        return ret;
    }

    public String buildTestString(boolean match) {
        this.match = match;
        return this.buildNode(pTree.root);
    }

    //Sample for building  test pattern.
    public String sample(Node root) {
        String ptern = "";
        if (root == null) {
            return "";
        }

        ptern = ptern.concat(sample(root.left));

        if (root.id == Refer._op_plus || root.id == Refer._op_star) {

            int c = rand.nextInt(10);
            for (int i = 0; i < c; i++) {
                ptern = ptern.concat(sample(root.left));
            }
        }

        if (root.left == null && root.right == null) {
            //ptern = ptern.concat(root.value);
            System.out.println(root.value + " " + root.id);

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
                    if (!this.pTree.rule.getModifier().contains("s")) {
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
                                    if (this.pTree.rule.getModifier().contains("i")) {
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
                                    if (this.pTree.rule.getModifier().contains("i")) {
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
                    System.out.println("BRam[c]: " + BRam[c]);
                    ptern = ptern + (char) c;

                }
                break;
                default:
                    break;
            }
        }

        ptern = ptern.concat(sample(root.right));

        return ptern;
    }

    /**
     *
     * @param root
     * @return
     */
    public char buildLeaf(Node root) {
        char ret;
        //String temp = root.value;
        //String ptern = null;
        char chr;
        switch (root.id) {
            case Refer._char:
                chr = root.value.charAt(0);
                ret = variousCharCase(chr);
                break;
            case Refer._ascii_hex:
                if (root.value.length() <= 4) {
                    chr = (char) Integer.parseInt(root.value.substring(2), 16);
                    ret = variousCharCase(chr);
                } else {
                    ret = '\0';//todo
                }
                break;
            case Refer._class_digit: {
                int c = rand.nextInt(10);
                c = '0' + c;
                ret = (char) c;
            }
            break;
            case Refer._class_word: {
                // cai nay lam an gian ti ^^
                int c = rand.nextInt(27);
                c = 'a' + c;
                chr = (char) c;
                if (rand.nextBoolean()) {
                    chr = Character.toLowerCase(chr);
                } else {
                    chr = Character.toUpperCase(chr);
                }
                ret = chr;
            }
            break;

            case Refer._class_dot: {
                int c = rand.nextInt(256);
                if (!this.pTree.rule.getModifier().contains("s")) {
                    while (((char) c) == '\n') {
                        c = rand.nextInt(256);
                    }
                }
                ret = (char) c;
            }
            break;

            case Refer._class: { //[\z617a\z415a\z3039]
                //Todo
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
                                if (this.pTree.rule.getModifier().contains("i")) {
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
                ret = (char) c;
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
                                if (this.pTree.rule.getModifier().contains("i")) {
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
                System.out.println("BRam[c]: " + BRam[c]);
                ret = (char) c;

            }
            break;
            default:
                ret = '\0';
                break;
        }

        return ret;

    }

    private char variousCharCase(char chr) {
        if (this.isCaseSensitive()) {
            if (rand.nextBoolean()) {
                chr = Character.toLowerCase(chr);
            } else {
                chr = Character.toUpperCase(chr);
            }
        }
        return chr;
    }

    private boolean isCaseSensitive() {
        if (this.pTree.rule.getModifier().contains("i")) {
            return true;
        } else {
            return false;
        }
    }

    private String buildNode(Node root) {
        String s = "";
        //if node is null so EMPTY String is return.
        if (root == null) {
            return s;
        }
        if (root.left == null && root.right == null) {
            // leaf
            s = s + this.buildLeaf(root);
            return s;
        }
        //if not a leaf so:
        switch (root.id) {
            case Refer._op_and:
                s = this.buildOpAnd(root);
                break;
            case Refer._op_or:
                s = this.buildOpOr(root);
                break;
            case Refer._op_star:
                s = this.buildOpStar(root);
                break;
            case Refer._op_ques:
                s = this.buildOpQues(root);
                break;
            case Refer._op_plus:
                s = this.buildOpPlus(root);
                break;
            case Refer._op_constraint:
                s = this.buildConstraint(root);
                break;
            default:
                //todo
                break;
        }
        return s;
    }

    private String buildOpAnd(Node root) {
        //todo missmatch
        String s1 = this.buildNode(root.left);
        String s2 = this.buildNode(root.right);
        return s1 + s2;
    }

    private String buildOpOr(Node root) {
        String s1 = this.buildNode(root.left);
        String s2 = this.buildNode(root.right);
        if (rand.nextBoolean()) {
            return s1;
        } else {
            return s2;
        }
    }

    private String buildOpStar(Node root) {
        String s;
        if (root.left != null) {
            s = this.buildNode(root.left);
        } else {
            s = this.buildNode(root.right);
        }
        int r = rand.nextInt(4);
        String ret = "";
        for (int i = 0; i < r; i++) {
            ret = ret + s;
        }
        return ret;
    }

    private String buildOpQues(Node root) {
        String s;
        if (root.left != null) {
            s = this.buildNode(root.left);
        } else {
            s = this.buildNode(root.right);
        }

        if (rand.nextBoolean()) {
            return s;
        } else {
            return "";
        }
    }

    private String buildOpPlus(Node root) {
        String s;
        if (root.left != null) {
            s = this.buildNode(root.left);
        } else {
            s = this.buildNode(root.right);
        }
        int r = rand.nextInt(4);
        String ret = "";
        //Different with start i<=r :)).
        for (int i = 0; i <= r; i++) {
            ret = ret + s;
        }
        return ret;
    }

    private boolean isOperatorStart() {
        if (this.pTree.rule.getModifier().contains("t")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * insert char to mid postition
     * @param pat
     */
    private String makeMissMatch(String pat) {
        int index = rand.nextInt(pat.length() - 1);
        char c = (char) rand.nextInt(256);
        return pat.substring(0, index + 1) + c + pat.substring(index + 1);
    }

    /**
     * 
     * @param root
     * @return
     * 
     * note:
     *      +, currently, dont' support cons inside cons
     */
    private String buildConstraint(Node root) {
        //pcre: /abc/{5,7}
        //value = {5,7};
        int id;
        int m=0,n=0; //[m,n] or [n,] or [,n] or [n]

        //Parse type of "Constraint
        String s = root.value.substring(1, root.value.length() - 1);
        if (s.indexOf(",") == -1) {
            id = Refer._op_exactly;
            n = Integer.parseInt(s);
        } else {
            if (s.startsWith(",")) {
                id = Refer._op_atMost;
                String t1 = s.substring(1);
                n = Integer.parseInt(t1);
            } else {
                String[] t = s.split(",");
                if (t.length == 1) {
                    id = Refer._op_atleast;
                   n = Integer.parseInt(t[0]);

                } else {
                    id = Refer._op_between;
                    m = Integer.parseInt(t[0]);
                    n = Integer.parseInt(t[1]);
                }
            }
        }
        //Generate repetition pcre
        Contructor con = new Contructor(new ParseTree(root.left.value));
        Pattern pt = con.BuildPattern(true);

        //Base on type of constraint make a repetition
        s = "";
        switch (id){
            case Refer._op_atMost: // 1->n
                int r = rand.nextInt(n);
                for(int i=0; i<(r); i++)
                    s+=pt.data;
                break;
            case Refer._op_atleast: // n -> ..
                r = rand.nextInt(4);
                for(int i=0; i<(n+r); i++)
                    s+=pt.data;
                break;
            case Refer._op_between: // m-> n
                r = rand.nextInt(n-m);
                for(int i=0; i<(m+r); i++)
                    s+=pt.data;
                break;
            case Refer._op_exactly:// n
                for(int i=0; i<n; i++)
                    s+=pt.data;
                break;
            default:
                break;
        }

        return s;
    }
}
