module BRAM_0(out,clk,sod,en,char);
	input clk, sod, en;
	input [7:0] char;
	output [7:0] out;
	wire [33:0] q_out;

//BRAM declare 
	bram_entity_0 ram (.addr(char),.clk(clk),.dout(q_out),.en(en));
	engine_0 en_0(out[0], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[4], q_out[5], q_out[6], q_out[7], q_out[8], q_out[9], q_out[10], q_out[11], q_out[12], q_out[13]);
	engine_1 en_1(out[1], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[4], q_out[5], q_out[6], q_out[7], q_out[10], q_out[11], q_out[12], q_out[13], q_out[14], q_out[15], q_out[16], q_out[17], q_out[18], q_out[19], q_out[20]);
	engine_2 en_2(out[2], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[5], q_out[7], q_out[10], q_out[12], q_out[13], q_out[15], q_out[16], q_out[18], q_out[21], q_out[22], q_out[23], q_out[24], q_out[25]);
	engine_3 en_3(out[3], clk, sod, en, q_out[0], q_out[2], q_out[3], q_out[5], q_out[6], q_out[7], q_out[10], q_out[11], q_out[12], q_out[13], q_out[15], q_out[18]);
	engine_4 en_4(out[4], clk, sod, en, q_out[3], q_out[4], q_out[7], q_out[16], q_out[20], q_out[21], q_out[23], q_out[26], q_out[27], q_out[28], q_out[29], q_out[30]);
	engine_5 en_5(out[5], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[4], q_out[10], q_out[12], q_out[16], q_out[18], q_out[20], q_out[23], q_out[26], q_out[28], q_out[31], q_out[32], q_out[33]);
	engine_6 en_6(out[6], clk, sod, en, q_out[0], q_out[1], q_out[3], q_out[4], q_out[7], q_out[10], q_out[15], q_out[16], q_out[18], q_out[20], q_out[21], q_out[22], q_out[23], q_out[26], q_out[28], q_out[32], q_out[33]);
	engine_7 en_7(out[7], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3], q_out[4], q_out[5], q_out[6], q_out[7], q_out[8], q_out[9], q_out[10], q_out[11], q_out[12], q_out[13]);

endmodule
