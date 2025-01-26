module timer (
    // clock and reset
    input logic clock,
    input logic reset,
    // slot interface
    input logic [4:0] address,
    output logic [31:0] rd_data ,
    input logic [31:0] wr_data ,
    input logic read,
    input logic write,
    input logic cs
    // external signals -> NONE
);


// counter circuit 
logic [63:0] count;
logic go; // go start counting
logic clear; // clear initialize count to 0
      
always_ff @(posedge clock) begin
    if (reset) begin
        count <= 0;
    end else begin
        if (clear) begin
            count <= 0;
        end
        else begin
            if (go) begin
                count <= count + 1;
            end
        end
    end
end


// registers
logic [31:0] config_reg; // 0x00
logic [31:0] count_low; // 0x01
logic [31:0] count_high; // 0x02    

// give values to regs 

assign count_low  = count[31:0];
assign count_high = count[63:32];

// read interface 


always_comb begin : MUX_for_count
    case (address)
        5'h01: begin
            rd_data = count_low;
        end
        5'h02: begin
            rd_data = count_high;
        end
        default: begin
            rd_data = 5'h00;
        end
    endcase
end

// write interface 


// wr_enable signal 
logic wr_en;

assign wr_en = cs & write & (address == 5'h00);


always_ff @(posedge clock) begin
    if (reset) begin
        config_reg = 0;
    end else begin
        if (wr_en) begin
            config_reg <= wr_data;
        end
    end
end

// connect config reg with go and clear
assign go = config_reg[1];
assign clear = config_reg[0];

endmodule