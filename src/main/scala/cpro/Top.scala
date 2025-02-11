// See README.md for license details.

package cpro

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Top extends Module { // whole class body is actually constructor
  val sw = IO(Input(UInt(16.W)))
  val led = IO(Output(UInt(16.W)))
  val anode_assert = IO(Output(UInt(8.W)))
  val segs = IO(Output(UInt(7.W)))

  // Internal signals
  val IO_addr_strobe = Wire(Bool())
  val IO_address = Wire(UInt(32.W))
  val IO_byte_enable = Wire(UInt(4.W))
  val IO_read_data = Wire(UInt(32.W))
  val IO_read_strobe = Wire(Bool())
  val IO_ready = Wire(Bool())
  val IO_write_data = Wire(UInt(32.W))
  val IO_write_strobe = Wire(Bool())

  // Instantiate the MicroBlaze MCS Module
  val microblazeMCS = Module(new microblaze_mcs_0)
  microblazeMCS.io.Clk <> clock
  microblazeMCS.io.Reset <> reset
  microblazeMCS.io.IO_addr_strobe <> IO_addr_strobe
  microblazeMCS.io.IO_address <> IO_address
  microblazeMCS.io.IO_byte_enable <> IO_byte_enable
  microblazeMCS.io.IO_read_data <> IO_read_data
  microblazeMCS.io.IO_read_strobe <> IO_read_strobe
  microblazeMCS.io.IO_ready <> IO_ready
  microblazeMCS.io.IO_write_data <> IO_write_data
  microblazeMCS.io.IO_write_strobe <> IO_write_strobe

  val mcsBridge = Module(new mcs_bridge("h_c000_0000".U))
  mcsBridge.io.io_address <> IO_address
  mcsBridge.io.io_addr_strobe <> IO_addr_strobe
  mcsBridge.io.io_write_data <> IO_write_data
  mcsBridge.io.io_write_strobe <> IO_write_strobe
  mcsBridge.io.io_byte_enable <> IO_byte_enable
  mcsBridge.io.io_read_data <> IO_read_data
  mcsBridge.io.io_read_strobe <> IO_read_strobe
  mcsBridge.io.io_ready <> IO_ready

  // Instantiate the MMIO Subsystem
  val mmioSubsystem = Module(new mmio_subsystem)
  mmioSubsystem.io.fp <> mcsBridge.io.fp
  mmioSubsystem.io.data_in <> sw
  mmioSubsystem.io.data_out <> led
  mmioSubsystem.io.anode_assert <> anode_assert
  mmioSubsystem.io.segs <> segs
}

/** Generate Verilog sources */
object Top extends App { // object is used for static members
  ChiselStage.emitSystemVerilogFile(
    new Top,
    Array("--split-verilog", "--target-dir", "target_sv/"),
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info",
      // These options are needed to properly handle packed arrays in verilog
      "-preserve-aggregate=all",
      "-scalarize-public-modules=false",
      "-scalarize-ext-modules=false"
    )
  )
}
