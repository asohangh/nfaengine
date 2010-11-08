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
        parsePcre(rule.trim());
        formatPcre();
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
    private void parseElement(){
        this.elist = this.paresElement(regex);
    }
    /**
     * parse sregex and return list of Element.
     * @param sregex
     * @return
     */
    public LinkedList<Element> paresElement(String sregex){
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
}
