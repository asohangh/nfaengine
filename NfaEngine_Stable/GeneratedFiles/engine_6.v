module engine_6(out,clk,sod,en, in_6_0, in_6_1, in_6_3, in_6_4, in_6_7, in_6_10, in_6_15, in_6_16, in_6_18, in_6_20, in_6_21, in_6_22, in_6_23, in_6_26, in_6_28, in_6_32, in_6_33);
//pcre: /\x2F°g°u°m°b°l°a°r°\x2E°c°n°\x2F°r°s°s°\x2F°\x3F°i°d°\x3D°\d+/smi
//block char: a[0], d[1], n[3], c[4], s[7], r[10], l[15], m[16], i[18], u[20], b[21], g[22], \x2E[23], \x2F[26], \x3F[28], \x3D[32], \d[33], 

	input clk,sod,en;

	input in_6_0, in_6_1, in_6_3, in_6_4, in_6_7, in_6_10, in_6_15, in_6_16, in_6_18, in_6_20, in_6_21, in_6_22, in_6_23, in_6_26, in_6_28, in_6_32, in_6_33;
	output out;


	state_6_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_6_1 BS_6_1 (w1,in_6_26,clk,en,sod,w0);
	state_6_2 BS_6_2 (w2,in_6_22,clk,en,sod,w1);
	state_6_3 BS_6_3 (w3,in_6_20,clk,en,sod,w2);
	state_6_4 BS_6_4 (w4,in_6_16,clk,en,sod,w3);
	state_6_5 BS_6_5 (w5,in_6_21,clk,en,sod,w4);
	state_6_6 BS_6_6 (w6,in_6_15,clk,en,sod,w5);
	state_6_7 BS_6_7 (w7,in_6_0,clk,en,sod,w6);
	state_6_8 BS_6_8 (w8,in_6_10,clk,en,sod,w7);
	state_6_9 BS_6_9 (w9,in_6_23,clk,en,sod,w8);
	state_6_10 BS_6_10 (w10,in_6_4,clk,en,sod,w9);
	state_6_11 BS_6_11 (w11,in_6_3,clk,en,sod,w10);
	state_6_12 BS_6_12 (w12,in_6_26,clk,en,sod,w11);
	state_6_13 BS_6_13 (w13,in_6_10,clk,en,sod,w12);
	state_6_14 BS_6_14 (w14,in_6_7,clk,en,sod,w13);
	state_6_15 BS_6_15 (w15,in_6_7,clk,en,sod,w14);
	state_6_16 BS_6_16 (w16,in_6_26,clk,en,sod,w15);
	state_6_17 BS_6_17 (w17,in_6_28,clk,en,sod,w16);
	state_6_18 BS_6_18 (w18,in_6_18,clk,en,sod,w17);
	state_6_19 BS_6_19 (w19,in_6_1,clk,en,sod,w18);
	state_6_20 BS_6_20 (w20,in_6_32,clk,en,sod,w19);
	state_6_21 BS_6_21 (w21,in_6_33,clk,en,sod,w21,w20);
	state_6_22 BS_6_22 (out,clk,en,sod,w21);
endmodule

module state_6_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_6_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_13(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_16(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_20(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_21(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_6_22(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

