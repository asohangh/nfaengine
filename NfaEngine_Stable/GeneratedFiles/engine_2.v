module engine_2(out,clk,sod,en, in_2_0, in_2_1, in_2_2, in_2_3, in_2_5, in_2_7, in_2_10, in_2_12, in_2_13, in_2_15, in_2_16, in_2_18, in_2_21, in_2_22, in_2_23, in_2_24, in_2_25);
//pcre: /l°i°b°M°a°n°a°g°e°r°\x2E°d°l°l°\x5E°g°e°t°(d°r°i°v°e°s|f°i°l°e°s)°\x2A/smi
//block char: a[0], d[1], v[2], n[3], e[5], s[7], r[10], t[12], f[13], l[15], M[16], i[18], b[21], g[22], \x2E[23], \x5E[24], \x2A[25], 

	input clk,sod,en;

	input in_2_0, in_2_1, in_2_2, in_2_3, in_2_5, in_2_7, in_2_10, in_2_12, in_2_13, in_2_15, in_2_16, in_2_18, in_2_21, in_2_22, in_2_23, in_2_24, in_2_25;
	output out;


	state_2_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_2_1 BS_2_1 (w1,in_2_15,clk,en,sod,w0);
	state_2_2 BS_2_2 (w2,in_2_18,clk,en,sod,w1);
	state_2_3 BS_2_3 (w3,in_2_21,clk,en,sod,w2);
	state_2_4 BS_2_4 (w4,in_2_16,clk,en,sod,w3);
	state_2_5 BS_2_5 (w5,in_2_0,clk,en,sod,w4);
	state_2_6 BS_2_6 (w6,in_2_3,clk,en,sod,w5);
	state_2_7 BS_2_7 (w7,in_2_0,clk,en,sod,w6);
	state_2_8 BS_2_8 (w8,in_2_22,clk,en,sod,w7);
	state_2_9 BS_2_9 (w9,in_2_5,clk,en,sod,w8);
	state_2_10 BS_2_10 (w10,in_2_10,clk,en,sod,w9);
	state_2_11 BS_2_11 (w11,in_2_23,clk,en,sod,w10);
	state_2_12 BS_2_12 (w12,in_2_1,clk,en,sod,w11);
	state_2_13 BS_2_13 (w13,in_2_15,clk,en,sod,w12);
	state_2_14 BS_2_14 (w14,in_2_15,clk,en,sod,w13);
	state_2_15 BS_2_15 (w15,in_2_24,clk,en,sod,w14);
	state_2_16 BS_2_16 (w16,in_2_22,clk,en,sod,w15);
	state_2_17 BS_2_17 (w17,in_2_5,clk,en,sod,w16);
	state_2_18 BS_2_18 (w18,in_2_12,clk,en,sod,w17);
	state_2_19 BS_2_19 (w19,in_2_1,clk,en,sod,w18);
	state_2_20 BS_2_20 (w20,in_2_10,clk,en,sod,w19);
	state_2_21 BS_2_21 (w21,in_2_18,clk,en,sod,w20);
	state_2_22 BS_2_22 (w22,in_2_2,clk,en,sod,w21);
	state_2_23 BS_2_23 (w23,in_2_5,clk,en,sod,w22);
	state_2_24 BS_2_24 (w24,in_2_7,clk,en,sod,w23);
	state_2_25 BS_2_25 (w25,in_2_25,clk,en,sod,w24,w30);
	state_2_26 BS_2_26 (w26,in_2_13,clk,en,sod,w18);
	state_2_27 BS_2_27 (w27,in_2_18,clk,en,sod,w26);
	state_2_28 BS_2_28 (w28,in_2_15,clk,en,sod,w27);
	state_2_29 BS_2_29 (w29,in_2_5,clk,en,sod,w28);
	state_2_30 BS_2_30 (w30,in_2_7,clk,en,sod,w29);
	state_2_31 BS_2_31 (out,clk,en,sod,w25);
endmodule

module state_2_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_2_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_13(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_16(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_20(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_21(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_22(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_23(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_24(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_25(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_26(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_27(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_28(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_29(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_30(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_2_31(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

