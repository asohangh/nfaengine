/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.pcre_v2;

/**
 *
 * @author heckarim
 */

/*
 * Note:
 * + *.
 * + ?.
 * + +.
 * + {,}.
 */
public class ElementRepetition extends Element {

    ElementRepetition(Element e) {
        this.modifier = e.modifier;
        this.id = e.id;
        this.value = e.value;
        this.isAtom = e.isAtom;
        this.isOptional = e.isOptional;
        this.isParentheis = e.isParentheis;
        this.isRepetition = e.isRepetition;
    }

    public void print() {
        System.out.println("[Repetition]: " + id + " - " + value);
    }

    public boolean compareto(Element e) {
        if (e.isRepetition) {
            if (this.id == e.id && this.value.compareToIgnoreCase(e.value) == 0) {
                return true;
            }
        }
        return false;
    }

    public int getNumAtom() {
        //TODO  fix for constrait with multi sate inside
        if(this.id == Refer._op_constraint){
           // return Refer.getContraintSize(this);
           return 0;
        }
        return 0;
    }

    public String toString() {

        String ret = this.value;
        return ret;
    }
}
