module engine_1(out,clk,sod,en, in_1_0, in_1_1, in_1_2, in_1_3, in_1_4, in_1_5, in_1_6, in_1_7, in_1_10, in_1_11, in_1_12, in_1_13, in_1_14, in_1_15, in_1_16, in_1_17, in_1_18, in_1_19, in_1_20);
//pcre: /W°e°l°c°o°m°e°\s+°t°o°\s+°t°h°e°\s+°O°m°n°i°q°u°a°d°\s+°F°i°l°e°\s+°T°r°a°n°s°f°e°r°\s+°S°e°r°v°e°r/smi
//block char: a[0], d[1], v[2], n[3], c[4], e[5], \x20[6], s[7], r[10], o[11], t[12], F[13], W[14], l[15], m[16], h[17], i[18], q[19], u[20], 

	input clk,sod,en;

	input in_1_0, in_1_1, in_1_2, in_1_3, in_1_4, in_1_5, in_1_6, in_1_7, in_1_10, in_1_11, in_1_12, in_1_13, in_1_14, in_1_15, in_1_16, in_1_17, in_1_18, in_1_19, in_1_20;
	output out;


	state_1_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_1_1 BS_1_1 (w1,in_1_14,clk,en,sod,w0);
	state_1_2 BS_1_2 (w2,in_1_5,clk,en,sod,w1);
	state_1_3 BS_1_3 (w3,in_1_15,clk,en,sod,w2);
	state_1_4 BS_1_4 (w4,in_1_4,clk,en,sod,w3);
	state_1_5 BS_1_5 (w5,in_1_11,clk,en,sod,w4);
	state_1_6 BS_1_6 (w6,in_1_16,clk,en,sod,w5);
	state_1_7 BS_1_7 (w7,in_1_5,clk,en,sod,w6);
	state_1_8 BS_1_8 (w8,in_1_6,clk,en,sod,w8,w7);
	state_1_9 BS_1_9 (w9,in_1_12,clk,en,sod,w8);
	state_1_10 BS_1_10 (w10,in_1_11,clk,en,sod,w9);
	state_1_11 BS_1_11 (w11,in_1_6,clk,en,sod,w11,w10);
	state_1_12 BS_1_12 (w12,in_1_12,clk,en,sod,w11);
	state_1_13 BS_1_13 (w13,in_1_17,clk,en,sod,w12);
	state_1_14 BS_1_14 (w14,in_1_5,clk,en,sod,w13);
	state_1_15 BS_1_15 (w15,in_1_6,clk,en,sod,w15,w14);
	state_1_16 BS_1_16 (w16,in_1_11,clk,en,sod,w15);
	state_1_17 BS_1_17 (w17,in_1_16,clk,en,sod,w16);
	state_1_18 BS_1_18 (w18,in_1_3,clk,en,sod,w17);
	state_1_19 BS_1_19 (w19,in_1_18,clk,en,sod,w18);
	state_1_20 BS_1_20 (w20,in_1_19,clk,en,sod,w19);
	state_1_21 BS_1_21 (w21,in_1_20,clk,en,sod,w20);
	state_1_22 BS_1_22 (w22,in_1_0,clk,en,sod,w21);
	state_1_23 BS_1_23 (w23,in_1_1,clk,en,sod,w22);
	state_1_24 BS_1_24 (w24,in_1_6,clk,en,sod,w24,w23);
	state_1_25 BS_1_25 (w25,in_1_13,clk,en,sod,w24);
	state_1_26 BS_1_26 (w26,in_1_18,clk,en,sod,w25);
	state_1_27 BS_1_27 (w27,in_1_15,clk,en,sod,w26);
	state_1_28 BS_1_28 (w28,in_1_5,clk,en,sod,w27);
	state_1_29 BS_1_29 (w29,in_1_6,clk,en,sod,w29,w28);
	state_1_30 BS_1_30 (w30,in_1_12,clk,en,sod,w29);
	state_1_31 BS_1_31 (w31,in_1_10,clk,en,sod,w30);
	state_1_32 BS_1_32 (w32,in_1_0,clk,en,sod,w31);
	state_1_33 BS_1_33 (w33,in_1_3,clk,en,sod,w32);
	state_1_34 BS_1_34 (w34,in_1_7,clk,en,sod,w33);
	state_1_35 BS_1_35 (w35,in_1_13,clk,en,sod,w34);
	state_1_36 BS_1_36 (w36,in_1_5,clk,en,sod,w35);
	state_1_37 BS_1_37 (w37,in_1_10,clk,en,sod,w36);
	state_1_38 BS_1_38 (w38,in_1_6,clk,en,sod,w38,w37);
	state_1_39 BS_1_39 (w39,in_1_7,clk,en,sod,w38);
	state_1_40 BS_1_40 (w40,in_1_5,clk,en,sod,w39);
	state_1_41 BS_1_41 (w41,in_1_10,clk,en,sod,w40);
	state_1_42 BS_1_42 (w42,in_1_2,clk,en,sod,w41);
	state_1_43 BS_1_43 (w43,in_1_5,clk,en,sod,w42);
	state_1_44 BS_1_44 (w44,in_1_10,clk,en,sod,w43);
	state_1_45 BS_1_45 (out,clk,en,sod,w44);
endmodule

module state_1_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_1_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_3(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_4(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_5(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_6(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_7(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_8(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_9(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_10(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_11(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_12(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_13(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_14(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_15(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_16(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_17(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_18(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_19(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_20(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_21(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_22(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_23(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_24(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_25(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_26(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_27(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_28(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_29(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_30(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_31(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_32(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_33(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_34(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_35(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_36(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_37(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_38(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_39(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_40(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_41(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_42(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_43(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_44(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	assign w1 = in0; 
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_1_45(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

