

module mmio_subsystem (
    // clock and reset
    input logic clock,
    input logic reset,
    // from fpro brigge
    input logic mmio_cs,
    input logic [20:0] mmio_address,
    input logic [31:0] mmio_write_data,
    input logic mmio_write, 
    output logic [31:0] mmio_read_data,
    input logic mmio_read,
    // leds and switches to connect to board 
    input logic [15:0] data_in,
    output logic [15:0] data_out,
    output logic [7:0] anode_assert,
    output logic [6:0] segs
);

    // define the signals for the modules
    logic [63:0] slot_cs;
    logic [4:0]  slot_reg_addr [63:0];
    logic [31:0] slot_write_data [63:0]; // array 
    logic [63:0] slot_write;
    logic [31:0] slot_read_data [63:0];
    logic [63:0] slot_read;

    // instantiate the mmio_controller
    mmio_controller mmio_controller_inst (
        .clock(clock),
        .reset(reset),
        .mmio_cs(mmio_cs),
        .mmio_address(mmio_address),
        .mmio_write_data(mmio_write_data),
        .mmio_write(mmio_write),
        .mmio_read_data(mmio_read_data),
        .mmio_read(mmio_read),
        .slot_cs(slot_cs),
        .slot_reg_addr(slot_reg_addr),
        .slot_write_data(slot_write_data),
        .slot_write(slot_write),
        .slot_read_data(slot_read_data),
        .slot_read(slot_read)
    );

    // instantiate the GPO
    GPO gpo_inst (
        .clock(clock),
        .reset(reset),
        // Because the GPO is the first slot, the base address is 0x00
        // on hw level we implement this by routing the signals with index 0
        // the mmio_controller will generate appropriate signals
        // based od address[10:5] == 6'h02
        .address(slot_reg_addr[0]),
        .rd_data(slot_read_data[0]),
        .wr_data(slot_write_data[0]),
        .read(slot_read[0]),
        .write(slot_write[0]),
        .cs(slot_cs[0]), 
        .data_out(data_out)
    );

    // instantiate the GPI
    GPI gpi_inst (
        .clock(clock),
        .reset(reset),
        // Because the GPI is the second slot, the base address is 0x01
        // on hw level we implement this by routing the signals with index 1
        // the mmio_controller will generate appropriate signals
        // based od address[10:5] == 6'h01
        .address(slot_reg_addr[1]),
        .rd_data(slot_read_data[1]),
        .wr_data(slot_write_data[1]),
        .read(slot_read[1]),
        .write(slot_write[1]),
        .cs(slot_cs[1]), 
        .data_in(data_in)
    );

    // instantiate the timer
    timer timer_inst (
        // Because the timer is the third slot, the base address is 0x02
        // on hw level we implement this by routing the signals with index 2
        // the mmio_controller will generate appropriate signals
        // based od address[10:5] == 6'h02
        .clock(clock),
        .reset(reset),
        .address(slot_reg_addr[2]),
        .rd_data(slot_read_data[2]),
        .wr_data(slot_write_data[2]),
        .read(slot_read[2]),
        .write(slot_write[2]),
        .cs(slot_cs[2]) 
    );


        // instantiate the timer
    SevSegDisplay_core seven_seg_inst (
        // Because the SevSegDisplay_core is the third slot, the base address is 0x03
        // on hw level we implement this by routing the signals with index 3
        // the mmio_controller will generate appropriate signals
        // based od address[10:5] == 6'h03
        .clock(clock),
        .reset(reset),
        .address(slot_reg_addr[3]),
        .rd_data(slot_read_data[3]),
        .wr_data(slot_write_data[3]),
        .read(slot_read[3]),
        .write(slot_write[3]),
        .cs(slot_cs[3]),
        // connect the anode_assert and segs to the module
        .anode_select(anode_assert),
        .segs(segs)
    );

    //regarding the rest read_data we will connect to FFFFFF
    generate
        genvar i;
        for ( i=4 ; i<64 ; i++ ) begin
            assign slot_read_data[i] = 32'hFFFFFFFF;
        end
    endgenerate

endmodule
