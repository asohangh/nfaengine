/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.pcre_v2;

/**
 *
 * @author heckarim
 */
public class ElementConcat extends Element {

    public void print() {
        System.out.println("[Element]: " + id + " - " + value);
    }

    public boolean compareto(Element e) {
        if (this.id == e.id) {
            if (this.value.compareTo(e.value) == 0) {
                return true;
            }
        }
        return false;
    }

    public int getNumAtom() {
        return 0;
    }

    ElementConcat(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = e.isAtom;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
    }

    ElementConcat() {
        this.id = Refer._op_and;
        this.value = "" + Refer._char_and;
        this.isConcat = true;
    }

    ElementConcat(String modifier) {
        this.id = Refer._op_and;
        this.modifier = modifier;
        this.value = "" + Refer._char_and;
        this.isConcat = true;
    }

    public String toString() {
        String ret = "";
        return ret;
    }
}
