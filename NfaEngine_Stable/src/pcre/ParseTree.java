package pcre;

import javax.swing.text.Document;

/*
 * 	Author: Heckarim, version 1
 */

/*
 * 			Chaim Frenkel: "Perl's grammar can not be reduced to BNF.
 * 				The work of parsing perl is distributed between yacc, the lexer, smoke and mirrors."
 */

/* The BNF will be apply:
 *
 * pcre		::= concat | (concat '|' expr)
 *
 * concat	::= rep | (rep '\176' concat)
 *
 * rep		::= atom '*' | atom '?' | atome '+' | atom '{...}'
 *
 * atom		::= char | '(' expr ')'
 *
 * char		::= alphanumeric or class character "[]";
 */

/*
 * Supported::
 * 			Operator: *, and, +, ?, {}, |, ()
 * 			Leaf:    a, ., [], \xFF, \d, \w, \s, \n, \r, \t
 * 			Modifier: smi
 * NotSupport::
 * 			Operator: backreference, ^, $, ...
 * 			Leaf: \000 ...
 */


public class ParseTree {

	public Node root;			// Root of the tree
	public PcreRule rule;                   // DataStructure for Pcre rule.

	//Building Node Function nest.
    public Node buildLeafNode (Element e){
        Node re = new Node (e);
        return re;
    }
    public Node buildOrNode(Node node1, Node node2){
        Node re=new Node("" + '|', Refer._op_or);
        re.left = node1;
        re.right = node2;
        return re;
    }

    public Node buildAndNode(Node node1, Node node2){
        Node re=new Node("" + Refer._char_and, Refer._op_and);
        re.left = node1;
        re.right = node2;
        return re;
    }

    public Node buildRepNode(Node temp, Element e){
        Node re = new Node(e);
        re.left = temp;
        return re;
    }

    //Parse tree function nest.
    public ParseTree(String rule){
        this.rule = new PcreRule(rule);
        this.root = parsePcre(this.rule);
    }

    /**
     * BNF:     pcre    ::= concat | (concat '|' expr)
     * @param rule
     * @return
     */
    public Node parsePcre(PcreRule rule){
        Node temp = parseConcat(rule);
        if(temp == null){
            pcre.Refer.println("Something wrong, parse Pcre", null);
        return null;
    }
            while(rule.isNotEnd() && rule.getElement().isOpt){
                    rule.nextElement();
                    Node othernode = parseConcat(rule);
                    temp = this.buildOrNode(temp, othernode);
            }
            return temp;
    }

    /**
     * BNF:     concat	::= rep | (rep '\176' concat)
     * @param rule
     * @return
     */
	public Node parseConcat(PcreRule rule){
		Node temp = parseRep(rule);
		if (temp == null){
			pcre.Refer.println("Somethig wrong, parseConcat",null);
            return null;
		}
		while(rule.isNotEnd() && rule.getElement().isConcat){
			rule.nextElement();
			Node othernode = parseRep(rule);
			temp = this.buildAndNode(temp, othernode);
		}
		return temp;
	}

    /**
     * BNF:     rep		::= atom '*' | atom '?' | atome '+' | atom '{...}'
     * @param rule
     * @return
     */
    public Node parseRep(PcreRule rule){
		Node temp = parseAtom(rule);
		if(temp == null){
			System.out.println("Somethingworng rep");
            return null;
        }
		while(rule.isNotEnd() && rule.getElement().isRep){
			temp = buildRepNode(temp,rule.getElement());
			rule.nextElement();
		}
		return temp;
	}

    /**
     * BNF:     atom    ::= char | '(' expr ')'
     * @param rule
     * @return
     */
	public Node parseAtom(PcreRule rule){
            Node temp = null;
            if(rule.isNotEnd() && rule.getElement().isAtom) {
                if(rule.getElement().id != Refer._op_parent){
                    Element eTemp = rule.getNextElement();
                    if(eTemp != null && eTemp.id == Refer._op_constraint){
                        Element ele = rule.getElement();
                        ele.value = "/" + ele.value + "/" + this.rule.getModifier();
                        temp = buildLeafNode(ele);
                        rule.nextElement();
                    }else {
                        temp = buildLeafNode(rule.getElement());
                        rule.nextElement();
                    }
            }else if(rule.getNextElement() != null && rule.getNextElement().id == Refer._op_constraint){
                Element ele = rule.getElement();
                ele.value = "/" + ele.value + "/" + this.rule.getModifier();
                temp = buildLeafNode(ele);
                rule.nextElement();
                }else{
                        PcreRule other = new PcreRule();
                        other.setPattern(rule.getElement().value);
                        temp = parsePcre(other);
                        rule.nextElement();
                        }
                }
                return temp;
	}

    /*
     *      PRINT TREE FUNCTION NEST.
     */
    public void printTree(){
        pcre.Refer.println("Print Tree: ", null);
		this.printTreeRecursive(null,root, 0);
    }

    public void printTree(Document doc){
        pcre.Refer.println("Print Tree: ", doc);
		this.printTreeRecursive(doc,root, 0);
    }
    public void printTreeRecursive(Document doc,Node root, int tab){
        if(root==null)
			return;
		for(int i=0;i<tab;i++){
            pcre.Refer.print("    ", doc);
        }

        pcre.Refer.print(tab + ".", doc);
        pcre.Refer.println(root.value + "," + Refer.convert[root.id], doc);

		if(root.left==null && root.right==null)
			return;
		if(root.left!=null)
			this.printTreeRecursive(doc,root.left, tab+1);
		if(root.right!=null)
			this.printTreeRecursive(doc,root.right, tab+1);
    }

}
