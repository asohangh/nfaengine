/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import PrefixShare.PrefixShare;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class PrefixTest {
    public static void main(String[] args) throws Exception {
        PrefixTest gen = new PrefixTest();
        gen.doAction();
    }

    public void doAction() throws Exception {
        String workdir = System.getProperty("user.dir") + File.separator;

        LinkedList<String> lpcre = this.readfromfile(workdir + "pcre.prefix.test");
        String first = lpcre.getFirst();
        lpcre.removeFirst();
        PrefixShare ps = new PrefixShare(first,lpcre);
        ps.buildPrefixNFA();
        ps.combineNFA.generateDotFile("nfa_combine_noreduce.dot", null);
        ps.combineNFA.reduceRedundantState();
        ps.combineNFA.generateDotFile("nfa_combine.dot", null);
        ps.buildPrefixEngine();
        ps.engine.printBlockChar();
        ps.engine.generateDotFile("engine_combine.dot",null );
    }

    private LinkedList<String> readfromfile(String string) throws FileNotFoundException, IOException {
        LinkedList<String> ret = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(string));
        String s;
        s= br.readLine().trim();
        ret.add(s);        
        while ((s = br.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty()) {
                ret.add(s);
            }
        }
        return ret;
    }
}
