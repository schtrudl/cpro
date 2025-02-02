package cpro

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import cpro.mmio_controller

class mmio_controllerSpec extends AnyFreeSpec with Matchers {

  "mmio_controller should work" in {
    simulate(new mmio_controller()) { dut =>
      // Initialize signals
      dut.reset.poke(true)
      dut.io.fp.mmio_cs.poke(false)
      dut.io.fp.addr.poke(0)
      dut.io.fp.wr_data.poke(0)
      dut.io.fp.write.poke(0)
      dut.io.fp.read.poke(0)

      // Apply reset
      dut.clock.step(10)
      dut.reset.poke(false)
      dut.clock.step(20)

      val data = 0x7a00_0000L
      val one = BigInt(1)
      val one64 = BigInt("ffffffffffffffff", 16)

      for (slot <- 0 until 64) {
        for (reg <- 0 until 32) {
          val w = data + slot * 3
          // Test case 1: Write to MMIO
          dut.io.fp.mmio_cs.poke(true)
          dut.io.fp.write.poke(true)
          dut.io.fp.read.poke(false)
          dut.io.fp.addr.poke((slot << 5) + reg)
          dut.io.fp.wr_data.poke(w)
          dut.clock.step(10)
          dut.io.slot.cs.expect(one << slot)
          dut.io.slot.reg_addr(slot).expect(reg)
          dut.io.slot.write_data(slot).expect(w)
          dut.io.slot.write.expect(one64)
          dut.io.slot.read.expect(0)

          dut.io.fp.mmio_cs.poke(false)
          dut.io.fp.write.poke(false)
          dut.clock.step(10)

          // Test case 2: Read from MMIO
          dut.io.fp.mmio_cs.poke(true)
          dut.io.fp.read.poke(true)
          dut.io.fp.addr.poke((slot << 5) + reg)
          dut.clock.step(10)
          // dut.io.slot.cs.expect(one << slot)
          dut.io.slot.reg_addr(slot).expect(reg)
          dut.io.slot.write.expect(0)
          dut.io.slot.read.expect(one64)
        }
      }
    }
  }
}
