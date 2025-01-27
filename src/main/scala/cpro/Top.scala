// See README.md for license details.

package cpro

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class Top extends Module {
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

  // Instantiate the MicroBlaze MCS Module (BlackBox)
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

  val mcsBridge = Module(new mcs_bridge)
  mcsBridge.io.io_address <> IO_address
  mcsBridge.io.io_addr_strobe <> IO_addr_strobe
  mcsBridge.io.io_write_data <> IO_write_data
  mcsBridge.io.io_write_strobe <> IO_write_strobe
  mcsBridge.io.io_byte_enable <> IO_byte_enable
  mcsBridge.io.io_read_data <> IO_read_data
  mcsBridge.io.io_read_strobe <> IO_read_strobe
  mcsBridge.io.io_ready <> IO_ready

  // FPro Bus Signals
  val fp_video_cs = Wire(Bool())
  val fp_mmio_cs = Wire(Bool())
  val fp_wr = Wire(Bool())
  val fp_rd = Wire(Bool())
  val fp_addr = Wire(UInt(21.W))
  val fp_wr_data = Wire(UInt(32.W))
  val fp_rd_data = Wire(UInt(32.W))

  mcsBridge.io.fp_video_cs <> fp_video_cs
  mcsBridge.io.fp_mmio_cs <> fp_mmio_cs
  mcsBridge.io.fp_wr <> fp_wr
  mcsBridge.io.fp_rd <> fp_rd
  mcsBridge.io.fp_addr <> fp_addr
  mcsBridge.io.fp_wr_data <> fp_wr_data
  mcsBridge.io.fp_rd_data <> fp_rd_data

  // Instantiate the MMIO Subsystem (BlackBox)
  val mmioSubsystem = Module(new mmio_subsystem)
  mmioSubsystem.io.clock <> clock
  mmioSubsystem.io.reset <> reset
  mmioSubsystem.io.mmio_cs <> fp_mmio_cs
  mmioSubsystem.io.mmio_address <> fp_addr
  mmioSubsystem.io.mmio_write_data <> fp_wr_data
  mmioSubsystem.io.mmio_write <> fp_wr
  mmioSubsystem.io.mmio_read_data <> fp_rd_data
  mmioSubsystem.io.mmio_read <> fp_rd
  mmioSubsystem.io.data_in <> sw
  mmioSubsystem.io.data_out <> led
  mmioSubsystem.io.anode_assert <> anode_assert
  mmioSubsystem.io.segs <> segs
}

/** Generate Verilog sources */
object Top extends App {
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
