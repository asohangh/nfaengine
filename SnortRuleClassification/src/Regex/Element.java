package Regex;

import InfixSharing.ShareableUnit;
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
 */
public class Element {
    // Common  attribute of Element, include its ID and Value

    public int id;
    public String value;
    public String modifier = "";
    // If Element is parenthesis "( )", so it will include a list of elements
    // Boolean attribute to indicate some type of element:
    public boolean isParentheis = false;// is parenthesis.
    public boolean isAtom = false; 	//is atom element
    public boolean isOptional = false; //is | element
    public boolean isRepetition = false; // is repetition operator
    public boolean isConcat = false;
    public boolean isPrefix = false;
    public boolean isInfix = false;
    public boolean isShareableUnit = false;

    public Element() {
        this.id = -1;
        this.value = null;
    }

    Element(String modifier) {
        this.modifier = modifier;
    }

    public void print() {
        System.out.println("[Element]: " + id + " - " + value);
    }

    public String getString() {
        return this.value;
    }

    public boolean compareto(Element e) {
        System.out.println("compare atom: " + this.value + "   " + e.value);
        if (e.isAtom && this.isAtom) {
            if (this.modifier.indexOf('i') != -1) {//case sensitive
                if (this.value.compareToIgnoreCase(e.value) == 0) {
                    return true;
                }
            } else {
                if (this.value.compareTo(e.value) == 0) {
                    return true;
                }

            }

        }
        return false;
    }

    public int getNumAtom() {
        return 0;
    }

    public String toString() {
        return value;
    }

    public boolean replaceRegex(ShareableUnit aThis) {
        System.out.println("Element: wrong");
        return false;
    }
}
