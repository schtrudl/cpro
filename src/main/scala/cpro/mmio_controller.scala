package cpro

import chisel3._
import chisel3.util.Fill
import chisel3.util.UIntToOH

class Slots extends Bundle {
  val cs = Output(UInt(64.W)) // 64-bit signal
  val reg_addr = Output(Vec(64, UInt(5.W))) // 64 elements, 5-bit address
  val write_data =
    Output(Vec(64, UInt(32.W))) // Array of 64, each 32-bit
  val write = Output(UInt(64.W)) // 64-bit signal
  val read_data = Input(Vec(64, UInt(32.W))) // Array of 64, each 32-bit
  val read = Output(UInt(64.W)) // 64-bit signal
}

object Slots {
  def apply() = {
    new Slots()
  }
}

class mmio_controller extends Module {
  val io = IO(new Bundle {
    // From FPro bridge
    val fp = FPRO()

    // To the cores
    val slot = Slots()
  })

  // module and register address
  val reg_addr = io.fp.addr(4, 0) // last 5 bits
  val slot_addr = io.fp.addr(10, 5)

  // decode interface

  // generate chip select signal
  io.slot.cs := 0.U
  when(io.fp.mmio_cs) {
    io.slot.cs := UIntToOH(slot_addr)
  }

  // broadcast everything
  for (i <- 0 until 64) {
    io.slot.reg_addr(i) := reg_addr
    io.slot.write_data(i) := io.fp.wr_data
  }
  io.slot.write := Fill(64, io.fp.write)

  // read data interface
  io.fp.rd_data := 0.U
  when(io.fp.read) {
    io.fp.rd_data := io.slot.read_data(slot_addr)
  }

  io.slot.read := Fill(64, io.fp.read)
}
