package cpro

import chisel3._
import chisel3.util.Fill

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

class mmio_controller extends Module {
  val io = IO(new Bundle {
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

  // module and register address
  val reg_addr = io.mmio_address(4, 0) // last 5 bits
  val slot_addr = io.mmio_address(10, 5)

  // decode interface

  // generate chip select signal
  io.slot_cs := 0.U
  when(io.mmio_cs) {
    io.slot_cs := (1.U << slot_addr)
  }

  // broadcast everything
  for (i <- 0 until 64) {
    io.slot_reg_addr(i) := reg_addr
    io.slot_write_data(i) := io.mmio_write_data
  }
  io.slot_write := Fill(64, io.mmio_write)

  // read data interface
  io.mmio_read_data := 0.U
  when(io.mmio_read) {
    io.mmio_read_data := io.slot_read_data(slot_addr)
  }

  io.slot_read := Fill(64, io.mmio_read)
}
