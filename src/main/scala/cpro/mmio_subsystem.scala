package cpro

import chisel3._
import chisel3.util.HasBlackBoxResource

/*
// clock and reset
    input logic clock,
    input logic reset,
    // from fpro brigge
    input logic mmio_cs,
    input logic [20:0] mmio_address,
    input logic [31:0] mmio_write_data,
    input logic mmio_write,
    output logic [31:0] mmio_read_data,
    input logic mmio_read,
    // leds and switches to connect to board
    input logic [15:0] data_in,
    output logic [15:0] data_out,
    output logic [7:0] anode_assert,
    output logic [6:0] segs
 */

class mmio_subsystem extends BlackBox with HasBlackBoxResource {
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

    // LEDs and switches to connect to board
    val data_in = Input(UInt(16.W))
    val data_out = Output(UInt(16.W))
    val anode_assert = Output(UInt(8.W))
    val segs = Output(UInt(7.W))
  })
  addResource("/mmio_subsystem.sv")
  addResource("/mmio_controller.sv")
  addResource("/mmio_cores/gpi.sv")
  addResource("/mmio_cores/gpo.sv")
  addResource("/mmio_cores/timer.sv")
  addResource("/mmio_cores/sev_seg_display.sv")
}
