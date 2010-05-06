module top_engine(out,stop,clk_in,sod,en,char,eod);
	input [7:0] char;
	input clk_in,sod,en,eod;
	output stop;
 	wire [7:0] char_int;
	wire en_int;
	output [2:0] out;

	assign clk = ~clk_in;
	interfacer I1(stop,char_int,en_int,en,char,sod,eod,clk);
	engine_0 E_0 (out[0],clk,sod,en_int,char_int);
	engine_1 E_1 (out[1],clk,sod,en_int,char_int);
	engine_2 E_2 (out[2],clk,sod,en_int,char_int);

endmodule
