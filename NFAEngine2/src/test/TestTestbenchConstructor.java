/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import ParseTree.ParseTree;
import TestPattern.Contructor;
import java.util.regex.Pattern;

/**
 *
 * @author heckarim
 */
public class TestTestbenchConstructor {

    public static void main(String[] args) {
        TestTestbenchConstructor tc = new TestTestbenchConstructor();
        tc.action();
    }

    private void action() {
        String pcre ="/abc((x|y)*zm){4}s/smi";

        ParseTree ps = new ParseTree(pcre);
        Contructor con = new Contructor(ps);
        TestPattern.Pattern pt = con.BuildPattern(true);

       System.out.println(pt.data);
    }
}
