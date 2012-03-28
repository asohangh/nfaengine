/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Regex;

import InfixSharing.ShareableUnit;
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

    public String toString() {
        /*  String ret = "";
        for (int i = 0; i < this.elist.size(); i++) {
        ret += this.elist.get(i).toString();
        }
        ret = "(" + ret + ")";
        return ret;
         *
         */
        return getString();
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

    public String getString() {
        String s = "";
        for (int i = 0; i < elist.size(); i++) {
            s += this.elist.get(i).getString();
        }
        s = "(" + s + ")";
        return s;
    }

    public int getNumAtom() {
        int re = 0;
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isAtom) {
                re++;
            } else if (e.isParentheis) {
                re += e.getNumAtom();
            }
        }
        return re;
    }

    public LinkedList<ShareableUnit> getshareablepattern() {
        LinkedList<ShareableUnit> lsInfix = new LinkedList<ShareableUnit>();
        LinkedList<Element> le = new LinkedList<Element>();
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isAtom) {
                le.add(e);
            } else {
                if (le.size() >= 6) { //ok to ad
                    ShareableUnit in = new ShareableUnit(le);
                    lsInfix.add(in);
                    le = new LinkedList<Element>();
                } else {
                    le.removeAll(le);
                }
            }
        }
        if (le.size() >= 6) { //ok to ad
            ShareableUnit in = new ShareableUnit(le);
            lsInfix.add(in);
        }
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isParentheis) {
                lsInfix.addAll(((ElementParenthesis) e).getshareablepattern());
            }
        }
        return lsInfix;
    }

    @Override
    public boolean replaceRegex(ShareableUnit aThis) {
        boolean ret = false;
        //System.out.println("\nBefore: " + this.getString());
        int index = -1;
        for (int i = 0; i < this.elist.size(); i++) {
            if (this.elist.get(i).isShareableUnit) {
                ElementShareableUnit share = (ElementShareableUnit) this.elist.get(i);
                if (share.share == aThis) {
                    index = i;
                }
            }
        }
        //replace in parenthesis
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isParentheis) {
                if (e.replaceRegex(aThis)) {
                    ret = true;
                }
            }

        }
        if (index == -1) {
            if (!ret) {
                System.out.println("there is error " + this.getString());
            }
        } else {
            this.elist.remove(index);
            for (int i = 0; i < aThis.lelement.size(); i++) {
                this.elist.add(index + i, aThis.lelement.get(i));
            }
            ret = true;
        }
        //System.out.println("After: " + this.getString());
        return ret;
    }

    public void replaceElement2shareable(ShareableUnit get) {
       // System.out.println("parenthesis: " + get.getString());
       // System.out.println("parenthesis: before " + this.getString());
        ElementShareableUnit e = new ElementShareableUnit(get);
        int begin = -1;
        for (int i = 0; i <= (this.elist.size() - get.lelement.size()); i++) {
            Element e1 = this.elist.get(i);
            Element e2 = get.lelement.getFirst();

            if (e1 == e2) {
                begin = i;
            }
        }
        if (begin == -1) {
            //
        } else {

            for (int i = 0; i < get.lelement.size(); i++) {
                this.elist.remove(begin);
            }
            this.elist.add(begin, e);
        }

        //replace inside packet
        for (int i = 0; i < this.elist.size(); i++) {
            Element element = this.elist.get(i);
            if (element.isParentheis) {
                ElementParenthesis par = (ElementParenthesis) element;
                par.replaceElement2shareable(get);
            }

        }
       // System.out.println("parenthesis: after " + this.getString());
    }
}
