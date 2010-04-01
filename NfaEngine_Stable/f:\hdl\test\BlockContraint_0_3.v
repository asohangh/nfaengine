module BlockContraint_0_3(out,char,in0, clk, en,rst);
	//120,120,/m|n/smi
	input [7:0] char;
	input clk,en,rst, in0;
	output out;

	or(w0,out_inc,in0);
	or (out_inc,w1,w2);
	nor (rst_inc,w1,w2);
	CountCompUnit_0_3 c (out,clk,out_inc,en,rst,rst_inc);
	charBlock_100003_0 C0 (char_100003_0,char);
	charBlock_100003_1 C1 (char_100003_1,char);
	state_100003_1 S1 (w1,char_100003_0,clk,en,rst,w0);
	state_100003_2 S2 (w2,char_100003_1,clk,en,rst,w0);
endmodule

module CountCompUnit_0_3(out,clk,inc,en_in,rst,rst_inc);
	//Contraint repetition: 120,120,/m|n/smi
	parameter	K=7;
	parameter	M=120;
	parameter	N=120;
	parameter	G=1; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;

	input		inc, clk, en_in, rst, rst_inc;
	output		out;
	wire	compN, compM, en, mux_out;
	wire	[K-1:0]	cReg;

	counter_Kbit_0_3 count1(cReg,clk,en,inc,rst,rst_inc);
	assign compN = (cReg >= N);
	assign compM = (cReg <= M);
	assign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;
	assign out = mux_out;
	assign en = ((G==0)?!mux_out:1'b1) && en_in ;
endmodule

module counter_Kbit_0_3 (cReg,clk,en,inc,rst,rst_inc);
	parameter	K = 7;
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

