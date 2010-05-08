module interfacer(stop,data,en,en_in,data_in,sod,eod,clk);
	input en_in,eod,clk,sod;
	input [7:0] data_in;
	output [7:0] data;
	output  stop,en;
	wire [8:0] buffer;
	wire [8:0] temp,temp1;
	wire sod_0,sod_1;

	assign data = buffer[7:0];
	or(en,sod_1,stop,buffer[8]);
	delay_1 de_1(sod_0,sod,clk);
	delay_1 de_2(sod_1,sod_0,clk);
	countforstop c1(stop,clk,sod,eod);
	delay de1(temp,{en_in,data_in},clk);
	delay de2(buffer,temp,clk);
	//delay de3(buffer,temp1,clk);
endmodule

module delay(out,in,clk);
	input [8:0] in;
	output [8:0] out;
	input clk;
	reg [8:0] out;

	always @(negedge clk)
		begin
			out <= in;
		end
endmodule

module delay_1(out,in,clk);
	input [0:0] in;
	output [0:0] out;
	input clk;
	reg [0:0] out;

	always @(negedge clk)
		begin			out <= in;
		end
endmodule

module countforstop(out,clk,rst_out,rst);
	input clk,rst,rst_out;
	output out;
	wire d_in;
	reg out;
	reg [2:0] count;
	or(d_in,rst,out);
	always @(posedge clk)
		begin
			if(rst)
				begin
					count <= 3'b001;
				end
			else if(out == 1'b1)
				begin
					count <= count +1;
				end
			else
				begin
					count <= count;
				end
		end
	always @(posedge clk)
		begin
			if(rst_out)
				out <= 1'b0;
			else if(rst)
				out <= 1'b1;
			else if(count >= 3'b100)
				out <= 1'b0;
			else
				out <= d_in;
		end
endmodule

