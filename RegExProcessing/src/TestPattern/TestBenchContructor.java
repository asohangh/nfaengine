/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPattern;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heckarim
 */
public class TestBenchContructor {

    public TestBenchContructor() {
    }

    /**
     * file testbench .v for simulation
     * @param file
     * @param tc
     */
    public static void genTestBench(String file, TestCase tc) {

        try {
            File fi = new File(file);
            String modulename = fi.getName();
            //ab.v => ab
            modulename = modulename.substring(0, modulename.length() - 2);

            BufferedWriter bw = new BufferedWriter(new FileWriter(fi));



            bw.write("`timescale 1ns / 1ps \n\n");

            bw.write("module "+ modulename + ";\n"
                    + "// Inputs\n"
                    + "reg i_clk;\n"
                    + "reg i_rst;\n"
                    + "reg [10:0] i_data;\n"
                    + "reg i_rd_en;\n"
                    + "// Outputs\n"
                    + "wire [9:0] o_dout;\n"
                    + "wire o_eod;\n"
                    + "wire [7:0] o_data;\n"
                    + "wire o_sod;\n"
                    + "wire [9:0] o_main_fifo;\n"
                    + "wire o_main_empty;\n"
                    + "wire [8:0] w_in;\n"
                    + "integer fd;\n"
                    + "// Instantiate the Unit Under Test (UUT)\n"
                    + "\ttop_nfa_one_rule uut (\n"
                    + "\t\t.o_dout(o_dout),\n"
                    + "\t\t.o_eod(o_eod),\n"
                    + "\t\t.o_data(o_data),\n"
                    + "\t\t.o_sod(o_sod),\n"
                    + "\t\t.o_main_fifo(o_main_fifo),\n"
                    + "\t\t.o_main_empty(o_main_empty),\n"
                    + "\t\t.i_clk(i_clk),\n"
                    + "\t\t.i_rst(i_rst),\n"
                    + "\t\t.i_data(i_data),\n"
                    + "\t\t//.w_in(w_in),\n"
                    + "\t\t.i_rd_en(i_rd_en)\n"
                    + "\t\t);\n"
                    + "\tinitial begin\n"
                    + "\t\t//i_clk, i_rst, i_ctr, i_char, o_mainfifo;\n"
                    + "\t\tfd = $fopen(\"" + modulename + "_sim.out\",\"w\");\n"
                    + "\t\t$fmonitor(fd,\"%d\",o_main_fifo);\n"
                    + "\t\t$monitor(\"%g %b %b %b %d\",$time, i_clk, i_rst, i_data, o_main_fifo);\n"
                    + "\t\t// Initialize Inputs\n"
                    + "\t\ti_clk = 0;\n"
                    + "\t\ti_rst = 0;\n"
                    + "\t\ti_data = 0;\n"
                    + "\t\ti_rd_en = 0;\n"
                    + "\t\t// Wait 100 ns for global reset to finish\n"
                    + "\t\t#200;\n"
                    + "\t\t#10 i_rst = 1;\n"
                    + "\t\t#15 i_rst = 0;\n"
                    + "\t\t// Add stimulus here\n"
                    + "\t\t#125 i_data = {8'd1,1'b1,1'b0,1'b0};//sod\n");
            // insert test case;
            for (int i = 0; i < tc.listPattern.size(); i++) {
                //        "#10 i_data = {8'd99,1'b0,1'b1,1'b0};//c\n" +
                Pattern pt = tc.listPattern.get(i);
                System.out.println("Gen Pattern: " + pt.data);
                for (int j = 0; j < pt.data.length(); j++) {
                    bw.write("\t\t#10 i_data = {8'd" + ((int) pt.data.charAt(j)) + ",1'b0,1'b1,1'b0};//" + pt.data.charAt(j) + "\n");
                }

            }

            bw.write("\t\t#10 i_data = {8'd0,1'b0,1'b1,1'b1};//eod\n"
                    + "\t\t#30 i_rd_en = 1'b1;\n"
                    + "\tend\n"
                    + "\tinitial begin\n"
                    + "\t\t#0 i_clk = 5;\n"
                    + "\t\tforever #5 i_clk = ~i_clk;\n"
                    + "\tend\n"
                    + "endmodule\n");

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }


    }

    /**
     * file matching output for comparing.
     * @param file
     * @param tc
     */
    public static void genOutputFile(String file, TestCase tc) {
        BufferedWriter bw = null;
        try {
            File fi = new File(file);
            bw = new BufferedWriter(new FileWriter(fi));

            for (int i = 0; i < tc.listMatchIndex.size(); i++) {
                bw.write(tc.listMatchIndex.get(i) + "\n");
            }

            bw.flush();
            bw.close();


        } catch (IOException ex) {
            Logger.getLogger(TestBenchContructor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(TestBenchContructor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
