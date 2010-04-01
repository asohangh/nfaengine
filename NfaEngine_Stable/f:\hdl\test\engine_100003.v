module engine_100003(out,clk,sod,en,char);
//pcre: /m|n/smi
	input [7:0] char;
	input clk,sod,en;
	output out;

	charBlock_100003_0 BC_100003_0 (char_100003_0,char);
	charBlock_100003_1 BC_100003_1 (char_100003_1,char);

	state_100003_0 St_0 (y1,1'b0,clk,en,sod);
	assign w0 = ~y1;
	state_100003_1 BS_100003_1 (w1,char_100003_0,clk,en,sod,w0);
	state_100003_2 BS_100003_2 (w2,char_100003_1,clk,en,sod,w0);
	state_100003_3 BS_100003_3 (out,clk,en,sod,w1,w2);
endmodule

module charBlock_100003_0(out, char);
// Char: m
	input [7:0] char;
	output out;
	assign out = ((char == 8'b1101101) || (char == 8'b1001101)) ? 1'b1 : 1'b0;
endmodule

module charBlock_100003_1(out, char);
// Char: n
	input [7:0] char;
	output out;
	assign out = ((char == 8'b1101110) || (char == 8'b1001110)) ? 1'b1 : 1'b0;
endmodule


module state_100003_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_100003_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	or(w1,in0);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_100003_2(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	or(w1,in0);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_100003_3(out1,clk,en,rst,in0,in1);
	input clk,rst,en,in0,in1;
	output out1;
	wire w1;
	or(w1,out1,in0,in1);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

