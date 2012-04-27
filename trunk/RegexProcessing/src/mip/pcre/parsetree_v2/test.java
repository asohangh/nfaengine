/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mip.pcre.parsetree_v2;

import java.io.File;

/**
 *
 * @author heckarim
 */
public class test {

	public final String outDir_idot = System.getProperty("user.dir") + File.separatorChar + "dotFiles" + File.separatorChar;
    public static void main(String[] args) {
        test control = new test();

        //System.out.println("test");
        // System.out.println(chr.getNumericValue(chr));

        control.doTest();
        //control.doOutput();
        //control.doTest();
    }

    public void doTest() {
    	RegexTreeBuilder regexBuilder = new RegexTreeBuilder();
        //String rule = "/a*b?|c+(d|e)/smi";
        //String rule = "/User-Agent\\x3A[^\\n\\r]+Google[^\\n\\r]+Desktop/smi";
        //String rule= "/(spray|return_address|payloadcode|shellcode|retaddr|retaddress|block|payload|agent|hspt)/smi";
        //String rule = "/ab\\x3Ac/smi";
        //String rule = "/(a|b)|c*de/smi";
        String rule = "/\\sLOGIN\\s[^\\n]*?\\{/smi";
        RegexTree tree = regexBuilder.build(rule);
        System.out.println("pcre is: " + tree.rule.getPattern() + " -------- " + tree.rule.getModifier());
        tree.printTree();
        String s = "";
        //s = tree.patternOfPCRE(tree.root);
        System.out.println(s);
        tree.generateDotFile("abc.dot",outDir_idot);
    }
}
