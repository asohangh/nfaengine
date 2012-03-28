package PCREv2;

import ParseTree.Node;
import java.io.File;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Refer {
    // Constant

    public static final char _char_and = (char) 176;
    public static final int _char = 0;			//
    public static final int _class = 1;			// [...]
    public static final int _neg_class = 2;			// [^...]
    public static final int _class_range = 3;         // [:\-a-zA-z]
    public static final int _neg_class_range = 4;         // [^a-d]
    public static final int _class_digit = 5;			// \d
    public static final int _class_word = 6; 			// \w
    public static final int _class_dot = 7;          // .
    public static final int _ascii_hex = 8;			// \xFF
    public static final int _char_start = 9;         // ^
    public static final int _char_end = 10;         // $
    public static final int _op_and = 11;			// Operator and
    public static final int _op_or = 12; 			// Operator or	|
    public static final int _op_star = 13;			// Operator repetition 	*
    public static final int _op_plus = 14;			//		...				+
    public static final int _op_ques = 15;			//		...				?
    public static final int _op_constraint = 16;			//  Contraint Repetiton
    public static final int _op_atleast = 17;			//	Contraint Repetiton {n,}
    public static final int _op_between = 18;			// 		...				{n,x}
    public static final int _op_exactly = 19;			//		...				{n}
    public static final int _op_parent = 20; 		//	Parenthesis 		()
    public static final int _op_backrefer = 21;			//  BackReference		\1, \2, ...\n
    public static final int _op_atMost = 22;
    public static final int _class_notspace = 23;
    public static final int _prefix = 24;
    public static final int _infix = 25;
    public static final String[] convert = {"Char", "Class", "NegC", "CRange", "NegCRange",
        "CDigit", "CWord", "CDot", "Hex", "START", "END",
        "AND", "OR", "STAR", "PLUS", "QUES", "CONTR",
        "ATLEA", "BETW", "EXACT", "PAREN", "BACKRE", "ATMOS", "NotSpace",
        "Prefix", "Infix"};
    //define mode for process constraint repetion opeartion
    public static final int _mode_with_CR = 0;
    public static final int _mode_no_CR = 1;

    /**
     * Get the index of the end of block String which indicated by bmark and emark.
     * @param s			:   Sring, must begin with bmark;
     * @param bmark		:   begin mark;
     * @param emark		:   end   mark;
     * @return			:   index of the end of block String
     *                      0 if s == null
     * note: 	- BackSlash \
     * 			- Block in Block problem
     * example:  s = "(a(b\)c)d)"   bmark = '/'     emark = ')'
     *          return: 9
     */
    public static int getIndexOBlock(String s, char bmark, char emark) {

        if (s == null || s.length() < 2 || s.charAt(0) != bmark) {
            return 0;
        }

        int count = 1;
        int i;
        for (i = 1; i < s.length(); i++) {
            char chr = s.charAt(i);
            if (chr == emark && s.charAt(i - 1) != '\\') {
                count--;
                if (count == 0) {
                    break;
                }
            } else if (chr == bmark && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return i;
    }

    public static Node processContraint(Node node) {
        Node temp = new Node(null, 0);
        String s = node.value.substring(1, node.value.length() - 1);
        //process inside pattern;
        System.out.println("Refer processConstraint:  " + node.id + " " + node.value);
        String pattern = node.getElement().toString();
        System.out.println("patter: " + pattern);
        if (s.indexOf(",") == -1) {
            temp.id = Refer._op_exactly;
            temp.value = s + "," + s + "," + pattern;
        } else {
            if (s.startsWith(",")) {
                temp.id = Refer._op_atMost;
                String t1 = s.substring(1);
                temp.value = t1 + "," + t1 + "," + pattern;
            } else {
                String[] t = s.split(",");
                if (t.length == 1) {

                    temp.id = Refer._op_atleast;
                    temp.value = t[0] + "," + t[0] + "," + pattern;

                } else {
                    temp.id = Refer._op_between;
                    temp.value = t[0] + "," + t[1] + "," + pattern;
                }
            }
        }
        return temp;
    }
    /**
     * This fucntion is for counting character
     * @param node
     * @return
     */
    public static int getContraintSize(Element node) {
        String s = node.value.substring(1, node.value.length() - 1);
        int ret=0;
        if (s.indexOf(",") == -1) {
            ret = Integer.parseInt(s);
        } else {
            if (s.startsWith(",")) {
                String t1 = s.substring(1);
                ret = Integer.parseInt(t1);
            } else {
                String[] t = s.split(",");
                if (t.length == 1) {

                    ret = Integer.parseInt(t[0]);

                } else {
                    ret = Integer.parseInt(t[1]);
                }
            }
        }
        return ret;
    }

    private static String getPattern(Node node) {
        LinkedList<Node> inorder = new LinkedList<Node>();
        String ret = "";
        Refer.inorder(node, inorder);
        System.out.println("Refer: getPatternNode : ");
        for (int i = 0; i < inorder.size(); i++) {
            System.out.print(inorder.get(i).id + " - " + inorder.get(i).value);
            if (inorder.get(i).getElement() == null) {
                System.out.println("wrong");
            }
            //ret += inorder.get(i).getElement().toString();
        }
        System.out.println("\n " + ret);
        return ret;
    }

    private static void inorder(Node node, LinkedList<Node> list) {
        if (node.left != null) {
            Refer.inorder(node.left, list);
        }
        list.add(node);
        if (node.right != null) {
            Refer.inorder(node.right, list);
        }
    }

    public boolean createFloder(String path, Document doc) {
        boolean success = false;
        File folder = new File(path);
        if (folder.exists()) {
            PCREv2.Refer.println("Create folder: " + path + " existed", doc);
        } else {
            success = (new File(path)).mkdirs();
        }
        if (success) {
            PCREv2.Refer.println("Create folder: " + path + " is created", doc);
        }
        return success;
    }

    public static boolean isOperator(int id) {
        return (id >= 11 && id <= 21);
    }

    public static String fixFolderPath(String path) {
        if (path.indexOf("\\") != -1) { // he thong file window
            if (!path.endsWith("\\")) {
                return path + "\\";
            }
        } else if (path.indexOf("/") != -1) { // he thong file linux
            if (!path.endsWith("/")) {
                return path + "/";
            }
        }
        return null;
    }

    public static void println(String s) {
        Refer.println(s, null);
    }

    public static void print(String s) {
        Refer.print(s, null);
    }

    public static void println(String s, Document doc) {
        if (null == doc) {
            System.out.println(s);
        } else {
            try {
                System.out.println(s);
                doc.insertString(doc.getLength(), s + "\n", null);
            } catch (BadLocationException ex) {
                Logger.getLogger(Refer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void print(String s, Document doc) {
        if (null == doc) {
            System.out.print(s);
        } else {
            try {
                System.out.print(s);
                doc.insertString(doc.getLength(), s, null);
            } catch (BadLocationException ex) {
                Logger.getLogger(Refer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *  conver "value" to 256 bit.
     */
    public static boolean[] convertTo256(int id, String value, String modifier) {
        boolean[] ret = null;
        // System.out.println("fillEntry NFAEdge");
        //System.out.println("id: " +  id + " value " +  value + " modifier " +  modifier);
        int hexValue = -1;
        switch (id) {
            case Refer._char:
                //just single char
                ret = fillSingle(value, modifier);
                break;
            case Refer._class:
                //this is class char
                ret = fillCharClass(value, modifier);
                break;
            case Refer._neg_class:
                ret = fillNegCharClass(value, modifier);
                break;
            case Refer._ascii_hex:
                // \xFF
                hexValue = Integer.parseInt(value.substring(2), 16);
                ret = fillHex(hexValue);
                break;
            case Refer._class_digit:
                // \d
                ret = fillDigitRange();
                break;
            case Refer._class_dot:
                // .
                ret = fillDotClass(modifier);
                break;

            /*case Refer._char_start:
            case Refer._char_end:
            //FIXME
            hexValue = 10;
            buildHex(hexValue);
            break;*/
            case Refer._class_notspace:
                ret = fillCharRangeNoSpace();
            case Refer._class_word:
                // \w
                ret = fillCharRange();
                break;

            case Refer._char_start:
                if (modifier.indexOf('m') != -1) {
                    ret = fillHex(10);
                } else {
                    ret = fillall0();
                }
                break;
            case Refer._char_end:
                ret = fillall0();
                break;
            case Refer._op_between:
            case Refer._op_atleast:
            case Refer._op_exactly:
                break;

            default:
                ret = fillSingle(value.substring(0), modifier);
                break;

        }
        return ret;
    }

    private static boolean[] fillall0() {
        boolean[] value256 = new boolean[256];
        for (int i = 0; i < 256; i++) {
            value256[i] = false;
        }
        return value256;
    }

    private static boolean[] fillSingle(String value, String modifier) {
        //throw new UnsupportedOperationException("Not yet implemented");
        boolean[] value256 = new boolean[256];
        int ascii = (int) value.charAt(0);

        if (modifier.contains("i")) {
            if (ascii >= 65 && ascii <= 90) {
                value256[ascii] = true;
                value256[ascii + 32] = true;
            } else if (ascii >= 97 && ascii <= 122) {
                value256[ascii] = true;
                value256[ascii - 32] = true;
            } else // not letter
            {
                value256[ascii] = true;
            }
        } else { // case sensitive
            value256[ascii] = true;
        }
        return value256;
    }

    private static boolean[] fillCharClass(String value, String modifier) {

        // value = [abc]
        boolean[] value256 = new boolean[256];
        int from = 0, to = 0;
        value = value.substring(1, value.length() - 1);
        for (int i = 0; i < value.length(); i++) {
            int hex;
            if (value.charAt(i) == '\\') {
                switch (value.charAt(i + 1)) {
                    case 'x':
                    case 'X':
                        hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                        value256[hex] = true;
                        i = i + 3;
                        break;
                    case 'd':
                        for (int j = 48; j <= 57; j++) {
                            value256[j] = true;
                        }
                        i++;
                        break;
                    case 'w':
                        //TODO
                        i++;
                        break;
                    case 's': // white space \x20 = 32
                        value256[32] = true;
                        i++;
                        break;
                    case 'n': // LF \x0A
                        value256[10] = true;
                        i++;
                        break;
                    case 'r': // CR \x0D
                        value256[14] = true;
                        i++;
                        break;
                    case 't': // tab \x09
                        value256[9] = true;
                        i++;
                        break;
                    case 'z': // range
                        from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                        to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                        if (modifier.contains("i")) {
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
                                    value256[j] = true;
                                }
                                for (int j = from1; j <= to1; j++) {
                                    value256[j] = true;
                                }
                            } else {// not letter
                                for (int j = from; j <= to; j++) {
                                    value256[j] = true;
                                }
                            }
                        } else { // case sensitive
                            for (int j = from; j <= to; j++) {
                                value256[j] = true;
                            }
                        }
                        i = i + 3;
                        break;
                    default: // \?
                        hex = (int) value.charAt(i + 1);
                        value256[hex] = true;
                        i++;
                        break;

                }
            } else {
                //value.charAt(i) != '\\' ex: a
                value256[(int) value.charAt(i)] = true;

            }
        }
        return value256;
    }

    /* this finction to fill \w*/
    private static boolean[] fillCharRange() {
        boolean[] value256 = new boolean[256];
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
        // 65 - 90 [A - Z]
        // 97 - 122 [a - z]
        for (int i = 0; i <= 47; i++) {
            value256[i] = false;
        }
        for (int i = 48; i <= 57; i++) {
            value256[i] = true;
        }
        for (int i = 58; i <= 64; i++) {
            value256[i] = false;
        }
        for (int i = 65; i <= 90; i++) {
            value256[i] = true;
        }
        for (int i = 91; i <= 96; i++) {
            value256[i] = false;
        }
        for (int i = 97; i <= 122; i++) {
            value256[i] = true;
        }
        for (int i = 123; i <= 255; i++) {
            value256[i] = false;
        }
        return value256;
    }

    private static boolean[] fillCharRangeNoSpace() {
        boolean[] value256 = new boolean[256];
        for (int i = 0; i <= 255; i++) {
            value256[i] = true;
        }
        char ch = ' ';
        value256[(int) ch] = false;
        return value256;
    }

    private static boolean[] fillDigitRange() {
        boolean[] value256 = new boolean[256];
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
        for (int i = 0; i <= 47; i++) {
            value256[i] = false;
        }
        for (int i = 48; i <= 57; i++) {
            value256[i] = true;
        }
        for (int i = 58; i <= 255; i++) {
            value256[i] = false;
        }
        return value256;
    }

    private static boolean[] fillNegCharClass(String value, String modifier) {
        boolean[] value256 = new boolean[256];
        //initialize
        for (int i = 0; i < 256; i++) {
            value256[i] = true;
        }
        // value = [^abc]
        int from = 0, to = 0;
        //System.out.println("value: " + value);
        value = value.substring(2, value.length() - 1);
        //System.out.println("value after: " + value);
        for (int i = 0; i < value.length(); i++) {
            int hex;
            if (value.charAt(i) == '\\') {
                switch (value.charAt(i + 1)) {
                    case 'x':
                    case 'X':
                        hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                        value256[hex] = false;
                        i = i + 3;
                        break;
                    case 'd':
                        for (int j = 48; j <= 57; j++) {
                            value256[j] = false;
                        }
                        i++;
                        break;
                    case 'w':
                        //TODO
                        i++;
                        break;
                    case 's': // white space \x20 = 32
                        value256[32] = false;
                        i++;
                        break;
                    case 'n': // LF \x0A
                        value256[10] = false;
                        i++;
                        break;
                    case 'r': // CR \x0D
                        value256[14] = false;
                        i++;
                        break;
                    case 't': // tab \x09
                        value256[9] = false;
                        i++;
                        break;
                    case 'z': // range
                        from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                        to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                        if (modifier.contains("i")) {
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
                                    value256[j] = false;
                                }
                                for (int j = from1; j <= to1; j++) {
                                    value256[j] = false;
                                }
                            } else {// not letter
                                for (int j = from; j <= to; j++) {
                                    value256[j] = false;
                                }
                            }
                        } else { // case sensitive
                            for (int j = from; j <= to; j++) {
                                value256[j] = false;
                            }
                        }
                        i = i + 3;
                        break;
                    default: // \?
                        value256[(int) value.charAt(i + 1)] = false;
                        i++;
                        break;

                }
            } else {
                //value.charAt(i) != '\\' ex: a
                value256[(int) value.charAt(i)] = false;

            }
        }
        return value256;
    }

    private static boolean[] fillHex(int hexValue) {
        boolean[] value256 = new boolean[256];
        //throw new UnsupportedOperationException("Not yet implemented");
        for (int i = 0; i < 256; i++) {
            if (i != hexValue) {
                value256[i] = false;
            } else {
                value256[i] = true;
            }
        }
        return value256;
    }

    private static boolean[] fillDotClass(String modifier) {
        boolean[] value256 = new boolean[256];
        for (int i = 0; i < 256; i++) {
            if (!modifier.contains("s")) {
                //match all except '\n'
                if (i == 10) {
                    value256[i] = false;
                } else {
                    value256[i] = true;
                }
            } else {
                value256[i] = true;
            }

        }
        return value256;
    }
}
