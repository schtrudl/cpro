package cpro

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import cpro.mmio_cores.GPI

/** This is a trivial example of how to run this Specification From within sbt
  * use:
  * {{{
  * testOnly cpro.GPISpec
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly cpro.GPISpec'
  * }}}
  * Testing from mill:
  * {{{
  * mill %NAME%.test.testOnly cpro.GPISpec
  * }}}
  */
class GPISpec extends AnyFreeSpec with Matchers {

  "GPI should work" in {
    simulate(new GPI()) { dut =>
      // Initialize signals
      dut.reset.poke(true.B)
      dut.io.address.poke(0.U)
      dut.io.wr_data.poke(0.U)
      dut.io.read.poke(false.B)
      dut.io.write.poke(false.B)
      dut.io.cs.poke(false.B)
      dut.io.data_in.poke(7.U)

      // Apply reset
      dut.clock.step(10)
      dut.reset.poke(false.B)

      dut.clock.step(20)
      dut.io.address.poke(1.U)
      dut.io.wr_data.poke("hA5A5A5A5".U)
      dut.io.write.poke(true.B)
      dut.io.cs.poke(true.B)
      // write ignored
      dut.io.rd_data.expect(7.U)
      dut.clock.step(1)
      dut.io.rd_data.expect(7.U)
      dut.clock.step(10)
      dut.io.rd_data.expect(7.U)
      // ordinary read
      dut.io.address.poke(1.U)
      dut.io.read.poke(true.B)
      dut.io.cs.poke(true.B)
      dut.io.rd_data.expect(7.U)
      dut.clock.step(1)
      dut.io.rd_data.expect(7.U)
      // ext
      dut.io.data_in.poke(14.U)
      dut.io.rd_data.expect(7.U)
      dut.clock.step(1)
      dut.io.rd_data.expect(14.U)
    }
  }
}
