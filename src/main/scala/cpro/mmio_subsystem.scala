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

object Slot {
  def apply() = {
    new Slot()
  }
}

trait MMIO_core {
  val slot_io = IO(Slot())
}

class mmio_subsystem extends Module {
  val io = IO(new Bundle {
    // From fpro bridge
    val fp = FPRO()

    // LEDs and switches to connect to board
    val data_in = Input(UInt(16.W))
    val data_out = Output(UInt(16.W))
    val anode_assert = Output(UInt(8.W))
    val segs = Output(UInt(7.W))
  })

  // Instantiate the mmio_controller
  val mmioController = Module(new mmio_controller())
  mmioController.io.fp <> io.fp
  val slot = mmioController.io.slot

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
    core.slot_io.address <> slot.reg_addr(i)
    core.slot_io.rd_data <> slot.read_data(i)
    core.slot_io.wr_data <> slot.write_data(i)
    core.slot_io.read <> slot.read(i)
    core.slot_io.write <> slot.write(i)
    core.slot_io.cs <> slot.cs(i)
  }

  // Default read data for unused slots
  for (i <- slots.length until 64) {
    slot.read_data(i) := "h_ffff_ffff".U
  }
}
