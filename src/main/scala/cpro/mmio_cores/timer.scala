package cpro.mmio_cores

import chisel3._
import chisel3.util.switch
import chisel3.util.is
import cpro.Slot

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
  val slot_io = IO(new Slot())

  val io = IO(new Bundle {})

  val clear = Wire(Bool())
  val go = Wire(Bool())

  // this could be done with chisel3.util.Counter, but then we would need to set limit manually ...
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

  slot_io.rd_data := 0.U // default
  switch(slot_io.address) {
    is("h00".U) {
      slot_io.rd_data := config_reg
    }
    is("h01".U) {
      slot_io.rd_data := count_low
    }
    is("h02".U) {
      slot_io.rd_data := count_high
    }
  }

  // write interface
  val wr_en: Bool = (slot_io.write & slot_io.cs & (slot_io.address === "h00".U))
  when(wr_en) {
    config_reg := slot_io.wr_data
  }

  go := config_reg(1)
  clear := config_reg(0)
}
