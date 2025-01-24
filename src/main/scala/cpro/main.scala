package cpro

import chisel3._
import chisel3.util.HasBlackBoxResource

class main extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Reset())
    val sw = Input(UInt(16.W))
    val led = Output(UInt(16.W))
    val anode_assert = Output(UInt(8.W))
    val segs = Output(UInt(7.W))
  })
  addResource("/main.sv")
  addResource("/mcs_bridge.sv")
  addResource("/mmio_controller.sv")
  addResource("/mmio_cores.sv")
  addResource("/mmio_subsystem.sv")
}
