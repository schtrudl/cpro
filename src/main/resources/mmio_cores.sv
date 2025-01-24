

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

 


 module GP_counter // General Purpose counter        
    #(parameter PRESCALER_WIDTH = 14,
      parameter LIMIT = 10000)
    (
        input logic clock,
        input logic reset,
        input start,
        output logic sample_tick
    );

    logic [PRESCALER_WIDTH-1:0] count;

    always_ff @( posedge clock) begin 
        if (reset) begin
            count <= 0;
            sample_tick <= 0;
        end
        else begin
            if (start) begin
                count <= count + 1;
                if (count == LIMIT-1) begin
                    count <= 0;
                    sample_tick <= 1;
                end
                else begin
                    sample_tick <= 0;
                end
            end 
        end
    end

    
endmodule



// Components for 7-segment display

module anode_assert (
    input logic clock,
    input logic reset,
    input logic clock_enable,
    input logic enable_7seg,
    output logic [7:0] anode_select
);

    // counter that counts from 0 to 7
    logic [2:0] count;

    always_ff @(posedge clock) begin
        if (reset) begin
            count <= 0;
        end
        else begin
            if (clock_enable) begin
                count <= count + 1;
            end
        end
    end

    // assert anode_select
    assign anode_select = enable_7seg ? ~(1 << count) : ~0  ;
    
endmodule


module value_to_digit(
    input logic [31:0] value,
    input logic [7:0] anode_select,
    output logic [3:0] digit
);

    always_comb begin : value_to_digit
        case (~anode_select)
            8'H01: digit = value[3:0];
            8'H02: digit = value[7:4];
            8'H04: digit = value[11:8];
            8'H08: digit = value[15:12];
            8'H10: digit = value[19:16];
            8'H20: digit = value[23:20];
            8'H40: digit = value[27:24];
            8'H80: digit = value[31:28];
            default: digit = 4'b1111;
        endcase
    end

endmodule

// purely comb module 

module digit_to_segments (
    input logic [3:0] digit,
    output logic [6:0] segs
);
    
    always_comb begin : segDecoder
        case (digit)
            4'b0000: segs = 7'b1000000; // 0
            4'b0001: segs = 7'b1111001; // 1
            4'b0010: segs = 7'b0100100; // 2
            4'b0011: segs = 7'b0110000; // 3
            4'b0100: segs = 7'b0011001; // 4
            4'b0101: segs = 7'b0010010; // 5
            4'b0110: segs = 7'b0000010; // 6
            4'b0111: segs = 7'b1111000; // 7
            4'b1000: segs = 7'b0000000; // 8
            4'b1001: segs = 7'b0010000; // 9
            4'b1010: segs = 7'b0001000; // A
            4'b1011: segs = 7'b0000011; // b
            4'b1100: segs = 7'b1000110; // C
            4'b1101: segs = 7'b0100001; // d
            4'b1110: segs = 7'b0000110; // E
            4'b1111: segs = 7'b0001110; // F
            default: segs = 7'b1111111; // off
        endcase
    end

endmodule




module SevSegDisplay (
    input logic clock,
    input logic reset,
    input logic enable_7seg,
    input logic [3:0] digit1,
    input logic [3:0] digit2,
    input logic [3:0] digit3,
    input logic [3:0] digit4,
    input logic [3:0] digit5,
    input logic [3:0] digit6,
    input logic [3:0] digit7,
    input logic [3:0] digit8,
    output logic [7:0] anode_select,
    output logic [6:0] segs
);

    // prescaler for anode
    localparam PRESCALER_ANODE_WIDTH = 16;
    localparam PRESCALER_ANODE_LIMIT = 40000; // achieve   delay
    logic anode_clock_enable;


    // define the prescaler module for anode as GP_counter
    GP_counter #(
        .PRESCALER_WIDTH(PRESCALER_ANODE_WIDTH),
        .LIMIT(PRESCALER_ANODE_LIMIT)
    ) anode_prescaler (
        .clock(clock),
        .reset(reset),
        .start(enable_7seg),
        .sample_tick(anode_clock_enable)
    );

    // define the anode_assert module
    anode_assert anode_assert_inst (
        .clock(clock),
        .reset(reset),
        .clock_enable(anode_clock_enable),
        .enable_7seg(enable_7seg),
        .anode_select(anode_select)
    );

    // define the value_to_digit module
    logic [31:0] digit_all;
    assign digit_all = {digit8, digit7, digit6, digit5, digit4, digit3, digit2, digit1};
    logic [3:0] digit_select;

    value_to_digit value_to_digit_inst (
        .value(digit_all),
        .anode_select(anode_select),
        .digit(digit_select)
    );

    digit_to_segments digit_to_segments_inst (
        .digit(digit_select),
        .segs(segs)
    );
    
    
endmodule


module SevSegDisplay_core (
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
    // external signals -> Catode and Anode
    output logic [7:0] anode_select,
    output logic [6:0] segs
);

    // instantiate the SevSegDisplay
    logic [31:0] display_data;
    logic enable_7seg;

    SevSegDisplay SevSegDisplay_inst (
        .clock(clock),
        .reset(reset),
        .enable_7seg(enable_7seg),
        .digit1(display_data[3:0]),
        .digit2(display_data[7:4]),
        .digit3(display_data[11:8]),
        .digit4(display_data[15:12]),
        .digit5(display_data[19:16]),
        .digit6(display_data[23:20]),
        .digit7(display_data[27:24]),
        .digit8(display_data[31:28]),
        .anode_select(anode_select),
        .segs(segs)
    );

    // read interface
    // No need to read the display data

    // write interface
    logic wr_en;
    assign wr_en = cs & write;


    // for the clock enable signal
    logic [31:0] config_reg;
    always_ff @(posedge clock) begin
        if (reset) begin
            config_reg <= 0;
        end else begin
            if (wr_en & (address == 5'h00)) begin
                config_reg <= wr_data;
            end
        end
    end
    assign enable_7seg = config_reg[0];


    // for the display data
    always_ff @(posedge clock) begin
        if (reset) begin
            display_data <= 0;
        end else begin
            if (wr_en & (address == 5'h01)) begin
                display_data <= wr_data;
            end
        end
    end

endmodule