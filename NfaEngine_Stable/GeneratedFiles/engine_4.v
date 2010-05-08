module engine_4(out,clk,sod,en, in_4_3, in_4_4, in_4_7, in_4_16, in_4_20, in_4_21, in_4_23, in_4_26, in_4_27, in_4_28, in_4_29, in_4_30);
//pcre: /\x2F°c°b°n°\x2F°(c|b)°\.°s°m°x°\?°[^\r\n]*°u°=/Ui
//block char: n[3], c[4], s[7], m[16], u[20], b[21], \x2E[23], \x2F[26], x[27], \x3F[28], [^\r\n][29], =[30], 

	input clk,sod,en;

	input in_4_3, in_4_4, in_4_7, in_4_16, in_4_20, in_4_21, in_4_23, in_4_26, in_4_27, in_4_28, in_4_29, in_4_30;
	output out;


	state_4_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_4_1 BS_4_1 (w1,in_4_26,clk,en,sod,w0);
	state_4_2 BS_4_2 (w2,in_4_4,clk,en,sod,w1);
	state_4_3 BS_4_3 (w3,in_4_21,clk,en,sod,w2);
	state_4_4 BS_4_4 (w4,in_4_3,clk,en,sod,w3);
	state_4_5 BS_4_5 (w5,in_4_26,clk,en,sod,w4);
	state_4_6 BS_4_6 (w6,in_4_4,clk,en,sod,w5);
	state_4_7 BS_4_7 (w7,in_4_23,clk,en,sod,w6,w15);
	state_4_8 BS_4_8 (w8,in_4_7,clk,en,sod,w7);
	state_4_9 BS_4_9 (w9,in_4_16,clk,en,sod,w8);
	state_4_10 BS_4_10 (w10,in_4_27,clk,en,sod,w9);
	state_4_11 BS_4_11 (w11,in_4_28,clk,en,sod,w10);
	state_4_12 BS_4_12 (w12,in_4_29,clk,en,sod,w12,w11);
	state_4_13 BS_4_13 (w13,in_4_20,clk,en,sod,w12,w11);
	state_4_14 BS_4_14 (w14,in_4_30,clk,en,sod,w13);
	state_4_15 BS_4_15 (w15,in_4_21,clk,en,sod,w5);
	state_4_16 BS_4_16 (out,clk,en,sod,w14);
endmodule

module state_4_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_4_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_7(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_12(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_13(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_4_16(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

