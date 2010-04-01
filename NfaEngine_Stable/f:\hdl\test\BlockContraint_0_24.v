module BlockContraint_0_24(out,char,in0, clk, en,rst);
	//8,8,/[\d\z6166\z4146]/smit
	input [7:0] char;
	input clk,en,rst, in0;
	output out;

	or(w0,out_inc,in0);
	or (out_inc,w1);
	nor (rst_inc,w1);
	CountCompUnit_0_24 c (out,clk,out_inc,en,rst,rst_inc);
	charBlock_100024_0 C0 (char_100024_0,char);
	state_100024_1 S1 (w1,char_100024_0,clk,en,rst,w0);
endmodule

module CountCompUnit_0_24(out,clk,inc,en_in,rst,rst_inc);
	//Contraint repetition: 8,8,/[\d\z6166\z4146]/smit
	parameter	K=4;
	parameter	M=8;
	parameter	N=8;
	parameter	G=1; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;

	input		inc, clk, en_in, rst, rst_inc;
	output		out;
	wire	compN, compM, en, mux_out;
	wire	[K-1:0]	cReg;

	counter_Kbit_0_24 count1(cReg,clk,en,inc,rst,rst_inc);
	assign compN = (cReg >= N);
	assign compM = (cReg <= M);
	assign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;
	assign out = mux_out;
	assign en = ((G==0)?!mux_out:1'b1) && en_in ;
endmodule

module counter_Kbit_0_24 (cReg,clk,en,inc,rst,rst_inc);
	parameter	K = 4;
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

