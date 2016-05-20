# RasberryPiSimulator
A simulator for ENGR101 Students
It needs a bit of work as some values are way off, but when tested it does follow a line (provided you write code for it to do so)
Currently Developed commands:
- Sleep
- write_digital
- set_motor
- read_analogue
- get_pixel

This is currently only for linux users
To start the simulator itself, run src/main/java/main/Main.java


To start up a pi emulator, run ./boot.sh
However, you can also just directly compile against the libe101 library
and you wont have any issues.

NOTE:
WHEN IMPORTING, use 
extern "C" unsigned char get_pixel(int row,int col,int color);
for get_pixel.

