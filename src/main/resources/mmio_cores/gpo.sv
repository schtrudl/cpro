module GPO (
    // clock and reset
    input logic clock,
    input logic reset,
    // slot interface
    input logic [4:0] address,
    output logic [31:0] rd_data ,
    input logic [31:0] wr_data ,
    input logic read, 
    input logic write,
    input logic cs,
    // external signal e.g. to LEDs 
    output logic [15:0]  data_out 
);
    
    // define register for GPO device 
    logic [15:0] buf_gpo;
    logic wr_en;

    // decoding logic 
    assign wr_en = write & cs; // there is only one register, so we do not need address signal 
                               // otherwise ß

    // write data into register when selected ß
    always_ff @( posedge clock ) begin : write_logic
        if (reset) begin
            buf_gpo <= 0;
        end else begin
            if (wr_en) begin
                buf_gpo <= wr_data[15:0];
            end
        end
    end

    // copy the buf_gpo to out
    assign data_out = buf_gpo;


endmodule