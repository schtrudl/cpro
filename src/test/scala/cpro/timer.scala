package cpro

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import cpro.mmio_cores.timer

class timerSpec extends AnyFreeSpec with Matchers {

  "timer should work" in {
    simulate(new timer()) { dut =>
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

      // Testcase 0: write to config_reg and start counting
      dut.slot_io.address.poke(0.U)
      dut.slot_io.wr_data.poke("h00000002".U)
      dut.slot_io.write.poke(true.B)
      dut.slot_io.cs.poke(true.B)
      dut.clock.step(10)
      dut.slot_io.write.poke(false.B)
      dut.slot_io.cs.poke(false.B)
      // Test case 2: Read from timer
      dut.clock.step(200)
      // read low
      dut.slot_io.read.poke(true.B)
      dut.slot_io.address.poke(1.U)
      dut.slot_io.cs.poke(true.B)
      dut.slot_io.rd_data.expect(209)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect(210)
      // read high
      dut.slot_io.read.poke(true.B)
      dut.slot_io.address.poke(2.U)
      dut.slot_io.cs.poke(true.B)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect(0)

      dut.clock.step(50)

      // Test case 3: Reset timer
      dut.slot_io.address.poke(0.U)
      dut.slot_io.wr_data.poke("h00000001".U)
      dut.slot_io.write.poke(true.B)
      dut.slot_io.cs.poke(true.B)
      dut.clock.step(10)
      dut.slot_io.write.poke(false.B)
      dut.slot_io.cs.poke(false.B)
      dut.clock.step(10)
      dut.slot_io.address.poke(1.U)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect(0)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect(0)
      dut.clock.step(1)
      dut.slot_io.rd_data.expect(0)
    }
  }
}
