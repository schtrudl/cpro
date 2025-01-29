package cpro

import chisel3._
import cpro.mmio_cores._

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
  mmioController.io.clock <> clock
  mmioController.io.reset <> reset
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

  // Instantiate the GPO (General Purpose Output)
  val gpo = Module(new GPO())
  gpo.io.clock <> clock
  gpo.io.reset <> reset
  gpo.io.address <> slotRegAddr(0)
  gpo.io.rd_data <> slotReadData(0)
  gpo.io.wr_data <> slotWriteData(0)
  gpo.io.read <> slotRead(0)
  gpo.io.write <> slotWrite(0)
  gpo.io.cs <> slotCs(0)
  gpo.io.data_out <> io.data_out

  // Instantiate the GPI (General Purpose Input)
  val gpi = Module(new GPI())
  gpi.clock <> clock
  gpi.reset <> reset
  gpi.io.address <> slotRegAddr(1)
  gpi.io.rd_data <> slotReadData(1)
  gpi.io.wr_data <> slotWriteData(1)
  gpi.io.read <> slotRead(1)
  gpi.io.write <> slotWrite(1)
  gpi.io.cs <> slotCs(1)
  gpi.io.data_in <> io.data_in

  // Instantiate the Timer
  val timer = Module(new timer())
  timer.io.clock <> clock
  timer.io.reset <> reset
  timer.io.address <> slotRegAddr(2)
  timer.io.rd_data <> slotReadData(2)
  timer.io.wr_data <> slotWriteData(2)
  timer.io.read <> slotRead(2)
  timer.io.write <> slotWrite(2)
  timer.io.cs <> slotCs(2)

  // Instantiate the Seven Segment Display
  val sevenSegDisplay = Module(new SevSegDisplay_core())
  sevenSegDisplay.io.clock <> clock
  sevenSegDisplay.io.reset <> reset
  sevenSegDisplay.io.address <> slotRegAddr(3)
  sevenSegDisplay.io.rd_data <> slotReadData(3)
  sevenSegDisplay.io.wr_data <> slotWriteData(3)
  sevenSegDisplay.io.read <> slotRead(3)
  sevenSegDisplay.io.write <> slotWrite(3)
  sevenSegDisplay.io.cs <> slotCs(3)
  io.anode_assert <> sevenSegDisplay.io.anode_select
  io.segs <> sevenSegDisplay.io.segs

  // Default read data for unused slots
  for (i <- 4 until 64) {
    slotReadData(i) := "hffffffff".U
  }
}
