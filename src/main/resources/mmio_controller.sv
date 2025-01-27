module mmio_controller (
    //
    input logic clock,  
    input logic reset, 
    // from fpro brigge
    input logic mmio_cs,
    input logic [20:0] mmio_address,
    input logic [31:0] mmio_write_data,
    input logic mmio_write, 
    output logic [31:0] mmio_read_data,
    input logic mmio_read,
    // to the cores 
    output logic [63:0] slot_cs,
    output logic [63:0] [4:0] slot_reg_addr,
    output logic [63:0] [31:0] slot_write_data, // array
    output logic [63:0] slot_write,
    input logic [63:0] [31:0] slot_read_data,
    output logic [63:0] slot_read
);

    // module and register address
    logic [4:0] reg_addr;
    logic [5:0] slot_addr;

    assign reg_addr = mmio_address[4:0]; // last 5 bits 
    assign slot_addr = mmio_address[10:5];

    // decode interface

    // generate chip select signal 
    always_comb begin : decodeLogic
        slot_cs = 0;
        if (mmio_cs) begin
            slot_cs[slot_addr] = 1;
        end
    end

    // broadcast everything
    generate
    genvar i;
    for ( i=0 ; i<64 ; i++ ) begin
        assign slot_reg_addr[i] = reg_addr;
        assign slot_write_data[i] = mmio_write_data;
        assign slot_write[i] = mmio_write;
    end
    endgenerate


    // read data interface
    // mux for read data
    always_comb begin : readData
        mmio_read_data = 0;
        if (mmio_read) begin
            mmio_read_data = slot_read_data[slot_addr];
        end
    end
    
    // broadcast read request
    assign slot_read = {64{mmio_read}}; // repeat 64 times

endmodule