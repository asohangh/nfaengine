package RegexEngine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 *	Class cac doi tuong dai dien cho trang thai theo mo hinh Compact Architecture for High-Throughput
 *   yeyang, weirongj, prasanna
 */
public class BlockState {
    // essential attribute

    public LinkedList<BlockState> comming; //list cac block state toi no, day cung chinh la cac trang thai se OR lai voi nhau
    public BlockChar acceptChar; // BlockChar su dung voi AND
    public LinkedList<BlockState> going; //list cac block state no se di toi
    // type of BlockState.
    public boolean isStart;// block start ko co acceptchar va khong co comming
    public boolean isEnd; // block end ko co going va khong co acceptchar.
    public boolean isConRep; // is constraint repetition operator block.
    // orther attribute
    public ReEngine engine;
    public int order;

    public BlockState() {
        this.isStart = false;
        this.isEnd = false;
        this.acceptChar = null;
        this.comming = null;
        this.going = null;
    }

    BlockState(int type, ReEngine engine) {
        this.engine = engine;
        if (type == ReEngine._start) {
            this.isStart = true;
            this.isEnd = false;
            this.acceptChar = null;
            this.going = new LinkedList<BlockState>();
            this.comming = null;
        } else if (type == ReEngine._end) {
            this.isStart = false;
            this.isEnd = true;
            this.acceptChar = null;
            this.going = null;
            this.comming = new LinkedList<BlockState>();
        } else if (type == ReEngine._normal) {
            this.isStart = false;
            this.isEnd = false;
            this.acceptChar = null;
            this.comming = new LinkedList<BlockState>();
            this.going = new LinkedList<BlockState>();
        }
    }

    public void printTest(){
        System.out.println("this is BlockState");
    }

     /**
     * Build blockStart
     * @param bw
     * @throws java.io.IOException
     */
    public void buildHDL_start(BufferedWriter bw) throws IOException{
        bw.write("module state_" + this.engine.ram_id + "_" + this.engine.order + "_"+ this.order + "(out1,in1,clk,en,rst");
        bw.write(");\n");
        bw.write("\tinput in1,clk,rst,en");
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\tmyDff Dff (out1,in1,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
                ".INIT(1'b0)" +
                ") FDCE_inst (\n" +
                "\t\t.Q(out1),\n" +
                "\t\t.C(clk),\n" +
                "\t\t.CE(en),\n" +
                "\t\t.CLR(rst),\n" +
                "\t\t.D(in1)\n" +
                ");\n");
         *
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     * Build block End
     * @param bw
     * @throws java.io.IOException
     */
    public void buildHDL_End(BufferedWriter bw) throws IOException{
        int inputsize=this.comming.size();
        bw.write("module state_" + this.engine.ram_id + "_" + this.engine.order + "_"+ this.order + "(out1,clk,en,rst");

        for(int i=0;i<inputsize;i++){
            bw.write(",in"+i);
        }
        bw.write(");\n");
        bw.write("\tinput clk,rst,en");
        for(int i=0;i<inputsize;i++){
            bw.write(",in"+i);
        }
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\twire w1;\n");
        if(inputsize>1){
            bw.write("\tor(w1,out1");
            for(int i=0;i<inputsize;i++){
                bw.write(",in"+i);
            }
            bw.write(");\n");

        }else{
            bw.write("\tor(w1,out1,in0);\n");
        }
        bw.write("\tmyDff Dff (out1,w1,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
                ".INIT(1'b0)" +
                ") FDCE_inst (\n" +
                "\t\t.Q(out1),\n" +
                "\t\t.C(clk),\n" +
                "\t\t.CE(en),\n" +
                "\t\t.CLR(rst),\n" +
                "\t\t.D(w1)\n" +
                ");\n");
         *
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }


    /**
     * Build normal blockState
     * Example:
     *  module state_0_1(out1,in_char,clk,en,rst,in0);
            input in_char,clk,en,rst,in0;
            output out1;
            wire w1,w2;
            or(w1,in0);
            and(w2,in_char,w1);
            myDff(out1,w2,clk,en,rst);
        endmodule
     */

    public void buildHDL(BufferedWriter bw) throws IOException{
        if(this.isStart){
            this.buildHDL_start(bw);
            return;
        }
        if(this.isEnd){
            this.buildHDL_End(bw);
            return;
        }

        int inputsize=this.comming.size();

        bw.write("module state_" + this.engine.ram_id + "_" + this.engine.order + "_"+ this.order + "(out1,in_char,clk,en,rst");

        for(int i=0;i<inputsize;i++){
            bw.write(",in"+i);
        }

        bw.write(");\n");
        bw.write("\tinput in_char,clk,en,rst");
        for(int i=0;i<inputsize;i++){
            bw.write(",in"+i);
        }
        bw.write(";\n");

        bw.write("\toutput out1;\n");
        bw.write("\twire w1,w2;\n");

        if(inputsize >1){
            bw.write("\tor(w1");
            for(int i=0;i<inputsize;i++){
                bw.write(",in"+i);
            }
            bw.write(");\n");
        }else{
            bw.write("\tassign w1 = in0; \n");
        }

        bw.write("\tand(w2,in_char,w1);\n");
        bw.write("\tmyDff Dff (out1,w2,clk,en,rst);\n");
        /*bw.write("\tFDCE #(" +
                ".INIT(1'b0)" +
                ") FDCE_inst (\n" +
                "\t\t.Q(out1),\n" +
                "\t\t.C(clk),\n" +
                "\t\t.CE(en),\n" +
                "\t\t.CLR(rst),\n" +
                "\t\t.D(w2)\n" +
                ");\n");
         * 
         */
        bw.write("endmodule\n\n");
        bw.flush();
    }

    /**
     * This function for BlockContraint.
     */
    public void buildHDL(){
            //This function exist in BlockConstraint
    }
}