// Generated by CIRCT firtool-1.62.1
module mmio_subsystem(
  input         clock,
                reset,
                io_mmio_cs,
  input  [20:0] io_mmio_address,
  input  [31:0] io_mmio_write_data,
  input         io_mmio_write,
  output [31:0] io_mmio_read_data,
  input         io_mmio_read,
  input  [15:0] io_data_in,
  output [15:0] io_data_out,
  output [7:0]  io_anode_assert,
  output [6:0]  io_segs
);

  wire [31:0]       _sevenSegDisplay_rd_data;
  wire [31:0]       _timer_io_rd_data;
  wire [31:0]       _gpi_io_rd_data;
  wire [63:0]       _mmioController_slot_cs;
  wire [63:0][4:0]  _mmioController_slot_reg_addr;
  wire [63:0][31:0] _mmioController_slot_write_data;
  wire [63:0]       _mmioController_slot_write;
  wire [63:0]       _mmioController_slot_read;
  mmio_controller mmioController (
    .clock           (clock),
    .reset           (reset),
    .mmio_cs         (io_mmio_cs),
    .mmio_address    (io_mmio_address),
    .mmio_write_data (io_mmio_write_data),
    .mmio_write      (io_mmio_write),
    .mmio_read_data  (io_mmio_read_data),
    .mmio_read       (io_mmio_read),
    .slot_cs         (_mmioController_slot_cs),
    .slot_reg_addr   (_mmioController_slot_reg_addr),
    .slot_write_data (_mmioController_slot_write_data),
    .slot_write      (_mmioController_slot_write),
    .slot_read_data
      ({{32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {32'hFFFFFFFF},
        {_sevenSegDisplay_rd_data},
        {_timer_io_rd_data},
        {_gpi_io_rd_data},
        {32'h0}}),
    .slot_read       (_mmioController_slot_read)
  );
  GPO gpo (
    .clock       (clock),
    .reset       (reset),
    .io_address  (_mmioController_slot_reg_addr[6'h0]),
    .io_wr_data  (_mmioController_slot_write_data[6'h0]),
    .io_read     (_mmioController_slot_read[0]),
    .io_write    (_mmioController_slot_write[0]),
    .io_cs       (_mmioController_slot_cs[0]),
    .io_data_out (io_data_out)
  );
  GPI gpi (
    .clock      (clock),
    .reset      (reset),
    .io_address (_mmioController_slot_reg_addr[6'h1]),
    .io_rd_data (_gpi_io_rd_data),
    .io_wr_data (_mmioController_slot_write_data[6'h1]),
    .io_read    (_mmioController_slot_read[1]),
    .io_write   (_mmioController_slot_write[1]),
    .io_cs      (_mmioController_slot_cs[1]),
    .io_data_in (io_data_in)
  );
  timer timer (
    .clock      (clock),
    .reset      (reset),
    .io_address (_mmioController_slot_reg_addr[6'h2]),
    .io_rd_data (_timer_io_rd_data),
    .io_wr_data (_mmioController_slot_write_data[6'h2]),
    .io_read    (_mmioController_slot_read[2]),
    .io_write   (_mmioController_slot_write[2]),
    .io_cs      (_mmioController_slot_cs[2])
  );
  SevSegDisplay_core sevenSegDisplay (
    .clock        (clock),
    .reset        (reset),
    .address      (_mmioController_slot_reg_addr[6'h3]),
    .rd_data      (_sevenSegDisplay_rd_data),
    .wr_data      (_mmioController_slot_write_data[6'h3]),
    .read         (_mmioController_slot_read[3]),
    .write        (_mmioController_slot_write[3]),
    .cs           (_mmioController_slot_cs[3]),
    .anode_select (io_anode_assert),
    .segs         (io_segs)
  );
endmodule

