package cpro.mmio_cores

import chisel3._
import chisel3.util.Counter
import chisel3.util.switch
import chisel3.util.is
import cpro.{Slot, MMIO_core}

/*
  // clock and reset
    input logic clock,
    input logic reset,
    // slot interface
    input logic [4:0] address,
    output logic [31:0] rd_data ,
    input logic [31:0] wr_data ,
    input logic read,
    input logic write,
    input logic cs,
    // external signals -> Catode and Anode
    output logic [7:0] anode_select,
    output logic [6:0] segs
 */

class SevSegDisplay_core extends Module with MMIO_core {
  val io = IO(new Bundle {
    // external signal
    val anode_select = Output(UInt(8.W))
    val segs = Output(UInt(7.W))
  })

  slot_io.rd_data := 0.U

  val wr_en: Bool = (slot_io.write & slot_io.cs)

  val config_reg = RegInit(0.U(32.W))

  when(slot_io.address === "h00".U) {
    slot_io.rd_data := config_reg
    when(wr_en) {
      config_reg := slot_io.wr_data
    }
  }

  val enable_7seg = config_reg(0)

  val display_data = RegInit(0.U(32.W))

  when(slot_io.address === "h01".U) {
    slot_io.rd_data := display_data
    when(wr_en) {
      display_data := slot_io.wr_data
    }
  }

  val seg7 = Module(new SevSegDisplay())
  seg7.io.enable_7seg <> enable_7seg
  seg7.io.display_data <> display_data
  seg7.io.anode_select <> io.anode_select
  seg7.io.segs <> io.segs
}

class SevSegDisplay extends Module {
  val io = IO(new Bundle {
    val enable_7seg = Input(Bool())
    val display_data = Input(UInt(32.W))
    // external signal
    val anode_select = Output(UInt(8.W))
    val segs = Output(UInt(7.W))
  })

  val (_, anode_prescaler) =
    Counter(0 until 40000, io.enable_7seg, reset.asBool)

  // anode assert/select
  val (count, _) = Counter(0 until 8, anode_prescaler, reset.asBool)
  when(io.enable_7seg) {
    io.anode_select := ~(1.U << count)
  }.otherwise {
    io.anode_select := ~0.U
  }

  val digit = Wire(UInt(4.W))
  def extract_digit(select: UInt) = {
    digit := "b0000".U
    switch(select) {
      is("h01".U) {
        digit := io.display_data(3, 0)
      }
      is("h02".U) {
        digit := io.display_data(7, 4)
      }
      is("h04".U) {
        digit := io.display_data(11, 8)
      }
      is("h08".U) {
        digit := io.display_data(15, 12)
      }
      is("h10".U) {
        digit := io.display_data(19, 16)
      }
      is("h20".U) {
        digit := io.display_data(23, 20)
      }
      is("h40".U) {
        digit := io.display_data(27, 24)
      }
      is("h80".U) {
        digit := io.display_data(31, 28)
      }
    }
  }
  extract_digit(~io.anode_select)

  def digit_to_segments(digit: UInt) = {
    io.segs := DontCare
    switch(digit) {
      is("b0000".U) { io.segs := "b1000000".U } // 0
      is("b0001".U) { io.segs := "b1111001".U } // 1
      is("b0010".U) { io.segs := "b0100100".U } // 2
      is("b0011".U) { io.segs := "b0110000".U } // 3
      is("b0100".U) { io.segs := "b0011001".U } // 4
      is("b0101".U) { io.segs := "b0010010".U } // 5
      is("b0110".U) { io.segs := "b0000010".U } // 6
      is("b0111".U) { io.segs := "b1111000".U } // 7
      is("b1000".U) { io.segs := "b0000000".U } // 8
      is("b1001".U) { io.segs := "b0010000".U } // 9
      is("b1010".U) { io.segs := "b0001000".U } // A
      is("b1011".U) { io.segs := "b0000011".U } // b
      is("b1100".U) { io.segs := "b1000110".U } // C
      is("b1101".U) { io.segs := "b0100001".U } // d
      is("b1110".U) { io.segs := "b0000110".U } // E
      is("b1111".U) { io.segs := "b0001110".U } // F
    }
  }
  digit_to_segments(digit)
}
