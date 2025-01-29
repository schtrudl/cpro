package cpro.mmio_cores

import chisel3._
import chisel3.util.switch
import chisel3.util.is

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
 */

class timer extends Module {
  val io = IO(new Bundle {
    // slot interface
    val address = Input(UInt(5.W))
    val rd_data = Output(UInt(32.W))
    val wr_data = Input(UInt(32.W))
    val read = Input(Bool())
    val write = Input(Bool())
    val cs = Input(Bool())
  })

  val clear = Wire(Bool())
  val go = Wire(Bool())

  val count = RegInit(0.U(64.W))
  when(clear) {
    count := 0.U
  }.elsewhen(go) {
    count := count + 1.U
  }

  // regs
  val config_reg = RegInit(0.U(32.W)) // 0x00
  val count_low = count(31, 0) // 0x01
  val count_high = count(63, 32) // 0x02

  // read interface

  io.rd_data := 0.U // default
  switch(io.address) {
    is("h00".U) {
      io.rd_data := config_reg
    }
    is("h01".U) {
      io.rd_data := count_low
    }
    is("h02".U) {
      io.rd_data := count_high
    }
  }

  // write interface
  val wr_en: Bool = (io.write & io.cs & (io.address === "h00".U))
  when(wr_en) {
    config_reg := io.wr_data
  }

  go := config_reg(1)
  clear := config_reg(0)
}
