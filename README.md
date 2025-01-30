# FproSoC in Chisel

Requirements:

- `jdk`
- `scala`
- `sbt`
- (optional) `rust` & `just`
- `verilator` (only needed for tests)

`sbt run` will regenerate SystemVerilog files into target_sv folder

`sbt test` will run tests using `verilator`

## Vivaldo/Vitis project

Just follow typical steps for MCS project as presented in <https://ucilnica.fri.uni-lj.si/pluginfile.php/229327/mod_resource/content/1/08-MCS.pdf>. For constraint use [XDC file](./Nexys-A7-100T-Master.xdc) provided by the repo (same as it was in vaja 6) for sources use [target_sv](./target_sv) folder from repo (for best experience make sure `copy files to project` is **not** checked). For C code you can use [helloworld.c](./helloworld.c) from repo.
