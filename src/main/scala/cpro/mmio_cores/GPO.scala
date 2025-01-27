package cpro.mmio_cores

import chisel3._
import chisel3.util.HasBlackBoxResource

/*
// clock and reset
    input logic clock,
    input logic reset,
    // slot interface
    input logic [4:0] address,
    output logic [31:0] rd_data ,
    input logic [31:0] wr_data ,
    input logic read,
    input logic write,
    input logic cs,
    // external signal e.g. to LEDs
    output logic [15:0]  data_out
 */

class GPO extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    // Clock and reset
    val clock = Input(Clock())
    val reset = Input(Reset())

    // slot interface
    val address = Input(UInt(5.W))
    val rd_data = Output(UInt(32.W))
    val wr_data = Input(UInt(32.W))
    val read = Input(Bool())
    val write = Input(Bool())
    val cs = Input(Bool())

    // external signal
    val data_out = Output(UInt(16.W))
  })
  addResource("/mmio_cores/gpo.sv")
}
