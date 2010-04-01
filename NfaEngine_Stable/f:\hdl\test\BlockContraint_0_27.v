module BlockContraint_0_27(out,char,in0, clk, en,rst);
	//12,12,/[\d\z6166\z4146]/smit
	input [7:0] char;
	input clk,en,rst, in0;
	output out;

	or(w0,out_inc,in0);
	or (out_inc,w1);
	nor (rst_inc,w1);
	CountCompUnit_0_27 c (out,clk,out_inc,en,rst,rst_inc);
	charBlock_100027_0 C0 (char_100027_0,char);
	state_100027_1 S1 (w1,char_100027_0,clk,en,rst,w0);
endmodule

module CountCompUnit_0_27(out,clk,inc,en_in,rst,rst_inc);
	//Contraint repetition: 12,12,/[\d\z6166\z4146]/smit
	parameter	K=4;
	parameter	M=12;
	parameter	N=12;
	parameter	G=1; // g==0 is atmost, g==1 is exactly or between; g==2 is atleast;

	input		inc, clk, en_in, rst, rst_inc;
	output		out;
	wire	compN, compM, en, mux_out;
	wire	[K-1:0]	cReg;

	counter_Kbit_0_27 count1(cReg,clk,en,inc,rst,rst_inc);
	assign compN = (cReg >= N);
	assign compM = (cReg <= M);
	assign mux_out = (G==0)?compN:(G==1)?(compN && compM):compM;
	assign out = mux_out;
	assign en = ((G==0)?!mux_out:1'b1) && en_in ;
endmodule

module counter_Kbit_0_27 (cReg,clk,en,inc,rst,rst_inc);
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

