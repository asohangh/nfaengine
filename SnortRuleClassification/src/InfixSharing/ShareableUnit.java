/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package InfixSharing;

import Regex.Element;
import Regex.ElementInfix;
import Regex.ElementShareableUnit;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class ShareableUnit {

    pcre regex;
    public LinkedList<Element> lelement;
    public LinkedList<ReInfix> inlist = new LinkedList<ReInfix>();
    int size; //size of prefix (no of element atom)
    public int sizeShare = 0;
    public int sizeAtom = 0;
    //int begin; //position in regex;

    public ShareableUnit(LinkedList<Element> le) {
        this.lelement = le;
    }

    public boolean compareto(ShareableUnit pre) {
        if (this.lelement.size() != pre.lelement.size()) {
            return false;
        }
        for (int i = 0; i < this.lelement.size(); i++) {
            if (!this.lelement.get(i).compareto(pre.lelement.get(i))) {
                return false;
            }
        }
        return true;
    }

    void print() {
        System.out.println("Infix size" + this.lelement.size());
        for (int i = 0; i < this.lelement.size(); i++) {
            System.out.print(this.lelement.get(i).getString());
        }
        System.out.println("\nPCRE: ");

        System.out.print(this.regex.getString() + "\n");


    }

    public String getString() {
        String s = "";
        for (int i = 0; i < this.lelement.size(); i++) {
            s += this.lelement.get(i).getString();
        }
        return s;
    }

    private int getNumAtom() {
        int noAtom = 0;
        for (int i = 0; i < this.lelement.size(); i++) {
            Element e = this.lelement.get(i);
            if (e.isAtom) {
                noAtom++;
            } else if (e.isParentheis) {
                noAtom += e.getNumAtom();
            }
        }
        return noAtom;
    }

    void replace(ReInfix infix) {
        //System.out.println("\nShareabelunit Replace:" + infix.getString());
        //System.out.println("\t\tbefore " + this.getString());
       /* for (int i = 0; i < this.inlist.size(); i++) {
            System.out.println(" infix " + i + " " + this.inlist.get(i).getString());
        }*/
        ElementInfix e = new ElementInfix(infix.id);
        //this.print();
        //infix.print();
        int begin = -1;
        int next = 0;
        for (int i = 0; i < this.lelement.size(); i++) {
            Element e1 = this.lelement.get(i);
            Element e2 = infix.lelement.get(next);
            //System.out.println("e1: " + e1.toString() + " - e2: " + e2.toString());
            if (e1.compareto(e2)) {
                if (begin == -1) {
                    begin = i;
                }
                next++;
            } else {
                if (begin != -1) {
                    i = begin;

                }
                begin = -1;
                next = 0;
            }
            // System.out.println("begin, next " + begin + " , " +next);

            if (next == infix.lelement.size()) {
                break;
            }
        }
        if (begin == -1 || next != infix.lelement.size()) {
            //error
            System.out.println("ShareUnit: Repalce: Error: ");
        }
        for (int i = 0; i < infix.lelement.size(); i++) {
            this.lelement.remove(begin);
        }
        this.lelement.add(begin, e);
       // System.out.println("\t\tafter  " + begin + " .. " + this.getString());
    }

    void replaceRegex() {
        //System.out.println("\nBefore: " + this.regex.getString());
        int index = -1;
        for (int i = 0; i < regex.elist.size(); i++) {
            if (regex.elist.get(i).isShareableUnit) {
                ElementShareableUnit share = (ElementShareableUnit) regex.elist.get(i);
                if (share.share == this) {
                    index = i;
                }
            }
        }
        //replace in parenthesis
        for (int i = 0; i < regex.elist.size(); i++) {
            Element e = regex.elist.get(i);
            if (e.isParentheis) {
                e.replaceRegex(this);
            }

        }
        if (index == -1) {
            System.out.println("there is error " + this.getString());
        } else {
            regex.elist.remove(index);
            for (int i = 0; i < this.lelement.size(); i++) {
                regex.elist.add(index + i, this.lelement.get(i));
            }
        }
        //System.out.println("After: " + this.regex.getString());
    }
}
