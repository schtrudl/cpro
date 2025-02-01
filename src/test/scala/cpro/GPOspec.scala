package cpro

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import cpro.mmio_cores.GPO

class GPOSpec extends AnyFreeSpec with Matchers {

  "GPO should work" in {
    simulate(new GPO()) { dut =>
      // Initialize signals
      dut.reset.poke(true.B)
      dut.slot_io.address.poke(0.U)
      dut.slot_io.wr_data.poke(0.U)
      dut.slot_io.read.poke(false.B)
      dut.slot_io.write.poke(false.B)
      dut.slot_io.cs.poke(false.B)

      // Apply reset
      dut.clock.step(10)
      dut.reset.poke(false.B)

      dut.clock.step(20)
      dut.slot_io.address.poke(0.U)
      dut.slot_io.wr_data.poke("hA5A5".U)
      dut.slot_io.write.poke(true.B)
      dut.slot_io.cs.poke(true.B)
      // write ignored
      dut.slot_io.rd_data.expect(0.U)
      dut.io.data_out.expect(0.U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("hA5A5".U)
      dut.io.data_out.expect("hA5A5".U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("hA5A5".U)
      dut.io.data_out.expect("hA5A5".U)
      // new data
      dut.slot_io.wr_data.poke("h5A5A".U)
      dut.slot_io.write.poke(true.B)
      dut.slot_io.cs.poke(true.B)
      dut.slot_io.rd_data.expect("hA5A5".U)
      dut.io.data_out.expect("hA5A5".U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
      //
      dut.slot_io.wr_data.poke("hFAFA".U)
      dut.slot_io.write.poke(false.B)
      dut.slot_io.cs.poke(true.B)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
      //
      dut.slot_io.wr_data.poke("hFAFA".U)
      dut.slot_io.write.poke(true.B)
      dut.slot_io.cs.poke(false.B)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect("h5A5A".U)
      dut.io.data_out.expect("h5A5A".U)
    }
  }
}
