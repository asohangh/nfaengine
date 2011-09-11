/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PCREv2;

/**
 *
 * @author heckarim
 */
/*
 *  Note:
 * + |
 */
public class ElementOptional extends Element {

    ElementOptional(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = e.isAtom;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
    }

    public void print() {
        System.out.println("[Optional]: " + id + " - " + value);
    }

    public boolean compareto(Element e) {
        if (e.isOptional) {
            return true;
        } else {
            return false;
        }
    }
    public int getNumAtom(){
        return 0;
    }
}
