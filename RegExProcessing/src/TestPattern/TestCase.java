/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TestPattern;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 *
 * Each testcase contain set of pattern
 * and set of index of pcre
 */
public class TestCase {
    public LinkedList<Pattern> listPattern = new LinkedList<Pattern>();
    public LinkedList<Integer> listMatchIndex = new LinkedList<Integer>();

    public void TestCase(){

    }
    public void addPattern(Pattern pat, int index){
        this.listPattern.add(pat);
        if(pat.isMatch)
            this.listMatchIndex.add(index);
    }


}
