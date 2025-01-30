/******************************************************************************
* Copyright (C) 2023 Advanced Micro Devices, Inc. All Rights Reserved.
* SPDX-License-Identifier: MIT
******************************************************************************/
/*
 * helloworld.c: simple test application
 *
 * This application configures UART 16550 to baud rate 9600.
 * PS7 UART (Zynq) is not initialized by this application, since
 * bootrom/bsp configures it to baud rate 115200
 *
 * ------------------------------------------------
 * | UART TYPE   BAUD RATE                        |
 * ------------------------------------------------
 *   uartns550   9600
 *   uartlite    Configurable only in HW design
 *   ps7_uart    115200 (configured by bootrom/bsp)
 */

#include <stdio.h>
#include "platform.h"
#include "xil_printf.h"

#define TIMER 0xC0000100
#define TIMER_CNTL 0xC0000104
#define TIMER_CNTH 0xC0000108

#define SEG7_CONFIG 0xC0000180
#define SEG7_VALUE 0xC0000184

#define u64 uint64_t
#define u32 uint32_t
#define u8 uint8_t

// prepare ptrs
volatile uint32_t* timer_config = (uint32_t*) TIMER ;
volatile uint32_t* timer_count_low = (uint32_t*)(TIMER + 4); // second mistake: need to put parentheses around the addition
volatile uint32_t* timer_count_high = (uint32_t*)(TIMER + 8);
volatile uint32_t* seg7_config = (uint32_t*) SEG7_CONFIG;
volatile uint32_t* seg7_value = (uint32_t*) SEG7_VALUE;

volatile uint32_t * led_device = (uint32_t *) 0xC0000000;
volatile uint32_t * switches = (uint32_t *) (0xC0000000 + (1 << 7));


void enable_7seg_display() {
	*seg7_config = 1;
}

void write_7seg_display(u8 s7, u8 s6, u8 s5, u8 s4, u8 s3, u8 s2, u8 s1, u8 s0) {
	u32 value = (s7 & 0b1111);
	value <<= 4;
	value |= (s6 & 0b1111);
	value <<= 4;
	value |= (s5 & 0b1111);
	value <<= 4;
	value |= (s4 & 0b1111);
	value <<= 4;
	value |= (s3 & 0b1111);
	value <<= 4;
	value |= (s2 & 0b1111);
	value <<= 4;
	value |= (s1 & 0b1111);
	value <<= 4;
	value |= (s0 & 0b1111);
	// reading seg7_value does not work :(
	*seg7_value = value;
}

void start_counter() {
	//reset counter
	*timer_config = 0x00000001;
	//start counter
	*timer_config = 0x00000002;
}


u64 get_timer() {
	return (((u64)(*timer_count_high)) << 32) & ((u64)(*timer_count_low));
}

u64 s = 100000000;//100MHz
u64 ns = 10000;

void sleep(u64 limit) {
    volatile uint64_t counter_new, counter_old;
    	    //reset counter
    		*timer_config = 0x00000001;
    		//start counter
    		*timer_config = 0x00000002;

        	counter_new = *timer_count_high;
        	counter_new = *timer_count_low + (counter_new << 32);
        	counter_old = counter_new;

        	while((counter_old + limit) > counter_new) {
        		counter_new = *timer_count_high;
        	    counter_new = *timer_count_low + (counter_new << 32);
        	}
}

int main()
{
    init_platform();
    //print("Hello World\n\r");
    //print("Successfully ran Hello World application");

    enable_7seg_display();

    // test display
    //write_7seg_display(1, 2, 3, 4, 5, 6, 7, 8);

    start_counter();

    volatile u64 time = 1;

    while(1){
    	    sleep(s);
    	    u64 s0 = time;
    	    u64 s1 = s0/10;
    	    u64 s2 = s1/10;
    	    u64 s3 = s2/10;
    	    u64 s4 = s3/10;
    	    u64 s5 = s4/10;
    	    u64 s6 = s5/10;
    	    u64 s7 = s6/10;
    	    write_7seg_display(s7,s6%10,s5%10,s4%10,s3%10,s2%10,s1%10,s0%10);
    	    if (time % 2 == 0) {
    	    	*led_device = *switches;
    	    } else {
    	    	*led_device = ~*switches;
    	    }
    	    time += 1;
    }

    cleanup_platform();
    return 0;
}
