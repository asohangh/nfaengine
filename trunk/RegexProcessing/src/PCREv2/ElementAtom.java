/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PCREv2;

/**
 *
 * @author heckarim
 */
/**
 *
 *   Note:
 * + ^
 * + $
 * + Single char
 * + Class []
 * + Escape
 */
public class ElementAtom extends Element {


    ElementAtom(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = e.isAtom;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
    }

    public void print() {
        System.out.println("[Atom]: " + id + " - " + value);
    }

    public boolean compareto(Element e) {
        
        if (e.isAtom) {
          
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
        return 1;
    }

    public String toString() {
        String ret = "";
        switch (this.id) {
            case Refer._char_start:
                ret = "^";
                break;
            case Refer._char_end:
                ret = "$";
                break;
            default:
                ret = value;
                break;
        }
        return value;
    }
}
