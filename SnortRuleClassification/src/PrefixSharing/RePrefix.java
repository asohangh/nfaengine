/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PrefixSharing;

import Regex.Element;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RePrefix {

    LinkedList<pcre> lregex = new LinkedList<pcre>();
    LinkedList<Element> lelement;
    int size; //size of prefix (no of element atom)    
    public int sizeShare = 0;
    public int sizeAtom = 0;
    public int id = 0;

    RePrefix(LinkedList<Element> le) {
        this.lelement = le;
    }

    public boolean compareto(RePrefix pre) {
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
        System.out.println("Prefix size" + this.lelement.size());
        for (int i = 0; i < this.lelement.size(); i++) {
            this.lelement.get(i).print();
        }
        System.out.println("PCRE:");
        for (int i = 0; i < this.lregex.size(); i++) {
            System.out.println(this.lregex.get(i).getRule());
        }

    }

    /**
     * count sharing atom
     * 
     * note: 
     *    share = noAtom * noPcre
     */
    void countShare() {
        int noAtom = this.getNumAtom();

        this.sizeShare = noAtom * this.lregex.size();
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

    void replacePrefix() {
        for (int i = 0; i < this.lregex.size(); i++) {
            pcre pe = this.lregex.get(i);
            pe.replacePrefix(this);
        }
    }

    public String getPrefixString() {
        String ret = "/";
        for (int i = 0; i < this.lelement.size(); i++) {
            ret += this.lelement.get(i).getString();
        }
        ret += "/smi";
        return ret;
    }
}
