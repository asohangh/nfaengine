/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package InfixSharing;

import Regex.Element;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class ReInfix {

    LinkedList<ShareableUnit> lshareable = new LinkedList<ShareableUnit>();
    LinkedList<Element> lelement;
    int size; //size of prefix (no of element atom)
    public int sizeShare = 0;
    public int sizeAtom = 0;
    int id = 0;

    public ReInfix(LinkedList<Element> le) {
        this.lelement = le;
    }

    ReInfix(LinkedList<Element> el, ShareableUnit in1, ShareableUnit in2) {
        this.lelement = el;
        this.lshareable.add(in2);
        this.lshareable.add(in1);
    }

    public boolean compareto(ReInfix pre) {
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
        System.out.println("\nInfix id: " + this.id + " size : " + this.lelement.size() + " : ");
        for (int i = 0; i < this.lelement.size(); i++) {
            System.out.print(this.lelement.get(i).getString());
        }
        System.out.print("\n");

        System.out.println("Shareable uni: " + this.lshareable.size());
        for (int i = 0; i < this.lshareable.size(); i++) {
            ShareableUnit share = this.lshareable.get(i);
            System.out.println(share.getString());
        }

    }

    public String getString() {
        String s = "";
        for (int i = 0; i < this.lelement.size(); i++) {
            s += this.lelement.get(i).value;
        }
        return s;
    }

    /**
     * count sharing atom
     *
     * note:
     *    share = noAtom * noPcre
     */
    void countShare() {
        int noAtom = this.getNumAtom();

        this.sizeShare = noAtom * this.lshareable.size();
        this.sizeAtom = noAtom;
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

    public String getInfixString() {
        String s = "";
        for (int i = 0; i < this.lelement.size(); i++) {
            s += this.lelement.get(i).toString();
        }
        //TODO   modifier....
        s = "/" + s+ "/" + "smi";
        return s;
    }
}
