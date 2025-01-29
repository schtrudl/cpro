# Rewrite FPROSoC in chisel

## Setting up environment/workflow

Chisel is board independent, it generates SystemVerilog from chisel (scala) that is latter to be consumed by other FPGA tools (in our case Vivaldo) to generate bitstream for board.

We want to keep all sources in single tree (so we will use chisel tree). Chisel will generates all final System verilog files that are put in target_sv directory (even if blackboxed they are just copied from resources), files are then loaded in Vivaldo as folder (make sure that "copy to project" is not checked, to prevent Vivaldo from vendoring sources)

two step build process in chisel: compile scala then compile to verilog: <https://chipyard.readthedocs.io/en/stable/Customization/Incorporating-Verilog-Blocks.html#differences-between-hasblackboxpath-and-hasblackboxresource>, meaning that it's not that it's not that strongly typed (I am Rust programer myself). Its fitted on scala lang.

Firstly I just imported all files from vaja6, create blackbox for main file and instantiate/connect blackbox in Top.scala. Then it was start to test whole workflow (creating vivaldo project run and test on board) and after all that we can really start, using top-down approach by incrementally rewriting modules into chisel and/or blackboxing them.

Blackbox -> Module and blackbox it's children

## Blackboxes

are like header files in C, they only define interface and are not type checked (mismatch between impl of modul and chisel blackbox will cause error in simulation/synthesis)

## Verilog -> chisel

no support for subword assignments, always need to use cat

no concept of logic (typified) you need reg or wire

### init

| verilog | chisel |
|---------|--------|
| `logic a;` | / |
|`wire a;`| `val a = Wire(/*type*/)`|
|`reg a;`| `val a = Reg(/*type*/)`|
|| `val a = RegInit(r.U(w.W))`|
|| `val a = RegNext(/*TODO*/)`|

### slices

| verilog | chisel |
|---------|--------|
| `a[15]` | `a(15)`|
|`a[32:16]`| `a(32,16)`|

## Known chisel problems

- in verilog: `input  [15:0] sw` in chisel: `val led = Output(UInt(16.W))` not `val led = Output(UInt(15.W))`
- bad docs
- unpacked array not supported, but we can use [packed arrays](https://verificationguide.com/systemverilog/systemverilog-packed-and-unpacked-array/) that only require magic options (again bad docs) `scalarize-ext-modules`
- sizes are not really typed (they are checked in lowering)

not bug but a feature: all wires need to be connected, one can use `DontCare`

## Know Vivaldo problems

- associate ELF does not work correctly if MCS is not in top module
- vivaldo does not check folder for new files so folder needs to be reimported7
- sometimes changes are not detected (generate bitstream uses old files)
