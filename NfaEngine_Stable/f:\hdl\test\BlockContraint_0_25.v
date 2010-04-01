module BlockContraint_0_25(out,char,in0, clk, en,rst);
	//3,3,/-°[\da-fA-F]/smit
	input [7:0] char;
	input clk,en,rst, in0;
	output out;

	or(w0,out_inc,in0);
	or (out_inc,w2);
	nor (rst_inc,w1,w2);
	CountCompUnit_0_25 c (out,clk,out_inc,en,rst,rst_inc);
	charBlock_100025_0 C0 (char_100025_0,char);
	charBlock_100025_1 C1 (char_100025_1,char);
	state_100025_1 S1 (w1,char_100025_0,clk,en,rst,w0);
	state_100025_2 S2 (w2,char_100025_1,clk,en,rst,w1);
endmodule

module CountCompUnit_0_25(out,clk,inc,en_in,rst,rst_inc);
	//Contraint repetition: 3,3,/-°[\da-fA-F]/smit
	parameter	K=2;
	parameter	M=3;
	parameter	N=3;
	parameter	G=1; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;

	input		inc, clk, en_in, rst, rst_inc;
	output		out;
	wire	compN, compM, en, mux_out;
	wire	[K-1:0]	cReg;

	counter_Kbit_0_25 count1(cReg,clk,en,inc,rst,rst_inc);
	assign compN = (cReg >= N);
	assign compM = (cReg <= M);
	assign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;
	assign out = mux_out;
	assign en = ((G==0)?!mux_out:1'b1) && en_in ;
endmodule

module counter_Kbit_0_25 (cReg,clk,en,inc,rst,rst_inc);
	parameter	K = 2;
	input	clk, inc, rst, rst_inc, en;
	output	[K-1:0] cReg;
	reg		[K-1:0] cReg;

	always @(negedge clk)
	begin
		if(rst == 1'b1)
			cReg <= 0;
		else if(rst_inc == 1'b1)
			cReg <= 0;
		else if(en == 1'b0)
			cReg <= cReg;
		else if(inc == 1'b1)
			cReg <= cReg + 1;
	end
endmodule

