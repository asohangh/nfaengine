`timescale 1ns / 1ps

////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer:
//
// Create Date:   13:44:29 12/18/2010
// Design Name:   Boundary
// Module Name:   /media/DATA/Xilinx/netFPGA/tb_Boundary_Loc.v
// Project Name:  netFPGA
// Target Device:  
// Tool versions:  
// Description: 
//
// Verilog Test Fixture created by ISE for module: Boundary
//
// Dependencies:
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
////////////////////////////////////////////////////////////////////////////////

module tb_Boundary_
//xxxtmip

	// Inputs
	reg rx_ll_reset;
	reg [7:0] rx_ll_data_in;
	reg rx_ll_sof_in_n;
	reg rx_ll_eof_in_n;
	reg rx_ll_src_rdy_n;
	reg clk;
	reg rst;

	// Outputs
	wire rx_ll_dst_rdy_n;
	wire [7:0] oHead;
	wire HSod;
	wire HEn;
	wire HEod;
	wire [7:0] oCont;
	wire CSod;
	wire CEn;
	wire CEod;
	wire o_sod;
	wire o_enable;
	wire o_eod;
	wire [7:0] o_data;
	wire o_empty;
	wire o_rd_en;
	wire o_main_empty;
	wire [9:0] o_main_fifo;
	wire [46:0] o_dout;
	wire [8:0] Pre_CID_out;
	wire Pre_dvld_out;
	wire Pre_sop_out;
	
	integer fd;

	// Instantiate the Unit Under Test (UUT)
	Boundary uut (
		.rx_ll_reset(rx_ll_reset), 
		.rx_ll_data_in(rx_ll_data_in), 
		.rx_ll_sof_in_n(rx_ll_sof_in_n), 
		.rx_ll_eof_in_n(rx_ll_eof_in_n), 
		.rx_ll_src_rdy_n(rx_ll_src_rdy_n), 
		.rx_ll_dst_rdy_n(rx_ll_dst_rdy_n), 
		.oHead(oHead), 
		.HSod(HSod), 
		.HEn(HEn), 
		.HEod(HEod), 
		.oCont(oCont), 
		.CSod(CSod), 
		.CEn(CEn), 
		.CEod(CEod), 
		.clk(clk), 
		.rst(rst), 
		.o_sod(o_sod), 
		.o_enable(o_enable), 
		.o_eod(o_eod), 
		.o_data(o_data), 
		.o_empty(o_empty), 
		.o_rd_en(o_rd_en), 
		.o_main_empty(o_main_empty), 
		.o_main_fifo(o_main_fifo), 
		.o_dout(o_dout), 
		.Pre_CID_out(Pre_CID_out), 
		.Pre_dvld_out(Pre_dvld_out), 
		.Pre_sop_out(Pre_sop_out)
	);

	initial begin
		fd = $fopen("tb_boundary_loc.out","w");
		$fmonitor(fd,"%d",o_main_fifo);
		$monitor("%g %b %d",$time, clk, o_main_fifo);
	
		// Initialize Inputs
		rx_ll_reset = 0;
		rx_ll_data_in = 0;
		rx_ll_sof_in_n = 1;
		rx_ll_eof_in_n = 1;
		rx_ll_src_rdy_n = 1;
		clk = 0;
		rst = 0;
//		i_rd_en = 0;
		// Wait 100 ns for global reset to finish
		#100;
		#10 rx_ll_reset = 0;
		rx_ll_data_in = 0;
		rx_ll_sof_in_n = 1;
		rx_ll_eof_in_n = 1;
		rx_ll_src_rdy_n = 1;
		//HStop = 0;
		//CStop = 0;
		clk = 0;
		rst = 0;
		// Wait 100 ns for global reset to finish
		#100;
		#5;
		rx_ll_reset = 1;
		#10 rx_ll_reset = 0;
        
		// Add stimulus here
//xxxtmip
		// Add stimulus here

	end
       
	initial begin
		#5 clk = 0;
		forever  #5 clk = ~clk;
	end
      
endmodule

