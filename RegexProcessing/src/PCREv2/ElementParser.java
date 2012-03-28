/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PCREv2;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class ElementParser {

    public String pcre;
    public String modifier;
    public int index;
    public LinkedList<Element> lelement = new LinkedList<Element>();

    public ElementParser(String pcre, String modifier) {
        this.pcre = pcre;
        this.modifier = modifier;
        this.index = 0;
    }

    public void parseElement() {

        while (index < pcre.length()) {
            Element e = this.getElement();
            if (e.isAtom) {
                ElementAtom eatom = new ElementAtom(e);
                this.lelement.add(eatom);
            } else if (e.isOptional) {
                ElementOptional eop = new ElementOptional(e);
                this.lelement.add(eop);
            } else if (e.isParentheis) {
                ElementParenthesis epar = new ElementParenthesis(e);
                this.lelement.add(epar);
            } else if (e.isRepetition) {
                ElementRepetition erep = new ElementRepetition(e);
                this.lelement.add(erep);
            } else if (e.isInfix) {
                ElementInfix erep = new ElementInfix(e);
                this.lelement.add(erep);
            } else if (e.isPrefix) {
                ElementPrefix erep = new ElementPrefix(e);
                this.lelement.add(erep);
            }
        }
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
    public Element getElement() {
        char chr = pcre.charAt(index);
        int last;
        Element element = new Element(this.modifier);

        switch (chr) {
            case '|':
                element.id = Refer._op_or;
                element.value = "" + chr;
                element.isOptional = true;
                index = index + 1;
                break;
            case '*':
                element.id = Refer._op_star;
                element.value = "*";
                element.isRepetition = true;
                index = index + 1;
                break;
            case '+':
                element.id = Refer._op_plus;
                element.value = "+";
                element.isRepetition = true;
                index = index + 1;
                break;
            case '?':
                element.id = Refer._op_ques;
                element.value = "?";
                element.isRepetition = true;
                index = index + 1;
                break;
            case '{':
                element.id = Refer._op_constraint;
                last = Refer.getIndexOBlock(pcre.substring(index), '{', '}');
                element.value = pcre.substring(index, index + last + 1);
                element.isRepetition = true;
                index = index + last + 1;
                break;
            case '(':
                last = Refer.getIndexOBlock(pcre.substring(index), '(', ')');
                //element.id = Refer._op_parent;
                element.value = pcre.substring(index + 1, index + last);

                this.parseParenthesis(element);
                index = index + last + 1;
                break;
            case '[':
                last = Refer.getIndexOBlock(pcre.substring(index), '[', ']');
                element.value = pcre.substring(index + 1, index + last);
                if (element.value.startsWith("^")) {
                    element.id = Refer._neg_class;
                    element.value = this.parseClass(element.value.substring(1));

                } else {
                    element.id = Refer._class;
                    element.value = this.parseClass(element.value);
                }
                //this.id = Refer._class;
                if (element.id == Refer._neg_class) {
                    element.value = "[^" + element.value + "]";
                } else {
                    element.value = "[" + element.value + "]";
                }
                element.isAtom = true;
                index = index + last + 1;
                break;
            case '.':
                element.id = Refer._class_dot;
                element.value = ".";
                element.isAtom = true;
                index = index + 1;
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
                    element.id = Refer._ascii_hex;
                    element.value = temp;
                    element.isAtom = true;
                    index = index + 3;
                } else {
                    switch (chr) {
                        case 'x': 	// \xFF
                            element.id = Refer._ascii_hex;
                            element.value = pcre.substring(index - 1, index + 3);
                            element.isAtom = true;
                            index = index + 3;
                            break;
                        case 'd':  // \d is digit
                            element.id = Refer._class_digit;
                            element.value = "\\d";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 'w': // \w is work : digit and char
                            element.id = Refer._class_word;
                            element.value = "\\w";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 's': // is space
                            element.id = Refer._ascii_hex;
                            element.value = "\\x20";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 'S': // is not space
                            element.id = Refer._class_notspace;
                            element.value = "\\S";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 'n': // is LF
                            element.id = Refer._ascii_hex;
                            element.value = "\\x0A";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 'r': // is CR
                            element.id = Refer._ascii_hex;
                            element.value = "\\x0D";
                            element.isAtom = true;
                            index = index + 1;
                            break;
                        case 't': // is tab
                            element.id = Refer._ascii_hex;
                            element.value = "\\x09";
                            element.isAtom = true;
                            index = index + 1;
                            break;

                        default:
                            System.out.println("<ElementParser> Warning:\\" + chr);
                            element.id = Refer._ascii_hex;
                            element.value = "\\x" + Integer.toHexString(chr).toUpperCase();
                            element.isAtom = true;
                            index = index + 1;
                            break;
                    }
                }
                break;
            case '^':
                element.id = Refer._char_start;
                element.value = "" + chr;
                element.isAtom = true;
                index = index + 1;
                break;
            case '$':
                element.id = Refer._char_end;
                element.value = "" + chr;
                element.isAtom = true;
                index = index + 1;
                break;

            default://is character
                element.id = Refer._char;
                element.value = "" + chr;
                element.isAtom = true;
                index = index + 1;
                break;
        }
        return element;
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
     *
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
    }8/

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

    void print() {
        System.out.println("Element printer: ");
        for (int i = 0; i < this.lelement.size(); i++) {
            Element e = this.lelement.get(i);
            e.print();
            /*
            if (e.isAtom) {
            System.out.println("[Atom]: " + e.id + " -" + e.value);
            } else if (e.isOptional) {
            System.out.println("[Optional]: " + e.id + " -" + e.value);
            } else if (e.isParentheis) {
            System.out.println("[Parenthesis]: " + e.id + " -" + e.value);
            } else if (e.isRepetition) {
            System.out.println("[Repetition]: " + e.id + " -" + e.value);
            }*/
        }
    }

    /**
     *
     * @param e
     *
     * NoTe:
     *      +, bare parenthesis.
     *      +, prefix.  (!p0000)
     *      +, infix.   (!i0000)
     *      +, assertion.
     *
     */
    private void parseParenthesis(Element e) {
        String value = e.value.trim();
        char chr = value.charAt(0);
        int last;
        if (value.length() == 6) {
            if (value.charAt(0) == '!' && value.charAt(1) == 'p') {
                //is prefix
                e.isPrefix = true;
                e.id = Refer._prefix;
                e.value = value.substring(2);

            } else if (value.charAt(0) == '!' && value.charAt(1) == 'i') {
                //is inffix
                e.isInfix = true;
                e.id = Refer._infix;
                e.value = value.substring(2);
            } else {
                e.isParentheis = true;
                e.id = Refer._op_parent;
            }
        } else {
            e.isParentheis = true;
            e.id = Refer._op_parent;
        }

    }
}
