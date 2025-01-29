package cpro.mmio_cores

import chisel3._
import chisel3.Reg
import chisel3.util.HasBlackBoxResource
import chisel3.util.Cat

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
    // external signal e.g. to switch
    input logic [15:0]  data_in
 */

class GPI extends Module {
  val io = IO(new Bundle {
    // slot interface
    val address = Input(UInt(5.W))
    val rd_data = Output(UInt(32.W))
    val wr_data = Input(UInt(32.W))
    val read = Input(Bool())
    val write = Input(Bool())
    val cs = Input(Bool())

    // external signal
    val data_in = Input(UInt(16.W))
  })
  val buf_in = RegInit(0.U(32.W))
  buf_in := Cat(0.U, io.data_in)

  io.rd_data := buf_in
}
