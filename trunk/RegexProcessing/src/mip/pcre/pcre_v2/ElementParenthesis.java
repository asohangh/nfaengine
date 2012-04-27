/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.pcre_v2;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
/*
 * Note:
 * + ()
 * + Assertion ...
 */
public class ElementParenthesis extends Element {

    public LinkedList<Element> elist = null;

    ElementParenthesis(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = e.isAtom;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
        //do
        this.parseParenthesis();
    }

    /**
     * note:
     * +, not care assertion, just bare parethesis
     */
    private void parseParenthesis() {
        ElementParser epar = new ElementParser(this.value, this.modifier);
        epar.parseElement();
        this.elist = epar.lelement;
        // System.out.println("ok");
    }

    @Override
    public void print() {
        System.out.println("[Parethesis]: " + this.id + " - " + this.value);
        for (int i = 0; i < elist.size(); i++) {
            Element e = elist.get(i);
            System.out.print("+");
            e.print();
        }
    }

    public boolean compareto(Element e) {
        if (this.isParentheis) {
            if (this.id == e.id) {
                if (this.modifier.indexOf('i') != -1) {
                    if (this.value.compareToIgnoreCase(e.value) == 0) {
                        return true;
                    }
                } else {
                    if (this.value.compareTo(e.value) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getNumAtom() {
        int re = 0;
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            re += e.getNumAtom();
        }
        return re;
    }

    void insertConcat() {
        for (int i = 0; i < (this.elist.size()); i++) {
            Element e = this.elist.get(i);

            if (e.isParentheis) {
                ElementParenthesis epar = (ElementParenthesis) e;
                epar.insertConcat();
            }
            if ((i + 1) < this.elist.size()) {
                Element enext = this.elist.get(i + 1);
                if ((e.isAtom | e.isParentheis | e.isRepetition) && (enext.isAtom || enext.isParentheis)) {
                    Element enew = new ElementConcat(this.modifier);
                    this.elist.add(i + 1, enew);
                }
            }

        }
    }

    public String toString() {
        String ret = "";
        for (int i = 0; i < this.elist.size(); i++) {
            ret += this.elist.get(i).toString();
        }
        ret = "(" + ret + ")";
        return ret;
    }
}
