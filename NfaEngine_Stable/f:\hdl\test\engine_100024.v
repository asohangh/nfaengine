module engine_100024(out,clk,sod,en,char);
//pcre: /[\d\z6166\z4146]/smit
	input [7:0] char;
	input clk,sod,en;
	output out;

	charBlock_100024_0 BC_100024_0 (char_100024_0,char);

	state_100024_0 St_0 (y1,~y3,clk,en,sod);
	 charBlock_100024_100000 cB (y3,char);
	assign w0 = ~y1;
	state_100024_1 BS_100024_1 (w1,char_100024_0,clk,en,sod,w0);
	state_100024_2 BS_100024_2 (out,clk,en,sod,w1);
endmodule

module charBlock_100024_0(out, char);
// Char class: [\d\z6166\z4146]
	input [7:0] char;
	output out;
	assign out = (0 || (char >= 8'b00110000 && char <= 8'b00111001)|| (char >= 8'b1100001 && char <= 8'b1100110)|| (char >= 8'b1000001 && char <= 8'b1000110)|| (char == 8'b110110)|| (char == 8'b110110)|| (char >= 8'b1000001 && char <= 8'b1000110)|| (char >= 8'b1100001 && char <= 8'b1100110)|| (char == 8'b110100)|| (char == 8'b110110)) ? 1'b1 : 1'b0;
endmodule

module charBlock_100024_100000(out, char);
// Hex: a
	input [7:0] char;
	output out;
	assign out = (char == 8'b1010) ? 1 : 0;
endmodule

module state_100024_0(out1,in1,clk,en,rst);
	input in1,clk,rst,en;
	output out1;
	myDff Dff (out1,in1,clk,en,rst);
endmodule

module state_100024_1(out1,in_char,clk,en,rst,in0);
	input in_char,clk,en,rst,in0;
	output out1;
	wire w1,w2;
	or(w1,in0);
	and(w2,in_char,w1);
	myDff Dff (out1,w2,clk,en,rst);
endmodule

module state_100024_2(out1,clk,en,rst,in0);
	input clk,rst,en,in0;
	output out1;
	wire w1;
	or(w1,out1,in0);
	myDff Dff (out1,w1,clk,en,rst);
endmodule

