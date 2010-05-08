`timescale 1ns/1ps
module bram_entity_0(
addr,
clk,
dout,
en);
input [7 : 0] addr;
input clk;
output [33 : 0] dout;
input en;
// synopsys translate_off
	BLKMEMSP_V6_2 #(
	8,	// c_addr_width
	"0",	// c_default_data
	256,	// c_depth
	0,	// c_enable_rlocs
	0,	// c_has_default_data
	0,	// c_has_din
	1,	// c_has_en
	0,	// c_has_limit_data_pitch
	0,	// c_has_nd
	0,	// c_has_rdy
	0,	// c_has_rfd
	0,	// c_has_sinit
	0,	// c_has_we
	18,	// c_limit_data_pitch
	"bram_entity_0.mif",	// c_mem_init_file
	0,	// c_pipe_stages
	0,	// c_reg_inputs
	"0",	// c_sinit_value
	34,	// c_width
	0,	// c_write_mode
	"0",	// c_ybottom_addr	1,	// c_yclk_is_rising
	1,	// c_yen_is_high
	"hierarchy1",	// c_yhierarchy
	0,	// c_ymake_bmm
	"512x36",	// c_yprimitive_type
	1,	// c_ysinit_is_high
	"1024",	// c_ytop_addr
	1,	// c_yuse_single_primitive
	1,	// c_ywe_is_high
	1)	// c_yydisable_warnings
		inst (
		.ADDR(addr),
		.CLK(clk),
		.DOUT(dout),
		.EN(en),
		.DIN(),
		.ND(),
		.RFD(),
		.RDY(),
		.SINIT(),
		.WE());
endmodule
