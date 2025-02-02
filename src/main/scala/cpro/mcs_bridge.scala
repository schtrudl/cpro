package cpro

import chisel3._

// FPro bus (consumer viewpoint)
class FPRO extends Bundle {
  val video_cs = Input(Bool())
  val mmio_cs = Input(Bool())
  val write = Input(Bool())
  val read = Input(Bool())
  val addr = Input(UInt(21.W))
  val wr_data = Input(UInt(32.W))
  val rd_data = Output(UInt(32.W))
}

object FPRO {
  def apply() = {
    new FPRO()
  }
}

class mcs_bridge(BRG_BASE: UInt = "h_c000_0000".U) extends Module {
  val io = IO(new Bundle {
    // uBLAZE MCS I/O bus
    val io_address = Input(UInt(32.W))
    val io_addr_strobe = Input(Bool())
    val io_write_data = Input(UInt(32.W))
    val io_write_strobe = Input(Bool())
    val io_byte_enable = Input(UInt(4.W))
    val io_read_data = Output(UInt(32.W))
    val io_read_strobe = Input(Bool())
    val io_ready = Output(Bool())
    // FPro bus
    val fp = Flipped(FPRO())
  })

  // address decoding
  val mcs_bridge_enable = (io.io_address(31, 24) === BRG_BASE(31, 24))
  io.fp.mmio_cs := mcs_bridge_enable & (io.io_address(23) === 0.U)
  io.fp.video_cs := mcs_bridge_enable & (io.io_address(23) === 1.U)
  io.fp.addr := io.io_address(22, 2) // the mmio system is word addressed
  // bit 1 and 0 are always 0

  // control line conversion
  io.fp.write := io.io_write_strobe;
  io.fp.read := io.io_read_strobe;
  io.io_ready := true.B; // always ready

  // data line conversion
  io.fp.wr_data := io.io_write_data;
  io.io_read_data := io.fp.rd_data;
}
