/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TestPattern;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class Pattern {
    //public byte[] arrayData;    // Byte Array contains pattern
    public String data;
    public boolean isMatch;              // this pattern is match or not
    public int missPosition;            // where is the missmatch position.

    public Pattern(String data){
        this.data = data;
        isMatch = false;
        missPosition = -1;
    }
}
