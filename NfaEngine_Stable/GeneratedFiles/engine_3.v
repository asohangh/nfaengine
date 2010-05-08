module engine_3(out,clk,sod,en, in_3_0, in_3_2, in_3_3, in_3_5, in_3_6, in_3_7, in_3_10, in_3_11, in_3_12, in_3_13, in_3_15, in_3_18);
//pcre: /S°r°v°I°n°f°o°F°e°a°r°l°e°s°s°\s+°L°i°t°e°\s+°S°e°r°v°e°r/smi
//block char: a[0], v[2], n[3], e[5], \x20[6], S[7], r[10], o[11], t[12], f[13], l[15], I[18], 

	input clk,sod,en;

	input in_3_0, in_3_2, in_3_3, in_3_5, in_3_6, in_3_7, in_3_10, in_3_11, in_3_12, in_3_13, in_3_15, in_3_18;
	output out;


	state_3_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_3_1 BS_3_1 (w1,in_3_7,clk,en,sod,w0);
	state_3_2 BS_3_2 (w2,in_3_10,clk,en,sod,w1);
	state_3_3 BS_3_3 (w3,in_3_2,clk,en,sod,w2);
	state_3_4 BS_3_4 (w4,in_3_18,clk,en,sod,w3);
	state_3_5 BS_3_5 (w5,in_3_3,clk,en,sod,w4);
	state_3_6 BS_3_6 (w6,in_3_13,clk,en,sod,w5);
	state_3_7 BS_3_7 (w7,in_3_11,clk,en,sod,w6);
	state_3_8 BS_3_8 (w8,in_3_13,clk,en,sod,w7);
	state_3_9 BS_3_9 (w9,in_3_5,clk,en,sod,w8);
	state_3_10 BS_3_10 (w10,in_3_0,clk,en,sod,w9);
	state_3_11 BS_3_11 (w11,in_3_10,clk,en,sod,w10);
	state_3_12 BS_3_12 (w12,in_3_15,clk,en,sod,w11);
	state_3_13 BS_3_13 (w13,in_3_5,clk,en,sod,w12);
	state_3_14 BS_3_14 (w14,in_3_7,clk,en,sod,w13);
	state_3_15 BS_3_15 (w15,in_3_7,clk,en,sod,w14);
	state_3_16 BS_3_16 (w16,in_3_6,clk,en,sod,w16,w15);
	state_3_17 BS_3_17 (w17,in_3_15,clk,en,sod,w16);
	state_3_18 BS_3_18 (w18,in_3_18,clk,en,sod,w17);
	state_3_19 BS_3_19 (w19,in_3_12,clk,en,sod,w18);
	state_3_20 BS_3_20 (w20,in_3_5,clk,en,sod,w19);
	state_3_21 BS_3_21 (w21,in_3_6,clk,en,sod,w21,w20);
	state_3_22 BS_3_22 (w22,in_3_7,clk,en,sod,w21);
	state_3_23 BS_3_23 (w23,in_3_5,clk,en,sod,w22);
	state_3_24 BS_3_24 (w24,in_3_10,clk,en,sod,w23);
	state_3_25 BS_3_25 (w25,in_3_2,clk,en,sod,w24);
	state_3_26 BS_3_26 (w26,in_3_5,clk,en,sod,w25);
	state_3_27 BS_3_27 (w27,in_3_10,clk,en,sod,w26);
	state_3_28 BS_3_28 (out,clk,en,sod,w27);
endmodule

module state_3_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_3_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_8(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_11(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_13(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_15(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_16(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_20(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_21(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_22(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_23(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_24(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_25(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_26(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_27(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_3_28(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

