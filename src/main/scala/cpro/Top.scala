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

  val m = Module(new main);
  m.io.clock := clock;
  m.io.reset := reset;
  m.io.sw := sw;
  m.io.led <> led;
  m.io.anode_assert <> anode_assert;
  m.io.segs <> segs;
}

/** Generate Verilog sources */
object Top extends App {
  ChiselStage.emitSystemVerilogFile(
    new Top,
    Array("--split-verilog", "--target-dir", "target_sv/"),
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}
