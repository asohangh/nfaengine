package ParseTree;

import PCRE.Element;

public class Node {
    //int operator;
    public int id;              //use to indentify sort of element. describe in pcre.Refer.
    public String value="";

    public Node left;
    public Node right;

    public Node(Element e){
            this.id = e.id;
            this.value = e.value;
            this.left = this.right = null;
    }

    public Node (String s,int id){
    	this.id=id;
        value = s;
        left = right = null;
    }
}

