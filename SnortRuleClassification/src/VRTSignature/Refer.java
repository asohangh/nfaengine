package VRTSignature;


import java.io.File;
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
    public static final String[] convert = {"Char", "Class", "NegC", "CRange", "NegCRange",
        "CDigit", "CWord", "CDot", "Hex", "START", "END",
        "AND", "OR", "STAR", "PLUS", "QUES", "CONTR",
        "ATLEA", "BETW", "EXACT", "PAREN", "BACKRE", "ATMOS"};

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

    public static boolean isHaveConstraint(String pe) {
        boolean ret = false;
        for(int i=1; i<pe.length(); i++){
            char ch = pe.charAt(i);
            if(ch == '{')
                if(pe.charAt(i-1) != '\\'){
                    ret = true;
                    break;
                }
        }
        return ret;
    }
}
