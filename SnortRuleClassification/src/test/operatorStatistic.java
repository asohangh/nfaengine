/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class operatorStatistic {

    private String outfolder = System.getProperty("user.dir") + File.separator + "output.2.9" + File.separator +"stats" +File.separator;

    public static void main(String[] args) throws IOException {
        operatorStatistic os = new operatorStatistic();
        os.action();
    }

    private void action() throws IOException {
        String filename ="allpcre.pcre";

        LinkedList<String> lstring = this.readfromfile(outfolder + filename);
        this.countBackreference(lstring, outfolder + filename + ".BR.count");
        //this.countConstraintRepetition(lstring,outfolder+ filename + ".CR.count")

    }

    private LinkedList<String> readfromfile(String string) throws IOException {
        LinkedList<String> ls = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(string));
        String s;
        while ((s = br.readLine()) != null) {
            ls.add(s);
        }
        br.close();
        return ls;
    }

    private void countBackreference(LinkedList<String> lstring, String file) throws IOException {
        int max = 15;
        LinkedList<String> lpcre = new LinkedList<String>();
        int[] count = new int[max];
        for (int i = 0; i < max; i++) {
            count[i] = 0;
        }


        for (int i = 0; i < lstring.size(); i++) {
            int[] array;
            String pcre = lstring.get(i);
            array = this.coutBackReferenceRule(pcre);
            //add to array
            for (int j = 0; j < max; j++) {
                count[j] = count[j] + array[j];
            }
            if (array[0] > 0) {
                count[max - 1]++;
                lpcre.add(pcre);
            }
        }

        //print
        for (int i = 0; i < (max); i++) {
            System.out.println("_\\" + i + " : " + count[i]);
        }
        //out to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        //write statistic:
        bw.write("noRule:" + count[max-1] + "\n");
        bw.write("noBackreference:" + count[0] + "\n");
        for (int i = 1; i < max - 1; i++) {
            bw.write("_ \\" + i + " : " + count[i] + "\n");
        }
        //write pcre:
        bw.write("#PCRE: \n");
        for (int i = 0; i < lpcre.size(); i++) {
            bw.write(lpcre.get(i) + "\n");
        }
        bw.flush();
        bw.close();

    }

    private int[] coutBackReferenceRule(String pcre) {
        int max = 15;
        int[] array = new int[max];
        for (int i = 0; i < max; i++) {
            array[i] = 0;
        }

        char chr;
        for (int i = 1; i < (pcre.length() - 1); i++) {
            chr = pcre.charAt(i);
            if (chr == '\\') {
                if (pcre.charAt(i - 1) != '\\' && Character.isDigit(pcre.charAt(i + 1))) {
                    String num = "" + pcre.charAt(i + 1);
                    if ((i + 2) < pcre.length() && Character.isDigit(pcre.charAt(i + 2))) {
                        num = num + pcre.charAt(i + 2);
                    }
                    if ((i + 3) < pcre.length() && Character.isDigit(pcre.charAt(i + 3))) {
                        num = num + pcre.charAt(i + 3);
                    }
                    if (num.length() <= 2) { //is backreference
                        array[Integer.parseInt(num)]++;
                    }
                }
            }
        }

        for (int i = 1; i < max; i++) {
            array[0] += array[i];
        }

        return array;
    }

    public class CROperator{
        
    }
    
    private void countConstraintRepetition(LinkedList<String> lstring, String file) {

    }
}
