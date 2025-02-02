package cpro

import chisel3._
import cpro.mmio_cores._

// slot interface
class Slot extends Bundle {
  val address = Input(UInt(5.W))
  val rd_data = Output(UInt(32.W))
  val wr_data = Input(UInt(32.W))
  val read = Input(Bool())
  val write = Input(Bool())
  val cs = Input(Bool())
}

trait MMIO_core {
  val slot_io = IO(new Slot())
}

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

class mmio_subsystem extends Module {
  val io = IO(new Bundle {
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

  // Define signals for the modules
  val slotCs = Wire(UInt(64.W))
  val slotRegAddr = Wire(Vec(64, UInt(5.W)))
  val slotWriteData = Wire(Vec(64, UInt(32.W)))
  val slotWrite = Wire(UInt(64.W))
  val slotReadData = Wire(Vec(64, UInt(32.W)))
  val slotRead = Wire(UInt(64.W))

  // Instantiate the mmio_controller
  val mmioController = Module(new mmio_controller())
  mmioController.io.mmio_cs <> io.mmio_cs
  mmioController.io.mmio_address <> io.mmio_address
  mmioController.io.mmio_write_data <> io.mmio_write_data
  mmioController.io.mmio_write <> io.mmio_write
  mmioController.io.mmio_read_data <> io.mmio_read_data
  mmioController.io.mmio_read <> io.mmio_read
  mmioController.io.slot_cs <> slotCs
  mmioController.io.slot_reg_addr <> slotRegAddr
  mmioController.io.slot_write_data <> slotWriteData
  mmioController.io.slot_write <> slotWrite
  mmioController.io.slot_read_data <> slotReadData
  mmioController.io.slot_read <> slotRead

  var slots: Array[MMIO_core] = Array()

  // Instantiate the GPO (General Purpose Output)
  val gpo = Module(new GPO())
  gpo.io.data_out <> io.data_out
  slots :+= gpo

  // Instantiate the GPI (General Purpose Input)
  val gpi = Module(new GPI())
  gpi.io.data_in <> io.data_in
  slots :+= gpi

  // Instantiate the Timer
  val timer = Module(new timer())
  slots :+= timer

  // Instantiate the Seven Segment Display
  val sevenSegDisplay = Module(new SevSegDisplay_core())
  io.anode_assert <> sevenSegDisplay.io.anode_select
  io.segs <> sevenSegDisplay.io.segs
  slots :+= sevenSegDisplay

  require(slots.length < 64)

  for ((core, i) <- slots.zipWithIndex) {
    core.slot_io.address <> slotRegAddr(i)
    core.slot_io.rd_data <> slotReadData(i)
    core.slot_io.wr_data <> slotWriteData(i)
    core.slot_io.read <> slotRead(i)
    core.slot_io.write <> slotWrite(i)
    core.slot_io.cs <> slotCs(i)
  }

  // Default read data for unused slots
  for (i <- slots.length until 64) {
    slotReadData(i) := "h_ffff_ffff".U
  }
}
