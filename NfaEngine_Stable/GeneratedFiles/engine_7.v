module engine_7(out,clk,sod,en, in_7_0, in_7_1, in_7_2, in_7_3, in_7_4, in_7_5, in_7_6, in_7_7, in_7_8, in_7_9, in_7_10, in_7_11, in_7_12, in_7_13);
//pcre: /A°d°v°a°n°c°e°d°\s+°S°p°y°\s+°R°e°p°o°r°t°\s+°f°o°r/smi
//block char: A[0], d[1], v[2], n[3], c[4], e[5], \x20[6], S[7], p[8], y[9], R[10], o[11], t[12], f[13], 

	input clk,sod,en;

	input in_7_0, in_7_1, in_7_2, in_7_3, in_7_4, in_7_5, in_7_6, in_7_7, in_7_8, in_7_9, in_7_10, in_7_11, in_7_12, in_7_13;
	output out;


	state_7_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_7_1 BS_7_1 (w1,in_7_0,clk,en,sod,w0);
	state_7_2 BS_7_2 (w2,in_7_1,clk,en,sod,w1);
	state_7_3 BS_7_3 (w3,in_7_2,clk,en,sod,w2);
	state_7_4 BS_7_4 (w4,in_7_0,clk,en,sod,w3);
	state_7_5 BS_7_5 (w5,in_7_3,clk,en,sod,w4);
	state_7_6 BS_7_6 (w6,in_7_4,clk,en,sod,w5);
	state_7_7 BS_7_7 (w7,in_7_5,clk,en,sod,w6);
	state_7_8 BS_7_8 (w8,in_7_1,clk,en,sod,w7);
	state_7_9 BS_7_9 (w9,in_7_6,clk,en,sod,w9,w8);
	state_7_10 BS_7_10 (w10,in_7_7,clk,en,sod,w9);
	state_7_11 BS_7_11 (w11,in_7_8,clk,en,sod,w10);
	state_7_12 BS_7_12 (w12,in_7_9,clk,en,sod,w11);
	state_7_13 BS_7_13 (w13,in_7_6,clk,en,sod,w13,w12);
	state_7_14 BS_7_14 (w14,in_7_10,clk,en,sod,w13);
	state_7_15 BS_7_15 (w15,in_7_5,clk,en,sod,w14);
	state_7_16 BS_7_16 (w16,in_7_8,clk,en,sod,w15);
	state_7_17 BS_7_17 (w17,in_7_11,clk,en,sod,w16);
	state_7_18 BS_7_18 (w18,in_7_10,clk,en,sod,w17);
	state_7_19 BS_7_19 (w19,in_7_12,clk,en,sod,w18);
	state_7_20 BS_7_20 (w20,in_7_6,clk,en,sod,w20,w19);
	state_7_21 BS_7_21 (w21,in_7_13,clk,en,sod,w20);
	state_7_22 BS_7_22 (w22,in_7_11,clk,en,sod,w21);
	state_7_23 BS_7_23 (w23,in_7_10,clk,en,sod,w22);
	state_7_24 BS_7_24 (out,clk,en,sod,w23);
endmodule

module state_7_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_7_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_9(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_13(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_16(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_20(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_21(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_22(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_23(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_7_24(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

