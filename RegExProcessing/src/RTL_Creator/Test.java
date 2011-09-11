/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTL_Creator;

import HDL_Generator.HDL_Generator_v2;
import PCREv2.PcreRule;
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
public class Test {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Test test = new Test();
        test.action();
    }
    LinkedList<String> lpre = new LinkedList<String>();
    LinkedList<String> lin = new LinkedList<String>();
    LinkedList<String> len = new LinkedList<String>();
    RTL_Creator_v2 creator;
    public static String infolder = System.getProperty("user.dir") + 
            File.separator + "test" + File.separator +"extract"+ File.separator;
    public static String outfolder = System.getProperty("user.dir") +
            File.separator + "test" + File.separator +"gen"+ File.separator;

    private void action() throws FileNotFoundException, IOException {
        String file = infolder + "icnc.pcre";
        this.readfromfile(file);
        System.out.println("finish readfromfile");
        
        //craete ReGroup
        creator = new RTL_Creator_v2();
        creator.createGroup(0);
        creator.addPrefix(0, lpre);
        System.out.println("finish prefix");
        creator.addInfix(0, lin);
        System.out.println("finish infix");
        creator.addEngine(0, len);
        System.out.println("finish create RTL");

        creator.reduceChar(0);
        //creator.print(0);
        //generate HDL
        HDL_Generator_v2 gen = new HDL_Generator_v2();
        gen.genfolder = outfolder;
        gen.setRTLCreator(creator);
        //0: LUT, 1: Bram, 2: decoder
        gen.genHDL(1);
        gen.outstatistic();
        //this.calculateChar();
    }

    private void readfromfile(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        int mode = 0; //o pref, 1 infi, 2 engie
        while ((s = br.readLine()) != null) {
            if (s.compareToIgnoreCase("#prefix") == 0) {//read prefix
                mode = 0;
                continue;
            } else if (s.compareToIgnoreCase("#infix") == 0) {
                mode = 1;
                continue;
            } else if (s.compareToIgnoreCase("#engine") == 0) {
                mode = 2;
                continue;
            }
            if (s.startsWith("#")) {
                continue;
            }
            if (s.trim().isEmpty()) {
                continue;
            }
            switch (mode) {
                case 0:
                    this.lpre.add(s.trim());
                    break;
                case 1:
                    this.lin.add(s.trim());
                    break;
                case 2:
                    this.len.add(s.trim());
                    break;
            }

        }
        br.close();
    }

    private void calculateChar() {
        int count = 0;
        for (int i = 0; i < this.len.size(); i++) {
            int size;
            String pcre = this.len.get(i);
            PcreRule rule = new PcreRule(pcre);
            size = rule.getNoChar();
            System.out.println("\t" + size + " - " + pcre);
            count += rule.getNoChar();
        }
        System.out.println("no char : " + count);
    }
}
