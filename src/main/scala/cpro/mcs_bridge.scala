package cpro

import chisel3._
import chisel3.util.HasBlackBoxResource

/* // uBLAZE MCS I/O bus
    input logic [31:0] io_address,
    input logic io_addr_strobe,
    input logic [31:0] io_write_data,
    input logic io_write_strobe,
    input logic [3:0] io_byte_enable,
    output logic [31:0] io_read_data,
    input logic io_read_strobe,
    output logic io_ready,
    // FPro bus
    output logic fp_video_cs, // Change 1: Added the video control signals
    output logic fp_mmio_cs,
    output logic fp_wr,
    output logic fp_rd,
    output logic [20:0] fp_addr,
    output logic [31:0] fp_wr_data,
    input logic [31:0] fp_rd_data */

class mcs_bridge extends BlackBox with HasBlackBoxResource {
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
    val fp_video_cs = Output(Bool())
    val fp_mmio_cs = Output(Bool())
    val fp_wr = Output(Bool())
    val fp_rd = Output(Bool())
    val fp_addr = Output(UInt(21.W))
    val fp_wr_data = Output(UInt(32.W))
    val fp_rd_data = Input(UInt(32.W))
  })
  addResource("/mcs_bridge.sv")
}
