package PCRE;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 * 
 */

/*
 * What is Element:
 *      +, operator:        *, ° , | , ? , +, {}
 *      +, parenthesis:     ()
 *      +, escape Char:     \char
 *      +, dot:             .
 *      +, hexbye:          \xAB
 *      +, char:            0..9 , a..Z
 *      +, charclass:       []
 *      +, Position:        ^,$
 *
 *
 *
 */
public class Element {
    // Common  attribute of Element, include its ID and Value
    public int id;
    public String value;
    // If Element is parenthesis "( )", so it will include a list of elements
    public LinkedList<Element> list = null;
    // Boolean attribute to indicate some type of element:
    public boolean isParentheis = false;// is parenthesis.
    public boolean isConcat = false;	//is and element
    public boolean isOpt = false;	//is or element
    public boolean isRep = false; 	//is rep element
    public boolean isAtom = false; 	//is char or char class or parenthesis
    // don't know
    private int retIndex;           //Return Index of next Element int pcre
 

    public Element(String pcre, int index) {
        
        this.id = 0;
        this.value = null;
        this.retIndex = -1;

        this.parseElement(pcre, index);
    }

    public int getReturnIndex() {
        return this.retIndex;
    }

    /**
     * Parse pcre String and get element at current position indentified by index.
     * @param pcre
     * @param index
     * Definition:
     *      Option(or):     '|'
     *      Concatenation:  '\176'(Refer._char_and)
     *      Repetition:     '*', '+', '?', '{m[,n]}'
     *      Atom:           '()', char, \xAA, \r, \n, \t, \d, \w, \s
     *                      '[]','.'
     */
    public void parseElement(String pcre, int index) {
        char chr = pcre.charAt(index);
        int last;

        switch (chr) {
            case Refer._char_and:
                this.id = Refer._op_and;
                this.value = "" + chr;
                this.isConcat = true;
                this.retIndex = index + 1;
                break;
            case '|':
                this.id = Refer._op_or;
                this.value = "" + chr;
                this.isOpt = true;
                this.retIndex = index + 1;
                break;
            case '*':
                this.id = Refer._op_star;
                this.value = "*";
                this.isRep = true;
                this.retIndex = index + 1;
                break;
            case '+':
                this.id = Refer._op_plus;
                this.value = "+";
                this.isRep = true;
                this.retIndex = index + 1;
                break;
            case '?':
                this.id = Refer._op_ques;
                this.value = "?";
                this.isRep = true;
                this.retIndex = index + 1;
                break;
            case '{':
                this.id = Refer._op_constraint;
                last = Refer.getIndexOBlock(pcre.substring(index), '{', '}');
                this.value = pcre.substring(index, index + last + 1);
                this.isRep = true;
                this.retIndex = index + last + 1;
                break;
            case '(':
                this.id = Refer._op_parent;
                last = Refer.getIndexOBlock(pcre.substring(index), '(', ')');
                this.value = pcre.substring(index + 1, index + last);
                this.isAtom = true;
                this.retIndex = index + last + 1;
                break;
            case '[':
                last = Refer.getIndexOBlock(pcre.substring(index), '[', ']');
                this.value = pcre.substring(index + 1, index + last);
                if (this.value.startsWith("^")) {
                    this.id = Refer._neg_class;
                    value = this.parseClass(value.substring(1));

                } else {
                    this.id = Refer._class;
                    value = this.parseClass(value);
                }
                //this.id = Refer._class;
                if (this.id == Refer._neg_class) {
                    this.value = "[^" + this.value + "]";
                } else {
                    this.value = "[" + this.value + "]";
                }
                this.isAtom = true;
                this.retIndex = index + last + 1;
                break;
            case '.':
                this.id = Refer._class_dot;
                this.value = ".";
                this.isAtom = true;
                this.retIndex = index + 1;
                break;
            case '\\':
                index++;
                chr = pcre.charAt(index);
                if (Character.isDigit(chr)) {//\000
                    String temp = Integer.toHexString(Integer.parseInt(pcre.substring(index, index + 3)));
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    temp = "\\x" + temp;
                    this.id = Refer._ascii_hex;
                    this.value = temp;
                    this.isAtom = true;
                    this.retIndex = index + 3;
                } else {
                    switch (chr) {
                        case 'x': 	// \xFF
                            this.id = Refer._ascii_hex;
                            this.value = pcre.substring(index - 1, index + 3);
                            this.isAtom = true;
                            this.retIndex = index + 3;
                            break;
                        case 'd':  // \d is digit
                            this.id = Refer._class_digit;
                            this.value = "\\d";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                        case 'w': // \w is work : digit and char
                            this.id = Refer._class_word;
                            this.value = "\\w";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                        case 's': // is space
                            this.id = Refer._ascii_hex;
                            this.value = "\\x20";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                        case 'n': // is LF
                            this.id = Refer._ascii_hex;
                            this.value = "\\x0A";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                        case 'r': // is CR
                            this.id = Refer._ascii_hex;
                            this.value = "\\x0D";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                        case 't': // is tab
                            this.id = Refer._ascii_hex;
                            this.value = "\\x09";
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;

                        default:
                            this.id = Refer._ascii_hex;
                            this.value = "\\x" + Integer.toHexString(chr).toUpperCase();
                            this.isAtom = true;
                            this.retIndex = index + 1;
                            break;
                    }
                }
                break;
            case '^':
                this.id = Refer._char_start;
                this.value = "" + chr;
                this.isAtom = true;
                this.retIndex = index + 1;
                break;
            case '$':
                this.id = Refer._char_end;
                this.value = "" + chr;
                this.isAtom = true;
                this.retIndex = index + 1;
                break;

            default://is character
                this.id = Refer._char;
                this.value = "" + chr;
                this.isAtom = true;
                this.retIndex = index + 1;
                break;
        }
    }

    public boolean testRangeClass(String value) {
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == '-' && value.charAt(i - 1) != '\\') {
                return true;
            }
        }
        return false;
    }

    /**
     *  a-z will become \zaz
     * b-x  -> \zbx
     *
     * @param value
     * @return value after parse class range
     */
    public String parseClassRange(String value) {
        String ret = "";
        int i;
        for (i = 0; i < value.length() - 1; i++) {
            char chr = value.charAt(i);
            if (value.charAt(i + 1) != '-') {
                ret = ret + chr;
            } else {
                if (chr == '\\') // this is \-
                {
                    ret = ret + chr;
                } else {
                    ret = ret + "\\z" + chr + value.charAt(i + 2);
                    i += 2;
                }
            }
        }
        if (i == value.length() - 1) // con ki tu cuoi cung
        {
            ret = ret + value.charAt(i);
        }
        return ret;
    }

    /**
     *  a-z will become \z617a
     * b-x  -> \z6278
     *\x01-\x08 -> \z0108
     * @param value
     * @return value after parse class range
     */
    public String parseClassRange_v2(String value) {
        String ret = "";
        PcreRule temp = new PcreRule("/" + value + "/");
        Element etemp = temp.getElement();
        while (etemp != null) {
            if (!etemp.isAtom) {
                System.out.printf("\t\tSomething wrong in char Range");
                return null;
            }
            //Todo

        }
        return ret;
    }

    /**
     *
     * @param data
     * @param index
     * @return
     *
     * This fuction get character at index of data string.
     *
     * note:
     *      + \xFF return \xFF
     *      + \000 return coresponding \xFF
     *      + \?, \+, \*, ... return the same
     *      +
     *
     */
    public String getChar(String data, int index) {
        String ret = "";
        char chr;

        while (index < data.length()) {
            chr = data.charAt(index);
            if ('\\' == chr) {
                index++;
                char c = data.charAt(index);
                //if \xFF
                if ('x' == c | 'x' == c) {
                    ret = "\\x" + data.substring(index + 1, index + 3);
                    break;
                } else if (Character.isDigit(c)) {
                    //if \000 turn into \zFF
                    ret = Integer.toHexString(Integer.parseInt(data.substring(index, index + 3)));
                    if (ret.length() == 1) {
                        ret = "0" + ret;
                    }
                    ret = "\\x" + ret;
                    break;
                } else {          // other format: \?, \*, .....
                    ret = "\\" + data.substring(index, index + 1);
                    break;
                }
            } else {              //Normal char;
                ret = ret + chr;
                break;
            }
        }
        //System.out.println("get char: " + ret);
        return ret;

    }

    /**
     *  parse character class and turn it into processable model
     * @param data
     * @return
     *
     * Note:
     *      - 1-8 => \z3138
     *      - \xAB-\xB8 => \zABB8
     *      - '-' at the end, perform a normal character.
     *      - \000, 0 is digit, tranform into \xFF.
     */
    public String parseClass(String data) {
        //System.out.println("Call parseclass "+ data);
        //boolean neg = false;
        String ret = "";
        String range1, range2;
        int index = 0;
        while (index < data.length()) {
            range1 = this.getChar(data, index);
            index += range1.length();

            if (this.getChar(data, index).compareTo("-") == 0) {
                index++;
                range2 = this.getChar(data, index);

                if (range2.length() == 0) { // if end with '-'
                    ret = ret + range1 + "\\x" + Integer.toHexString((int) '-');
                } else {
                    index += range2.length();
                    // process range into \zFFEE
                    if (range1.length() == 4) {
                        range1 = range1.substring(2);
                    } else {
                        range1 = Integer.toHexString((int) range1.substring(range1.length() - 1).charAt(0)); // just take last character.
                    }

                    if (range2.length() == 4) {
                        range2 = range2.substring(2);
                    } else {
                        range2 = Integer.toHexString((int) range2.substring(range2.length() - 1).charAt(0)); // just take last character.
                    }

                    ret = ret + "\\z" + range1 + range2;
                }
            } else {
                ret = ret + range1;
            }
        }
        //System.out.println("parse complete: " + ret);
        return ret;
    }
}
