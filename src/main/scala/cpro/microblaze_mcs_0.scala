package cpro

import chisel3._

/* .Clk(clock),                          // input wire Clk
  .Reset(reset),                      // input wire Reset
  .IO_addr_strobe(IO_addr_strobe),    // output wire IO_addr_strobe
  .IO_address(IO_address),            // output wire [31 : 0] IO_address
  .IO_byte_enable(IO_byte_enable),    // output wire [3 : 0] IO_byte_enable
  .IO_read_data(IO_read_data),        // input wire [31 : 0] IO_read_data
  .IO_read_strobe(IO_read_strobe),    // output wire IO_read_strobe
  .IO_ready(IO_ready),                // input wire IO_ready
  .IO_write_data(IO_write_data),      // output wire [31 : 0] IO_write_data
  .IO_write_strobe(IO_write_strobe)  // output wire IO_write_strobe */

class microblaze_mcs_0 extends BlackBox {
  val io = IO(new Bundle {
    val Clk = Input(Clock())
    val Reset = Input(chisel3.Reset())
    val IO_addr_strobe = Output(Bool()) // output wire IO_addr_strobe
    val IO_address = Output(UInt(32.W)) // output wire [31 : 0] IO_address
    val IO_byte_enable =
      Output(UInt(4.W))
    // output wire [3 : 0] IO_byte_enable
    val IO_read_data = Input(UInt(32.W)) // input wire [31 : 0] IO_read_data
    val IO_read_strobe = Output(Bool()) // output wire IO_read_strobe
    val IO_ready = Input(Bool()) // input wire IO_ready
    val IO_write_data =
      Output(UInt(32.W))
    // output wire [31 : 0] IO_write_data
    val IO_write_strobe = Output(Bool()) // output wire IO_write_strobe
  })
}
