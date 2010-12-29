/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPattern;

import ParseTree.ParseTree;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
/**
 *
 * Contain pcre and test patterm
 */
public class PCREPatternCollection {

    ParseTree pTree;
    String pcre;
    int index = -1; //index of rule.
    LinkedList<Pattern> matchPattern = new LinkedList<Pattern>();
    LinkedList<Pattern> missPattern = new LinkedList<Pattern>();
    Contructor contructor;

    public PCREPatternCollection(ParseTree pTree,int index) {
        this.index = index;
        this.pTree = pTree;
        this.contructor = new Contructor(pTree);
    }

    PCREPatternCollection(String pcre, int index) {
        this.pcre = pcre;
        this.pTree = new ParseTree(pcre);
        this.index = index;
        this.contructor = new Contructor(this.pTree);
    }

    public void generateMissPattern(int size) {
        for (int i = 0; i < size; i++) {
            this.missPattern.add(contructor.BuildPattern(false));
        }
    }
    public void generateMatchPattern(int size) {
        for (int i = 0; i < size; i++) {
            this.matchPattern.add(contructor.BuildPattern(true));
        }
    }
}
