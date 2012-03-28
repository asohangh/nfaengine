/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ParseTree;

import PCREv2.Element;
import PCREv2.Refer;

/**
 *
 * @author heckarim
 */
public class Node {

    //int operator;
    public int id;              //use to indentify sort of element. describe in pcre.Refer.
    public String value = "";
    public Node left;
    public Node right;
    public Element element; //is for  Atom and Constraint only

    public Node(Element e) {
        this.id = e.id;
        this.value = e.value;
        this.left = this.right = null;
        this.element = e;
    }

    public Node(String s, int id) {
        this.id = id;
        value = s;
        left = right = null;
    }

    /**
     *
     * @param e1
     * @param e2
     * Since  (...) {..,..}  ~~  e2 e1
     */
    Node(Element e1, Element e2) {
        this.id = e1.id;
        this.value = e1.value;
        this.left = this.right = null;
        this.element = e2;
    }

    public Element getElement() {
        return element;
    }

    
}
