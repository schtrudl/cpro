module GPI (
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
    // external signal e.g. to switch  
    input logic [15:0]  data_in 
);

    // define register
    logic [31:0] buf_in;

    // connect to outside
    always_ff @(posedge clock) begin
        if(reset) begin
            buf_in <= 0;
        end else begin
            buf_in[15:0] <= data_in;
            buf_in[31:16] <= 0;
        end
    end

    // send to the bus through rd_data
    assign rd_data = buf_in;


endmodule