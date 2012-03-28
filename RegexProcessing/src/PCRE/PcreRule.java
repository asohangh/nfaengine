package PCRE;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 *
 * This class contain content of PCRE
 *
 * include dedicated method for parse tree;
 *
 */
public class PcreRule {

    // pcre:  /<pattern>/[<modifier>]
    private String regex;         //Content of pcreRule
    private String modifier;		//Modifier of pcreRule
    //
    private LinkedList<Element> elist = null;
    // index for element process
    private int index;				//index of current element;
    private int nextIndex;			//index of next element, set after call getElement
    public String testPartten;
    private String pcrerule;
    private boolean isSupportabe = true;

    public PcreRule() {
        regex = "";
        modifier = "";
        index = 0;
        nextIndex = 0;
        testPartten = "";
    }

    public PcreRule(String rule) {
        regex = "";
        modifier = "";
        index = 0;
        nextIndex = 0;
        this.pcrerule = rule;
        parsePcre(rule.trim());
        formatPcre();
    }

    public String getRule() {
        return pcrerule;
    }

    public void setPattern(String pattern) {
        this.regex = pattern;
    }

    public String getPattern() {
        return this.regex;
    }

    public String getModifier() {
        return this.modifier;
    }

    /**
     * Separate pcre rule into pattern and modifier.
     *
     * @param rule :    pcre rule
     *          format: /<pattern>/[<modifier>]
     * return:  true if success
     */
    public boolean parsePcre(String rule) {
        int i = Refer.getIndexOBlock(rule, '/', '/');
        if (i != 0) {
            this.regex = rule.substring(1, i);
            this.regex = this.regex.trim();

            if (i < rule.length() - 1) {
                modifier = rule.substring(i + 1);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * turn pcre pattern into formated model, easy to parse.
     * @return pattern after format;
     * note:
     *      - with '^' insert modifier 't'.  //todo: change to b,e.
     *      - with '$' insert modifier 'z'.
     *      - insert character of '\176' for concatenation.
     */
    private String formatPcre() {
        //This function just add char_and in to pcre pattern
        PCRE.Refer.println("Call format PCRE");
        PCRE.Refer.println("Before: " + this.regex);
        int i = 0;
        boolean cando = false;
        while (i < this.regex.length()) {
            char chr = this.regex.charAt(i);
            //System.out.println(chr);
            switch (chr) {
                case Refer._char_and:
                case '|':
                    cando = false;
                    i++;
                    break;
                case '*':
                case '+':
                case '?':
                case ')':
                    cando = true;
                    i++;
                    break;
                case '{':
                    int last = Refer.getIndexOBlock(this.regex.substring(i), '{', '}');
                    cando = true;
                    i = i + last + 1;
                    break;
                case '[':
                    if (cando) {
                        this.insertAnd(i);
                        i = i + 1;
                        cando = false;
                    }
                    last = Refer.getIndexOBlock(this.regex.substring(i), '[', ']');
                    //System.out.println(last);
                    //return;
                    cando = true;
                    i = i + last + 1;
                    break;
                case '(':
                    if (cando) {
                        this.insertAnd(i);
                        i = i + 2;
                        cando = false;
                    } else {
                        i++;
                    }
                    break;

                case '\\':
                    if (this.regex.charAt(i + 1) == 'x' || this.regex.charAt(i + 1) == 'X') {  // \xFF
                        if (cando) {
                            this.insertAnd(i);
                            i = i + 5;
                        } else {
                            i = i + 4;
                        }
                        cando = true;
                    } else if (this.regex.charAt(i + 1) >= '0' && this.regex.charAt(i + 1) <= '9') {	// \000
                        if (cando) {
                            this.insertAnd(i);
                            i = i + 5;
                        } else {
                            i = i + 4;
                        }
                        cando = true;
                    } else {	// \?
                        if (cando) {
                            this.insertAnd(i);
                            i = i + 3;
                        } else {
                            i = i + 2;
                        }
                        cando = true;

                    }
                    break;
                case '^':
                    String temp2 = this.regex.substring(i + 1);
                    String temp1 = this.regex.substring(0, i);
                    //System.out.println("insert:"+ temp1+ "..." + temp2);
                    this.regex = temp1 + temp2;
                    this.modifier += "t";
                    break;
                case '$':
                    temp2 = this.regex.substring(i + 1);
                    temp1 = this.regex.substring(0, i);
                    //System.out.println("insert:"+ temp1+ "..." + temp2);
                    this.regex = temp1 + temp2;
                    this.modifier += "z";
                    break;
                case '.':
                default://is character;
                    if (cando) {
                        this.insertAnd(i);
                        i = i + 2;
                    } else {
                        i++;
                    }
                    cando = true;
                    break;
            }
        }
        System.out.println("After: " + this.regex);
        return this.regex;
    }

    /**
     * This function use for reformat Contraint repetition
     * input:
     *      mode = with_CRB => if less than specific number, Convenient method is used
     *      mode = no_CRB => convenient method for all
     */
    public void formatConvenientCR(int mode) {
        String split_and[] = this.regex.split("" + Refer._char_and);
        String rs = this.regex;
        for (int i = 0; i < rs.length(); i++) {
            if (rs.charAt(i) == '{') {
                if (i == 0 || rs.charAt(i - 1) == '\\') {
                    continue;
                } else {// it is constraint repetion.
                    //look away for subpattern
                    int findex = 0; //first position of cr
                    int lindex = 0; //last position of cr

                    //get first position
                    if (rs.charAt(i - 1) == ')') {//it is substring
                        if (rs.charAt(i - 2) == '\\') {
                            findex = i - 2;
                        } else {
                            //try to get coresponding '(' prositon
                            int count = 0;
                            for (int j = i - 1; j >= 0; j--) {
                                if (rs.charAt(j) == ')' && (j == 0 || j > 0 && rs.charAt(j - 1) != '\\')) {
                                    count++;
                                }
                                if (rs.charAt(j) == '(' && (j == 0 || j > 0 && rs.charAt(j - 1) != '\\')) {
                                    count--;
                                }
                                if (count == 0) {
                                    findex = j;
                                    break;
                                }
                            }
                        }
                    } else {//normal
                        for (int j = i - 1; j >= 0; j--) {
                            if (rs.charAt(j) == Refer._char_and || rs.charAt(j) == '|') {
                                findex = j + 1;
                                break;
                            }
                        }
                    }
                    //get last position
                    for (int j = i; j < rs.length(); j++) {
                        if (rs.charAt(j) == '}') {
                            lindex = j;
                            break;
                        }
                    }
                    //System.out.println("... " + findex + "  " + lindex + "  " + rs.substring(findex, lindex + 1));
                    String fs = rs.substring(0, findex);
                    String s = rs.substring(findex, lindex + 1);
                    String ls = rs.substring(lindex+1, rs.length());
                    System.out.println("String " + s);
                    //prosecess cr
                    String subpaterm = "";
                    int m = 0, n = 0;  //subpatterm{m,n}
                    int type = -1; //type of constraint 0: exactly 1 atleas, 2 between, 3 atmost
                    for (int j = 0; j < s.length(); j++) {
                        if (s.charAt(j) == '{' && s.charAt(j - 1) != '\\') {
                            String option = s.substring(j);
                            if (option.indexOf(',') == -1) { // is exactly
                                type = 0;
                                m = n = Integer.parseInt(option.substring(1, option.length() - 1));
                                break;
                            } else if (option.charAt(1) == ',') {// is atmost{
                                type = 3;
                                m = n = Integer.parseInt(option.substring(2, option.length() - 1));
                                break;
                            } else {
                                if (option.charAt(option.length() - 2) == ',') {//is atleast
                                    type = 1;
                                    n = m = Integer.parseInt(option.substring(1, option.length() - 2));
                                    break;
                                } else {// is between
                                    type = 2;
                                    int index1 = option.indexOf(',');
                                    m = Integer.parseInt(option.substring(1, index1));
                                    n = Integer.parseInt(option.substring(index1 + 1, option.length() - 1));
                                    break;
                                }
                            }
                        } else {
                            subpaterm += s.charAt(j);
                        }
                    }
                    if (type == -1) {
                        continue;
                    }
                    s = this.processConstraintRepetition(subpaterm, m, n, type, mode);
                    System.out.println("affert fix constraint " + s);
                    rs = fs + s + ls;
                }
            }
        }
        this.regex = rs;
    }

    /**
     * Insert and character (176) into specific position
     * @param index
     */
    private void insertAnd(int index) {
        String temp2 = this.regex.substring(index);
        String temp1 = this.regex.substring(0, index);
        //System.out.println("insert:"+ temp1+ "..."+temp2);
        this.regex = temp1 + Refer._char_and + temp2;
    }

    /**
     *
     * parse Regex and copy in to elist.
     * 
     */
    private void parseElement() {
        this.elist = this.paresElement(regex);
    }

    /**
     * parse sregex and return list of Element.
     * @param sregex
     * @return
     */
    public LinkedList<Element> paresElement(String sregex) {
        //todo.
        return null;
    }

    //=====================================================================================================
    /*
     * Process Element
     */
    /**
     * Check if the end of pattern is reached.
     * @return
     */
    public boolean isNotEnd() {
        return index < this.regex.length();
    }

    /**
     * Get element with current index position
     * @return
     *
     */
    public Element getElement() {
        Element ret = this.getElementAt(this.index);
        this.nextIndex = ret.getReturnIndex();
        return ret;
    }

    /**
     * Get element at specific position
     * @param index
     * @return  null if index is out of range
     */
    public Element getElementAt(int index) {
        if (index >= this.regex.length()) {
            return null;
        }
        return new Element(this.regex, index);
    }

    /**
     * Current position to index of next element;
     */
    public void nextElement() {
        this.index = this.nextIndex;
    }

    /**
     * Get next element
     * @return
     */
    public Element getNextElement() {
        return getElementAt(this.nextIndex);
    }
    //=====================================================================================================
    /*
     * Other function
     */

    public String toString() {
        return "/" + this.regex + "/" + this.modifier;
    }

    private String processConstraintRepetition(String subpaterm, int m, int n, int type, int mode) {
        String s = "";
        //default pattern
        if (type == 0) {//exactly
            s = subpaterm + "{" + m + "}";
        } else if (type == 1) {//atleast
            s = subpaterm + "{" + m + ",}";
        } else if (type == 2) { //between
            s = subpaterm + "{" + m + "," + n + "}";
        } else {//atmost
            s = subpaterm + "{," + "n}";
        }


        //process CR.
        if (type == 0) {//exactly
            if (mode == Refer._mode_no_CR) {//don't care about CRB block
                s = subpaterm;
                for (int k = 1; k < n; k++) {
                    s += Refer._char_and + subpaterm;
                }
            } else {
                //todo
            }
        } else if (type == 1) {//atleast
            if (mode == Refer._mode_no_CR) {//don't care about CRB block
                s = subpaterm;
                for (int k = 1; k <= n; k++) {
                    s += Refer._char_and + subpaterm;
                }
                s += "+"; //repeat more or no.
            }
        } else if (type == 2) {//between
            // = exactly m and atmost (n-m)
            if (mode == Refer._mode_no_CR) {//don't care about CRB block.
                if (m > 1) {
                    s = subpaterm;
                    for (int k = 1; k < m - 1; k++) {
                        s += Refer._char_and + subpaterm;
                    }
                    // at most (n-m)
                    s += Refer._char_and + "(";
                } else {
                    s = "(";
                }
                for (int k = 1; k <= (n - m + 1); k++) {
                    String temp = subpaterm;
                    for (int j = 1; j < k; j++) {
                        temp += Refer._char_and + subpaterm;
                    }
                    s += temp + "|";
                }
                s = s.substring(0, s.length() - 1);
                s += ")";
            }
        } else if (type == 3) {//atmost
            if (mode == Refer._mode_no_CR) {// don't care about CRB block.
                // at most (n-m)
                s = "(";
                for (int k = 1; k <= (n); k++) {
                    String temp = subpaterm;
                    for (int j = 1; j < k; j++) {
                        temp += Refer._char_and + subpaterm;
                    }
                    s += temp + "|";
                }
                s = s.substring(0, s.length() - 1);
                s += ")";
            }
        }
        return s;
    }
}
