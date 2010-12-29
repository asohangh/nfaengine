/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ParseTree;

import TestPattern.Contructor;

/**
 *
 * @author heckarim
 */
public class test {

    public static void main(String[] args) {
        test control = new test();

        //System.out.println("test");
       // System.out.println(chr.getNumericValue(chr));

        control.doTest();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest() {
       //String rule = "/a*b?|c+(d|e)/smi";
       //String rule = "/User-Agent\\x3A[^\\n\\r]+Google[^\\n\\r]+Desktop/smi";
       String rule= "/(spray|return_address|payloadcode|shellcode|retaddr|retaddress|block|payload|agent|hspt)/smi";
        //String rule = "/ab\\x3Ac/smi";
       //String rule = "/(a|b)|c*de/smi";
        ParseTree tree=new ParseTree(rule);
        System.out.println("pcre is: "+tree.rule.getPattern() +" -------- "+tree.rule.getModifier())	;
        tree.printTree();
        String s = "";
        s = tree.patternOfPCRE(tree.root);
        System.out.println(s);
        //tree.generateDotFile(null, null);
        Contructor con = new Contructor(tree);
        
        
        System.out.println("Old pattern: " + tree.rule.testPartten);
        for(int i =0; i< 10; i++){
            String ts = con.buildTestString(true);
            System.out.println("Pattern Constructor: " + ts );
        }
    }

}
