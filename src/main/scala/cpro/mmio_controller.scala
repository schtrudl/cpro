package cpro

import chisel3._
import chisel3.util.HasBlackBoxResource

/*
//
    input logic clock,
    input logic reset,
    // from fpro brigge
    input logic mmio_cs,
    input logic [20:0] mmio_address,
    input logic [31:0] mmio_write_data,
    input logic mmio_write,
    output logic [31:0] mmio_read_data,
    input logic mmio_read,
    // to the cores
    output logic [63:0] slot_cs,
    output logic [4:0] slot_reg_addr [63:0],
    output logic [31:0] slot_write_data [63:0], // array
    output logic [63:0] slot_write,
    input logic [31:0] slot_read_data [63:0],
    output logic [63:0] slot_read
 */

class mmio_controller extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    // Clock and reset
    val clock = Input(Clock())
    val reset = Input(Reset())

    // From fpro bridge
    val mmio_cs = Input(Bool())
    val mmio_address =
      Input(UInt(21.W)) // 20 bits address + 1 bit (if applicable)
    val mmio_write_data = Input(UInt(32.W))
    val mmio_write = Input(Bool())
    val mmio_read_data = Output(UInt(32.W))
    val mmio_read = Input(Bool())

    // To the cores
    val slot_cs = Output(UInt(64.W)) // 64-bit signal
    val slot_reg_addr = Output(Vec(64, UInt(5.W))) // 64 elements, 5-bit address
    val slot_write_data =
      Output(Vec(64, UInt(32.W))) // Array of 64, each 32-bit
    val slot_write = Output(UInt(64.W)) // 64-bit signal
    val slot_read_data = Input(Vec(64, UInt(32.W))) // Array of 64, each 32-bit
    val slot_read = Output(UInt(64.W)) // 64-bit signal
  })
  addResource("/mmio_controller.sv")
}
