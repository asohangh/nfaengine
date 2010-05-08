module myDff (q_o,d_i, clk,en,rst);
 	 input d_i,clk,en,rst;
	 output reg q_o;
	 always @ (posedge clk)
		 begin
		if (rst == 1'b1)
			q_o <= 1'b0;
		else if (en)
			q_o <= d_i;
		else
			q_o <= q_o;
		 end
endmodule
