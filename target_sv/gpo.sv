// Generated by CIRCT firtool-1.62.1
module GPO(
  input                                          clock,
                                                 reset,
  input  [4:0]                                   slot_io_address,
  output [31:0]                                  slot_io_rd_data,
  input  [31:0]                                  slot_io_wr_data,
  input                                          slot_io_read,
                                                 slot_io_write,
                                                 slot_io_cs,
  output struct packed {logic [15:0] data_out; } io
);

  reg [15:0] buf_gpo;
  always @(posedge clock) begin
    if (reset)
      buf_gpo <= 16'h0;
    else if (slot_io_write & slot_io_cs)
      buf_gpo <= slot_io_wr_data[15:0];
  end // always @(posedge)
  assign slot_io_rd_data = {16'h0, buf_gpo};
  assign io = '{data_out: buf_gpo};
endmodule

