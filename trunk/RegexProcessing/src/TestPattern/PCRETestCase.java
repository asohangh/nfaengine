/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPattern;

import java.io.File;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author heckarim
 *
 *
 * This class handles a set of PCRE and diverse test case
 */
public class PCRETestCase {

    LinkedList<PCREPatternCollection> listCollection = new LinkedList<PCREPatternCollection>();
    public LinkedList<TestCase> listTestCase = new LinkedList<TestCase>();
    Random rand = new Random();
    private String prefix;//use to gnerate file

    public PCRETestCase(String prefix) {
        this.prefix = prefix;
    }
    public PCRETestCase() {
        this.prefix = "defaultfile";
    }

    /**
     *
     * note:
     *  index from 1 -> xx
     *  if index <1 -> get last index of listcollection
     *
     * @param pcre
     * @param index
     *
     */
    public void addPCRE(String pcre, int index) {
        if (index < 0) {
            index = this.listCollection.size() + 1;
        }
       // System.out.println("PCRETestcase.java: add pcre: " + pcre + " .. " + index);
        PCREPatternCollection collec = new PCREPatternCollection(pcre, index);
        this.listCollection.add(collec);
    }

    /**
     * justgenearte simple testcase witch math
     */
    public void generateSimpleTestcase(int size){
        System.out.println("generateSimpleTestcase()");
        
       //generate testpattern
        for(int i =0; i<this.listCollection.size();i++){
            this.listCollection.get(i).generateMatchPattern(size);
        }
        //print out content of generated patern
        for(int i =0; i<this.listCollection.size();i++){
            PCREPatternCollection pc = this.listCollection.get(i);
            for(int j =0; j< pc.matchPattern.size(); j++){
                System.out.println(pc.matchPattern.get(j).data);
            }
        }

        //diverse testpattern
        //firsly is "size" time match
        for(int i =0; i<size; i++){
            TestCase tc = new TestCase();
            for(int j=0; j<this.listCollection.size(); j++){
                PCREPatternCollection collec = this.listCollection.get(j);
                tc.addPattern(collec.matchPattern.get(i),collec.index);
            }
            this.listTestCase.add(tc);
        }
    }
    /**
     * generate each pcre testpattern
     */
    public void generateTestcase(){
        System.out.println("generateTestcase()");
        int size = 5;
       //generate testpattern
        for(int i =0; i<this.listCollection.size();i++){
            this.listCollection.get(i).generateMatchPattern(size);
            this.listCollection.get(i).generateMissPattern(size);
        }

        for(int i =0; i<this.listCollection.size();i++){
            PCREPatternCollection pc = this.listCollection.get(i);
            for(int j =0; j< pc.matchPattern.size(); j++){
                System.out.println(pc.matchPattern.get(j));
            }
        }
        
        //diverse testpattern
        //firsly is "size" time match
        for(int i =0; i<size; i++){
            TestCase tc = new TestCase();
            for(int j=0; j<this.listCollection.size(); j++){
                PCREPatternCollection collec = this.listCollection.get(j);
                tc.addPattern(collec.matchPattern.get(i),collec.index);
                
            }
            this.listTestCase.add(tc);
        }
        //secondly is "2* size" time various match and miss match.
        for(int i =0; i<2*size; i++){
            TestCase tc = new TestCase();
            for(int j=0; j<this.listCollection.size(); j++){
                PCREPatternCollection collec = this.listCollection.get(j);
                //probability is 40 match 60 missmatch
                int p = rand.nextInt(10);
                if(p<4){//match
                    int p1 = rand.nextInt(size);
                    tc.addPattern(collec.matchPattern.get(p1),collec.index);
                }else{ //missmatch
                    int p1 = rand.nextInt(size);
                    tc.addPattern(collec.missPattern.get(p1),collec.index);
                }
            }
            this.listTestCase.add(tc);
        }
    }

    public void genTestbench(String folder){
        String fTB= folder;
        String fOut = folder;
        for(int i =0 ;i< this.listTestCase.size(); i++){
            TestCase tc = this.listTestCase.get(i);
            fTB = folder  + prefix + "_tb_"+i + ".v";
            fOut = folder  + prefix+ "_tb_" + i + ".out";
            TestBenchContructor.genTestBench(fTB, tc);
            TestBenchContructor.genOutputFile(fOut, tc);
        }
    }
}