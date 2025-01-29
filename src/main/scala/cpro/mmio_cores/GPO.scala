package cpro.mmio_cores

import chisel3._

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

class GPO extends Module {
  val io = IO(new Bundle {
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

  // define register for GPO device
  val buf_gpo = RegInit(0.U(16.W))

  // decoding logic
  // there is only one register, so we do not need address signal
  val wr_en: Bool = (io.write & io.cs)

  when(wr_en) {
    // implicit subslice
    buf_gpo := io.wr_data // (15,0)
  }

  // copy the buf_gpo to out
  io.data_out := buf_gpo

  io.rd_data := DontCare
}
