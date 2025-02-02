package cpro.mmio_cores

import chisel3._
import cpro.{Slot, MMIO_core}

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

class GPO extends Module with MMIO_core {
  val io = IO(new Bundle {
    // external signal
    val data_out = Output(UInt(16.W))
  })

  // define register for GPO device
  val buf_gpo = RegInit(0.U(16.W))

  // decoding logic
  // there is only one register, so we do not need address signal
  val wr_en: Bool = (slot_io.write & slot_io.cs)

  when(wr_en) {
    // implicit subslice
    buf_gpo := slot_io.wr_data // (15,0)
  }

  // copy the buf_gpo to out
  io.data_out := buf_gpo

  slot_io.rd_data := buf_gpo
}
