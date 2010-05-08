module engine_5(out,clk,sod,en, in_5_0, in_5_1, in_5_2, in_5_3, in_5_4, in_5_10, in_5_12, in_5_16, in_5_18, in_5_20, in_5_23, in_5_26, in_5_28, in_5_31, in_5_32, in_5_33);
//pcre: /\x2F°m°a°r°t°u°z°\x2E°c°n°\x2F°v°i°d°\x2F°\x3F°i°d°\x3D°\d+/smi
//block char: a[0], d[1], v[2], n[3], c[4], r[10], t[12], m[16], i[18], u[20], \x2E[23], \x2F[26], \x3F[28], z[31], \x3D[32], \d[33], 

	input clk,sod,en;

	input in_5_0, in_5_1, in_5_2, in_5_3, in_5_4, in_5_10, in_5_12, in_5_16, in_5_18, in_5_20, in_5_23, in_5_26, in_5_28, in_5_31, in_5_32, in_5_33;
	output out;


	state_5_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_5_1 BS_5_1 (w1,in_5_26,clk,en,sod,w0);
	state_5_2 BS_5_2 (w2,in_5_16,clk,en,sod,w1);
	state_5_3 BS_5_3 (w3,in_5_0,clk,en,sod,w2);
	state_5_4 BS_5_4 (w4,in_5_10,clk,en,sod,w3);
	state_5_5 BS_5_5 (w5,in_5_12,clk,en,sod,w4);
	state_5_6 BS_5_6 (w6,in_5_20,clk,en,sod,w5);
	state_5_7 BS_5_7 (w7,in_5_31,clk,en,sod,w6);
	state_5_8 BS_5_8 (w8,in_5_23,clk,en,sod,w7);
	state_5_9 BS_5_9 (w9,in_5_4,clk,en,sod,w8);
	state_5_10 BS_5_10 (w10,in_5_3,clk,en,sod,w9);
	state_5_11 BS_5_11 (w11,in_5_26,clk,en,sod,w10);
	state_5_12 BS_5_12 (w12,in_5_2,clk,en,sod,w11);
	state_5_13 BS_5_13 (w13,in_5_18,clk,en,sod,w12);
	state_5_14 BS_5_14 (w14,in_5_1,clk,en,sod,w13);
	state_5_15 BS_5_15 (w15,in_5_26,clk,en,sod,w14);
	state_5_16 BS_5_16 (w16,in_5_28,clk,en,sod,w15);
	state_5_17 BS_5_17 (w17,in_5_18,clk,en,sod,w16);
	state_5_18 BS_5_18 (w18,in_5_1,clk,en,sod,w17);
	state_5_19 BS_5_19 (w19,in_5_32,clk,en,sod,w18);
	state_5_20 BS_5_20 (w20,in_5_33,clk,en,sod,w20,w19);
	state_5_21 BS_5_21 (out,clk,en,sod,w20);
endmodule

module state_5_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_5_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_13(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_16(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_20(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_5_21(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

