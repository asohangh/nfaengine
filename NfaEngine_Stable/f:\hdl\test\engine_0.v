module engine_0(out,clk,sod,en,char);
//pcre: /a°\010°[abc\x3a]*°b/smi
	input [7:0] char;
	input clk,sod,en;
	output out;

	charBlock_0_0 BC_0_0 (char_0_0,char);
	charBlock_0_1 BC_0_1 (char_0_1,char);
	charBlock_0_2 BC_0_2 (char_0_2,char);
	charBlock_0_3 BC_0_3 (char_0_3,char);

	state_0_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_0_1 BS_0_1 (w1,char_0_0,clk,en,sod,w0);
	state_0_2 BS_0_2 (w2,char_0_1,clk,en,sod,w1);
	state_0_3 BS_0_3 (w3,char_0_2,clk,en,sod,w3,w2);
	state_0_4 BS_0_4 (w4,char_0_3,clk,en,sod,w3,w2);
	state_0_5 BS_0_5 (out,clk,en,sod,w4);
endmodule

module charBlock_0_0(out, char);
// Char: a
	input [7:0] char;
	output out;
	assign out = ((char == 8'b1100001) || (char == 8'b1000001)) ? 1'b1 : 1'b0;
endmodule

module charBlock_0_1(out, char);
// Hex: a
	input [7:0] char;
	output out;
	assign out = (char == 8'b1010) ? 1 : 0;
endmodule

module charBlock_0_2(out, char);
// Char class: [abc\x3a]
	input [7:0] char;
	output out;
	assign out = (0 || (char == 8'b1100001)|| (char == 8'b1100010)|| (char == 8'b1100011)|| (char == 8'b111010)) ? 1'b1 : 1'b0;
endmodule

module charBlock_0_3(out, char);
// Char: b
	input [7:0] char;
	output out;
	assign out = ((char == 8'b1100010) || (char == 8'b1000010)) ? 1'b1 : 1'b0;
endmodule


module state_0_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_0_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	or(w1,in0);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_0_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	or(w1,in0);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_0_3(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_0_4(out1,in_char,clk,en,rst,in0,in1);
	input in_char,clk,en,rst,in0,in1;
	output out1;
	wire w1,w2;
	or(w1,in0,in1);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_0_5(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

