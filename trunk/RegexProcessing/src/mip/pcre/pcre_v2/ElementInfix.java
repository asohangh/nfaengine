/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.pcre_v2;

/**
 *
 * @author heckarim
 */
public class ElementInfix extends Element {

    public int infixId = 0000;

    ElementInfix(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = true;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
        //
        this.isInfix = true;
        this.isPrefix = e.isPrefix;
        this.parseInfix();
    }

    private void parseInfix() {
        this.infixId = Integer.parseInt(value);
    }

    public String toString() {
        int a4, a3, a2, a1;
        a4 = infixId / 1000;
        a3 = (infixId % 1000) / 100;
        a2 = (infixId % 100) / 10;
        a1 = (infixId % 10);
        String ret = "(!i" + a4 + "" + a3 + "" + a2 + "" + a1 + ")";
        return ret;
    }
}
