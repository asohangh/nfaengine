/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engineRe;

import NFAold.NFA;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ParseTree.ParseTree;
import PCRE.Refer;

/**
 *
 * @author heckarim
 */
public class BlockContraint extends BlockState{


    public ReEngine ContEngine;
    public int m,n,g,k;

    public BlockContraint(BlockState bState){
        this.acceptChar = bState.acceptChar;
        this.comming = bState.comming;
        this.going = bState.going;
        this.id = bState.id;
        this.engine = bState.engine;
        this.isContraint = true;

        //Replace old blockState. in other blockstate
        // in comming list of other blockstate.
        for(int i=0; i<this.going.size(); i++){
            int num = this.going.get(i).comming.indexOf(bState);
            this.going.get(i).comming.remove(num);
            this.going.get(i).comming.add(num, this);
        }
        // in goint list of other block state.
        for(int i=0; i<this.comming.size(); i++){
            int num = this.comming.get(i).going.indexOf(bState);
            this.comming.get(i).going.remove(num);
            this.comming.get(i).going.add(num, this);
        }
        // add to listConrep.
        this.engine.listConRep.add(this);

        this.constructReEngine();

        //now remove acceptchar of this
        bState.engine.listBlockChar.remove(bState.acceptChar);

        // now add up listblockchar.
        for(int i = 0; i<this.ContEngine.listBlockChar.size(); i++){
            this.engine.listBlockChar.add(this.ContEngine.listBlockChar.get(i));
        }




    }

    public void constructReEngine(){
        String rule;
        String s[] = this.acceptChar.value.split(",");
        this.m = Integer.parseInt(s[1]);
        this.n = Integer.parseInt(s[0]);
        int max= Math.max(n, m);
        this.k = (int)(Math.floor(Math.log(max)/Math.log(2)) +1);

        if(this.acceptChar.code_id == Refer._op_atleast)
            this.g = 0;
        else
            this.g = 1;

        PCRE.Refer.println("Create BlockConstraint: " + this.acceptChar.value , this.engine.document);
        PCRE.Refer.println("\tm,n,g,k: " + m +" "+ n+" "+g+" "+k,this.engine.document );
        rule = this.acceptChar.value.substring(this.acceptChar.value.indexOf("/"));

        ParseTree tempTree = new ParseTree(rule);
        NFA nfa = new NFA();
        nfa.tree2NFA(tempTree);
        nfa.updateID();
        PCRE.Refer.println("\tCreate NFA for Constraint Repetition: ",this.engine.document);
        PCRE.Refer.println("\tOriginal NFA: ",this.engine.document);
        nfa.print();

        nfa.deleteRedundantState();
        PCRE.Refer.println("\tModified NFA:",this.engine.document);
        nfa.print();

        PCRE.Refer.println("\tBuilding Regular Expression Engine....:",this.engine.document);
        this.ContEngine = new ReEngine();

        
        ContEngine.createEngine(nfa);

        //ContEngine.isInsideConRep = true;
        //ContEngine.ConRepBlock = this;



        PCRE.Refer.println("\tOK... ",this.engine.document);
        ContEngine.print();
        //this.ContEngine.buildHDL(this.engine._outputfolder);

        
        //pcre.Refer.println("Build engine_"+this.ContEngine.id_num,this.engine.document);
       // this.buildHDLBlockContraint();
    }

    /**
     *
     */
    @Override
    public void buildHDL(){
        //this.ContEngine.buildHDL(this.engine._outputfolder);
        //pcre.Refer.println("Build engine_"+this.ContEngine.id_num,this.engine.document);
        this.buildHDLBlockContraint();
        
    }


    /**
     *
     * module blockContraint_0_id (out, in, clk);
            //input [7:0] char;
            input clk;
            input [...]in;
            output out;

            or(w0,outinc,in);
            or(out_inc,...comingblockend);
            nor(rst_inc,..outcuablokstate)
            counter (out,...);

            charBlock_0_0_109 (char_0_0_109,char);
            charBlock_0_2_99 (char_0_2_99,char);

            //state_0_0 (w0,clk,1);
            state_0_1 (w1,char_0_0_109,clk,w0);
            state_0_2 (w2,char_0_1_null,clk,w1);
            state_0_3 (w3,char_0_2_99,clk,w2);
            //state_0_4 (out,clk,w3);
        endmodule
     */
    public void buildHDLBlockContraint(){
        this.ContEngine.id_num = (this.engine.id_num+1) *100 + this.id;
        this.ContEngine.id_ram = this.engine.id_ram;
        PCRE.Refer.println("build block constraint " + this.engine._outputfolder,this.engine.document);
        try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(this.engine._outputfolder + System.getProperty("file.separator") +  "BlockConRep_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id +".v"));
            bw.write("\tmodule BlockConRep_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id +  " (out" );
            // need insert character
            for(int j =0; j < this.ContEngine.listBlockChar.size(); j++){
                bw.write(",char_" + this.ContEngine.listBlockChar.get(j).id);
            }

            bw.write(",clk,en,rst");
            for (int i = 0; i < this.comming.size(); i ++)
                bw.write (",in" + i);
            bw.write(");\n");

            bw.write("\t//" + this.acceptChar.value + "\n");
            bw.write("\tinput clk,en,rst,in0");
            for (int i = 1; i < this.comming.size(); i ++)
                bw.write (",in" + i);
            for(int j =0; j < this.ContEngine.listBlockChar.size(); j++){
                bw.write(",char_" + this.ContEngine.listBlockChar.get(j).id);
            }
            bw.write (";\n\toutput out;\n");
            bw.write("wire w0");
            for (int j = 0; j < this.ContEngine.listBlockState.size(); j++)
            {
                BlockState bt = this.ContEngine.listBlockState.get(j);
                if(bt.isEnd || bt.isStart)
                    continue;
                bw.write(",w" + bt.id);
            }
            bw.write(";\n\n");



            if(this.comming.size() >= 1){
                bw.write("or(w0,out_inc");
                for (int i = 0; i < this.comming.size(); i ++)
                    bw.write (",in" + i);
                bw.write(");\n");
            }else
                bw.write("assign w0 = out_inc;\n");

            
            if(this.ContEngine.end.comming.size() >=2){
                bw.write("or (out_inc");
	        for (int j = 0; j < this.ContEngine.end.comming.size(); j++)
	        {
                    bw.write(",w" + this.ContEngine.end.comming.get(j).id);
	        }
	        bw.write(");\n");
            }else
                bw.write("assign out_inc = w" + this.ContEngine.end.comming.get(0).id + ";\n");


            if(this.ContEngine.listBlockState.size() > 3){
                bw.write("nor (rst_inc");
                for (int j = 0; j < this.ContEngine.listBlockState.size(); j++)
                    {
                    BlockState bt = this.ContEngine.listBlockState.get(j);
                    if(bt.isEnd || bt.isStart)
                        continue;
                    bw.write(",w" + bt.id);
                    }
                bw.write(");\n");
            }else{
                for (int j = 0; j < this.ContEngine.listBlockState.size(); j++)
                {
                    BlockState bt = this.ContEngine.listBlockState.get(j);
                    if(bt.isEnd || bt.isStart)
                        continue;

                        bw.write("assign rst_inc = ~w" + bt.id +";\n");
                }
            }
            

            bw.write("\tCountCompUnit_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id + " c (out,clk,out_inc,en,rst,rst_inc);\n");
            int size;
            //modified code
           /* int size = this.ContEngine.listBlockChar.size();
            pcre.Refer.println("list block char size: "+this.ContEngine.listBlockChar.size(),this.engine.document);
            for (int i = 0; i < size; i ++)
            {
                BlockChar bc = this.ContEngine.listBlockChar.get(i);
                bw.write ("\tcharBlock_" + this.ContEngine.id_num + "_" + bc.id + " C"+ i + " (char_" + this.ContEngine.id_num + "_" + bc.id + ",char);\n");
            }
            */
            size = this.ContEngine.listBlockState.size();
            PCRE.Refer.print(size + "<- size\n",this.engine.document);
            for (int i = 1; i < size; i++)
            {
                BlockState bt = this.ContEngine.listBlockState.get(i);
               if(bt.isEnd){
            	  continue;
               }else{

                    bw.write("\tstate_" + this.engine.id_ram + "_" + this.ContEngine.id_num + "_" +
                            bt.id + " BS_" + this.engine.id_ram + "_" + this.ContEngine.id_num + "_" +
                            bt.id + " (w" + bt.id + ",char_" + bt.acceptChar.id + ",clk,en,rst");
                    for (int j = 0; j < bt.comming.size(); j++) {
                        bw.write(",w" + bt.comming.get(j).id);
                    }
                    bw.write(");\n");


               }
            }
            bw.write("endmodule\n\n");

            this.buildState(bw);
            this.buildCountCompUnit(bw);
            bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void buildState(BufferedWriter bw) throws IOException{
        for (int i = 0; i < this.ContEngine.listBlockState.size(); i++) {
            if(this.ContEngine.listBlockState.get(i).isStart
                    || this.ContEngine.listBlockState.get(i).isEnd){
                continue;
            }else if (this.ContEngine.listBlockState.get(i).isContraint) {
                    //We don't support it, =.=".
            }else {
                this.ContEngine.listBlockState.get(i).buildHDL(bw);
            }
        }
        
    }


    public void buildCountCompUnit(BufferedWriter bw){
        try {
            bw.write("module CountCompUnit_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id + "(out,clk,inc,en_in,rst,rst_inc);\n");
            bw.write("\t//Contraint repetition: " + this.acceptChar.value);
            bw.write("\n\tparameter\tK="+this.k+";\n");
            bw.write("\tparameter\tM="+this.m+";\n");
            bw.write("\tparameter\tN="+this.n+";\n");
            bw.write("\tparameter\tG="+this.g+"; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;\n");

            bw.write("\n\tinput\t\tinc, clk, en_in, rst, rst_inc;\n");
            bw.write("\toutput\t\tout;\n");
            bw.write("\twire\tcompN, compM, en, mux_out;\n");
            bw.write("\twire\t[K-1:0]\tcReg;\n\n");

            bw.write("\tcounter_Kbit_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id + " count1(cReg,clk,en,inc,rst,rst_inc);\n");
            bw.write("\tassign compN = (cReg >= N);\n");
            bw.write("\tassign compM = (cReg <= M);\n");
            bw.write("\tassign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;\n");
            bw.write("\tassign out = mux_out;\n");
            bw.write("\tassign en = ((G==0)?!mux_out:1'b1) && en_in ;\n");
            bw.write("endmodule\n\n");

            bw.write("module counter_Kbit_" + this.engine.id_ram + "_" + this.engine.id_num + "_" + this.id + " (cReg,clk,en,inc,rst,rst_inc);\n");
            bw.write("\tparameter\tK = "+this.k+";\n");
            bw.write("\tinput\tclk, inc, rst, rst_inc, en;\n");
            bw.write("\toutput\t[K-1:0] cReg;\n");
            bw.write("\treg\t\t[K-1:0] cReg;\n");
            bw.write("\n\talways @(negedge clk)\n");// or posedge rst or posedge rst_inc)\n");
            bw.write("\tbegin\n");
            bw.write("\t\tif(rst == 1'b1)\n");
            bw.write("\t\t\tcReg <= 0;\n");
            bw.write("\t\telse if(rst_inc == 1'b1)\n");
            bw.write("\t\t\tcReg <= 0;\n");
            bw.write("\t\telse if(en == 1'b0)\n");
            bw.write("\t\t\tcReg <= cReg;\n");
            bw.write("\t\telse if(inc == 1'b1)\n");
            bw.write("\t\t\tcReg <= cReg + 1;\n");
            bw.write("\tend\n");
            bw.write("endmodule\n\n");

        } catch (IOException ex) {
            Logger.getLogger(BlockContraint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
