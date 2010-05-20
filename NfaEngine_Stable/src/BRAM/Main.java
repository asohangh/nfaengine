/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BRAM;

import NFA.*;
import engineRe.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import pcre.*;

/**
 *
 * @author Richard Le
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        boolean fromfile = true;
        if(fromfile){
            //String filename = System.getProperty("user.dir") + System.getProperty("file.separator") + "pcre20.4.mip.1.pcre";
            String filename = System.getProperty("user.dir") + System.getProperty("file.separator") + "pcre20.2.mip";
            Main.buildfromfile(filename);
            return;
        }
        String[] rule = new String[8];
         //String rule = "/(ga|at)((ag|aaa)*)/";
       rule[0] = "/(ga|at)((ag|aaa)*)cde/";
       rule[1] = "/b*c(a|b)*[ac]#Adf+/";
       rule[2] = "/ab[^cd][\\x3A2a]/";
       rule[3] = "/\\x2F(fn|s)\\x3F[\\r\\n]*si/smi";
        //rule[3] = "/^CSeq\\x3A[^\\r\\n]+[^\\x01-\\x08\\x0B1-8\\x0C\\128-\\011\\x0E-\\x1F\\126-\\127]/smi";
        rule[4] = "/A\\010[abc\\x3a]*b/smi";
        rule[5] = "/<window\\s+version\\s*=\\s*(\\?!(1\\.(0|2|4|5|6)))/smi";
        rule[6] = "/FTPON\\d+\\s+TIME\\d+\\s+/smi";
        rule[7] = "/Subject\\x3A[^\\r\\n]*2\\x2E41/smi";
        //ParseTree temp=new ParseTree("b*c(a|b)*[ac]#");
        ///FROM|3A|\\s+HTTP_RAT_.*SUBJECT|3A|\\s+there\\s+is\\s+a\\s+HTTPRAT\\s+waiting\\s+4\\s+u\\s+on/sm
        //temp.printTree();
        //String rule="/\\x3Ctitle\\x3ETroya\\s+\\x2D\\s+by\\s+Sma\\s+Soft\\x3C\\x2Ftitle\\x3E/smi";
        //String rule = "/\\x2F(fn|s)\\x3F[\\r\\n]*si/smi";
        //String rule = "/a\\010[abc\\x3a]*b/smi";
        //String rule = "/abc/";
        //String rule = "/ab{5,34}c*/smi";
        // String rule = "/[\\?\\x20\\x3b\\x26]module=[a-zA-Z0-9]*[^\\x3b\\x26]/U";
        //String rule = "/\\x2F[abcd]*a/smi";
        //String rule = "/ab[^cd][\\x3A2a]/smi";
        //String rule = "/ldap\\x3A\\x2F\\x2F[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)[^\\x0A]*(%3f|\\x3F)/smi";
        //String rule = "/(form|module|report)\\s*=\\s*(\\x2e\\x2e|\\x2f|[a-z]\\x3a\\x5c)/i";
        //System.out.print (rule.substring(1, rule.length() - 2));
        //String rule = "/[\\?\\x20\\x3b\\x26]module=[a-zA-Z0-9]*[^\\x3b\\x26]/";
        //System.out.print (rule);
        //String rule = "/^<window\\s+version\\s*=\\s*(\\?!(1\\.(0|2|4|5|6)))/smi";

        //String rule = "/a[a-z](m|n){120}z/smi";
        //String rule = "/^\\s*MAIL\\s+FROM\\s*\\x3A\\s*\\x3C?\\s*[^\\x3E\\s]{257}\\s*/mi";
        //String rule = "/^100013Agentsvr\\x5E\\x5EMerlin$/smi";
        // String rule = "/b*a{79}c/smi";
        //String rule = "/^Location\\x3a(\\s*|\\s*\\r?\\n\\s+)URL\\s*\\x3a/smi";
        //String rule ="/^Content-Disposition\\x3a(\\s*|\\s*\\r?\\n\\s+)[^\\r\\n]*\\{[\\da-fA-F]{8}(-[\\da-fA-F]){3}-[\\da-fA-F]{12}\\}/smi";
        //C°o°n°t°e°n°t°-°D°i°s°p°o°s°i°t°i°o°n°\x3a°(\s*|\s*°\r?°\n°\s+)°[^\r\n]*°\{°[\da-fA-F]{8}°(-°[\da-fA-F]{4}){3}°-°[\da-fA-F]{12}°\} -------- smit

        //String rule ="/^Content-Disposition\\x3a(\\s*|\\s*\\r?\\n\\s+)[^\\r\\n]*\\{[\\da-fA-F]{8}(-[\\da-fA-F]{4}){3}-[\\da-fA-F]{12}\\}/smi";
        //String rule = "/ab[^\\r\\n]/smi";
        //String rule = "/^CSeq\\x3A[^\\r\\n]+[^\\x01-\\x08\\x0B1-8\\x0C\\128-\\011\\x0E-\\x1F\\126-\\127]/smi";
        //String rule = "/abc[aA-G]";
        //String rule = "/ab{3}c/smi";

        /*String[] rule = new String[8];
        rule[1] = "/Welcome\\s+to\\s+the\\s+Omniquad\\s+File\\s+Transfer\\s+Server/smi";
        rule[3] = "/SrvInfoFearless\\s+Lite\\s+Server/smi";
        rule[2] = "/libManager\\x2Edll\\x5Eget(drives|files)\\x2A/smi";
        rule[0] = "/Advanced\\s+Spy\\s+Report\\s+for/smi";
        rule[4] = "/\\x2Fcbn\\x2F(c|b)\\.smx\\?[^\\r\\n]*u=/Ui";
        rule[5] = "/\\x2Fmartuz\\x2Ecn\\x2Fvid\\x2F\\x3Fid\\x3D\\d+/smi";
        rule[6] = "/\\x2Fgumblar\\x2Ecn\\x2Frss\\x2F\\x3Fid\\x3D\\d+/smi";
        rule[7] = "/Advanced\\s+Spy\\s+Report\\s+for/smi";*/
       // String[] rule = new String[3];
        
        BRAM bRam = new BRAM(0);
        LinkedList<ReEngine> engineList = new LinkedList<ReEngine>();
        //String folders = System.getProperty("user.dir") + System.getProperty("file.separator") + "test" + System.getProperty("file.separator");
        String folders = BRAM._outputFolder + File.separator;
        for (int i = 0; i < rule.length; i++) {
            ParseTree tree = new ParseTree(rule[i]);
            System.out.println("pcre is: " + tree.rule.getPattern() + " -------- " + tree.rule.getModifier());
            //tree.printTree();

            NFA nfa = new NFA();
            nfa.tree2NFA(tree);

            nfa.updateID();
            //System.out.println("Original NFA:");
            //nfa.print();

            nfa.deleteRedundantState();
            //System.out.println("Modified NFA:");
            //nfa.print();

            //System.out.println("Building Regular Expression Engine....:");

            ReEngine engine = new ReEngine();
            engine.createEngine(nfa);
            //engine.reduceBlockChar();

            bRam.addEngine(engine, i);
            engineList.add(engine);
            System.out.println("OK... ");
            engine.print();
            System.out.println("Build HDL ...");
            System.out.println("Finish");
        }
        bRam.unionCharBlocks();
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.print(temp.value + " ");
        }
        System.out.println("Width: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.println(temp.value);
            for (int j = 0; j < temp.listToState.size(); j++) {
                for (int k = 0; k < temp.listToState.get(j).size(); k++) {
                    BlockState tempState = (BlockState) temp.listToState.get(j).get(k);
                    System.out.println("Engine: " + temp.array_id[j] + " state: " + tempState.id);
                }
            }
        }
        bRam.fillEntryValue();
        //bRam.printBRam();
       // bRam.buildCOE();
       // bRam.buildHDL();
       // bRam.buildXCO();
       // bRam.buildCORE_RAM_HDL();
        bRam.buildNecessaryFiles();
        LinkedList <BRAM> bramList = new LinkedList<BRAM> ();
        bramList.add(bRam);
        Main.createTopEngineTogether(folders, bramList);

        System.out.println("\n\nResult: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            //System.out.print(this.blockCharList.get(i).value + " ");
            bRam.blockCharList.get(i).id = i;
            System.out.print(bRam.blockCharList.get(i).value + "[" + bRam.blockCharList.get(i).id +"] ");
        }
    }

    public static void buildfromfile(String name) throws IOException{
        BufferedReader bw = new BufferedReader(new FileReader(name));
        LinkedList<String[]> listBram = new LinkedList<String[]>();
        String temp;
        int id = -1;
        while((temp = bw.readLine()) != null){
            if(temp.isEmpty())
                continue;
            while(temp !=null && temp.trim().startsWith("#")){
                int index = temp.indexOf("#size");
                int size = Integer.parseInt(temp.substring(index+5));
                String[] listPcre = new String[size];
                index = 0;
                while((temp = bw.readLine())!= null){
                    if(temp.isEmpty())
                        continue;
                    if(temp.startsWith("#")){
                        listBram.add(listPcre);
                        break;
                    }

                    listPcre[index] = temp.trim();
                    index ++;
                }
                if(temp == null)
                    listBram.add(listPcre);
            }
        }
        System.out.println("Finish parse pcrefile: ");
        for(int i =0; i<listBram.size(); i++){

            String[] listPcre = listBram.get(i);
            System.out.println("BRAM " + i + " : ");
            for(int j =0; j<listPcre.length; j++){
                System.out.println(listPcre[j]);
            }
        }
        LinkedList<BRAM> bramlist = new LinkedList<BRAM>();
        for(int i =0; i< listBram.size(); i++){
            System.out.println("Bramxxx " + i);
            String[] listPcre = listBram.get(i);
            BRAM br = Main.createHDLFromListPCRE(listPcre, i);
            bramlist.add(br);
        }

        String folders = BRAM._outputFolder + File.separator;
        Main.createTopEngineTogether(folders, bramlist);
    }

    public static BRAM createHDLFromListPCRE(String[] rule, int index){
        BRAM bRam = new BRAM(index);
        LinkedList<ReEngine> engineList = new LinkedList<ReEngine>();
        //String folders = System.getProperty("user.dir") + System.getProperty("file.separator") + "test" + System.getProperty("file.separator");
        String folders = BRAM._outputFolder + File.separator;
        for (int i = 0; i < rule.length; i++) {
            ParseTree tree = new ParseTree(rule[i]);
            System.out.println("pcre is: " + tree.rule.getPattern() + " -------- " + tree.rule.getModifier());
            NFA nfa = new NFA();
            nfa.tree2NFA(tree);
            nfa.updateID();
            nfa.deleteRedundantState();

            ReEngine engine = new ReEngine();
            engine.createEngine(nfa);
            engine.id_ram = index;
            bRam.addEngine(engine, i);
            engineList.add(engine);

            engine.buildHDL();
            engine.print();
        }
        bRam.unionCharBlocks();
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.print(temp.value + " ");
        }
        System.out.println("Width: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.println(temp.value);
            for (int j = 0; j < temp.listToState.size(); j++) {
                for (int k = 0; k < temp.listToState.get(j).size(); k++) {
                    BlockState tempState = (BlockState) temp.listToState.get(j).get(k);
                    System.out.println("Engine: " + temp.array_id[j] + " state: " + tempState.id);
                }
            }
        }
        bRam.fillEntryValue();
        bRam.buildNecessaryFiles();
        System.out.println("\n\nResult: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            //System.out.print(this.blockCharList.get(i).value + " ");
            bRam.blockCharList.get(i).id = i;
            System.out.print(bRam.blockCharList.get(i).value + "[" + bRam.blockCharList.get(i).id +"] ");
        }
        return bRam;
    }


 /*  public static void createTopEngineTogether(String folder, LinkedList<ReEngine> engine)throws Exception{

        Main.doBuildInterfacer(folder);
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(folder + "top_engine.v"));
        //(out,clk,sod,en,char)
        bw.write("module top_engine(out,stop,clk_in,sod,en,char,eod);\n");
        bw.write("\tinput [7:0] char;\n\tinput clk_in,sod,en,eod;\n");
        bw.write("\toutput stop;\n ");
        bw.write("\twire [7:0] char_int;\n\twire en_int;\n");
        bw.write("\toutput ["+(engine.size()-1)+":0] out;\n\n");

        bw.write("\tassign clk = ~clk_in;\n");
        bw.write("\tinterfacer I1(stop,char_int,en_int,en,char,sod,eod,clk);\n");
        for(int j =0; j<engine.size(); j++){
            bw.write("\tengine_"+ j + " E_" + j +  " (out["+j+"],clk,sod,en_int,char_int);\n");
}

        bw.write("\nendmodule\n");
        bw.flush();
        bw.close();
    }*/
    public static int sumOfPCRE(LinkedList<BRAM> bramList) {
        int sum = 0;
        for(int i = 0; i < bramList.size(); i++) {
            BRAM temp = bramList.get(i);
            sum = sum + temp.engineList.size();
        }
        return sum;
    }

    public static void createTopEngineTogether(String folder, LinkedList<BRAM> bramList) {
        try {
            Main.doBuildInterfacer(folder);
            BufferedWriter bw;
            bw = new BufferedWriter(new FileWriter(folder + "top_engine.v"));
            bw.write("module top_engine(out,stop,clk_in,sod,en,char,eod);\n");
            bw.write("\tinput [7:0] char;\n\tinput clk_in,sod,en,eod;\n");
            bw.write("\toutput stop;\n ");
            bw.write("\twire [7:0] char_int;\n\twire en_int;\n");
            bw.write("\toutput ["+(sumOfPCRE(bramList)-1)+":0] out;\n\n");
            bw.write("\tassign clk = ~clk_in;\n");
            bw.write("\tinterfacer I1(stop,char_int,en_int,en,char,sod,eod,clk);\n");
            int offset = 0;
            for(int j = 0; j < bramList.size(); j++){
                BRAM temp = bramList.get(j);
                offset = offset + temp.engineList.size();
                bw.write("\tBRAM_"+ temp.ID + " blockram_" + temp.ID +  " (out["+(offset - 1)+ ":" + (offset - temp.engineList.size()) +"],clk,sod,en_int,char_int);\n");
            }

            bw.write("\nendmodule\n");
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

   public static void doBuildInterfacer(String folder)throws Exception{

      BufferedWriter bw = new BufferedWriter(new FileWriter(folder + "interfacer.v"));

       bw.write("module interfacer(stop,data,en,en_in,data_in,sod,eod,clk);\n" +
               "\tinput en_in,eod,clk,sod;\n" +
               "\tinput [7:0] data_in;\n" +
               "\toutput [7:0] data;\n" +
               "\toutput  stop,en;\n" +
               "\twire [8:0] buffer;\n" +
               "\twire [8:0] temp,temp1;\n" +
               "\twire sod_0,sod_1;\n\n" +
               "\tassign data = buffer[7:0];\n" +
               "\tor(en,sod_1,stop,buffer[8]);\n" +
               "\tdelay_1 de_1(sod_0,sod,clk);\n" +
               "\tdelay_1 de_2(sod_1,sod_0,clk);\n" +
               "\tcountforstop c1(stop,clk,sod,eod);\n" +
               "\tdelay de1(temp,{en_in,data_in},clk);\n" +
               "\tdelay de2(buffer,temp,clk);\n" +
               "\t//delay de3(buffer,temp1,clk);\n" +
               "endmodule\n\n" +
               "module delay(out,in,clk);\n" +
               "\tinput [8:0] in;\n" +
               "\toutput [8:0] out;\n" +
               "\tinput clk;\n" +
               "\treg [8:0] out;\n\n" +
               "\talways @(negedge clk)\n" +
               "\t\tbegin\n" +
               "\t\t\tout <= in;\n" +
               "\t\tend\n" +
               "endmodule\n\n" +
               "module delay_1(out,in,clk);\n" +
               "\tinput [0:0] in;\n" +
               "\toutput [0:0] out;\n" +
               "\tinput clk;\n" +
               "\treg [0:0] out;\n\n" +
               "\talways @(negedge clk)\n" +
               "\t\tbegin" +
               "\t\t\tout <= in;\n" +
               "\t\tend\n" +
               "endmodule\n\n" +
               "module countforstop(out,clk,rst_out,rst);\n" +
               "\tinput clk,rst,rst_out;\n" +
               "\toutput out;\n" +
               "\twire d_in;\n" +
               "\treg out;\n" +
               "\treg [2:0] count;\n" +
               "\tor(d_in,rst,out);\n" +
               "\talways @(posedge clk)\n" +
               "\t\tbegin\n" +
               "\t\t\tif(rst)\n" +
               "\t\t\t\tbegin\n" +
               "\t\t\t\t\tcount <= 3'b001;\n" +
               "\t\t\t\tend\n" +
               "\t\t\telse if(out == 1'b1)\n" +
               "\t\t\t\tbegin\n" +
               "\t\t\t\t\tcount <= count +1;\n" +
               "\t\t\t\tend\n" +
               "\t\t\telse\n" +
               "\t\t\t\tbegin\n" +
               "\t\t\t\t\tcount <= count;\n" +
               "\t\t\t\tend\n" +
               "\t\tend\n" +
               "\talways @(posedge clk)\n" +
               "\t\tbegin\n" +
               "\t\t\tif(rst_out)\n" +
               "\t\t\t\tout <= 1'b0;\n" +
               "\t\t\telse if(rst)\n" +
               "\t\t\t\tout <= 1'b1;\n" +
               "\t\t\telse if(count >= 3'b100)\n" +
               "\t\t\t\tout <= 1'b0;\n" +
               "\t\t\telse\n" +
               "\t\t\t\tout <= d_in;\n" +
               "\t\tend\n" +
               "endmodule\n\n");
               bw.flush();
               bw.close();
    }

}
