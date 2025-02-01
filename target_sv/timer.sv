// Generated by CIRCT firtool-1.62.1
module timer(
  input         clock,
                reset,
  input  [4:0]  slot_io_address,
  output [31:0] slot_io_rd_data,
  input  [31:0] slot_io_wr_data,
  input         slot_io_read,
                slot_io_write,
                slot_io_cs
);

  reg [63:0] count;
  reg [31:0] config_reg;
  always @(posedge clock) begin
    if (reset) begin
      count <= 64'h0;
      config_reg <= 32'h0;
    end
    else begin
      if (config_reg[0])
        count <= 64'h0;
      else if (config_reg[1])
        count <= count + 64'h1;
      if (slot_io_write & slot_io_cs & ~(|slot_io_address))
        config_reg <= slot_io_wr_data;
    end
  end // always @(posedge)
  assign slot_io_rd_data =
    (|slot_io_address)
      ? (slot_io_address == 5'h1
           ? count[31:0]
           : slot_io_address == 5'h2 ? count[63:32] : 32'h0)
      : config_reg;
endmodule

