/*
This should really be based on asingle socket.
*/
#include <stdlib.h>
#include <strings.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <time.h>
#define NUM(a) (sizeof(a) / sizeof(*a))
//Swap this to opcodes, and use binary not strings
//Then we have a hell of a lot less overhead, and its easier to send the camera
int sock;
//OPCodes
char op_init = 1;
char op_take_picture = 2;
char op_get_pixel = 3;
char op_set_pixel = 4;
char op_open_screen_stream = 5;
char op_close_screen_stream = 6;
char op_update_screen = 7;
char op_display_picture = 8;
char op_save_picture = 9;
char op_set_motor = 10;
char op_read_analog = 11;
char op_select_IO = 12;
char op_write_digital = 13;
char op_read_digital = 14;
char op_set_PWM = 15;
char op_connect_to_server = 16;
char op_send_to_server = 17;
char op_recieve_from_server = 18;
void sendData(char* data, int size);
char* itoc(int n);
void error(char *msg)
{
    perror(msg);
    exit(1);
}
int Sleep(int sec, int usec) {
     struct timespec tim, tim2;
     tim.tv_sec = sec;
     tim.tv_nsec = usec*1000;
     nanosleep(&tim , &tim2);
     return 1;
}
int init() {
	struct sockaddr_in server;

    //Create socket
    sock = socket(AF_INET , SOCK_STREAM , 0);
    if (sock == -1)
    {
        printf("Could not create socket");
    }
    puts("Socket created");

    server.sin_addr.s_addr = inet_addr("127.0.0.1");
    server.sin_family = AF_INET;
    server.sin_port = htons( 8080 );

    //Connect to remote server
    if (connect(sock , (struct sockaddr *)&server , sizeof(server)) < 0)
    {
        perror("connect failed. Error");
        return;
    }

    puts("Connected\n");
    char data[] = {op_init};
    sendData(data,1);
}
int write_digital(int chan, char level) {
    char data[] = {op_write_digital,chan,level};
    sendData(data,3);
}

int set_motor(int motor, int speed) {
    char data[] = {op_set_motor,motor,itoc(speed)[0],itoc(speed)[1],itoc(speed)[2],itoc(speed)[3]};
    sendData(data,6);
}
int read_analog(int chan) {
    char data[] = {op_read_analog,chan};
    sendData(data,2);
    char* received_int = malloc(4);
    char return_status = read(sock, received_int, 4);
    printf("TES:L %d%d%d%d\n",received_int[0],received_int[1],received_int[2],received_int[3]);
    if (return_status > 0)
       return ctoi(received_int);
    return 0;
}
char get_pixel(int row, int col, int color) {
	char data[] = {op_get_pixel,itoc(row)[0],itoc(row)[1],itoc(row)[2],itoc(row)[3],itoc(col)[0],itoc(col)[1],itoc(col)[2],itoc(col)[3],color};
    sendData(data,10);
    char* b = malloc(1);
    read(sock, b, 1);
    return b[0];
}
void take_picture() {
}
void sendData(char* data, int size)
{
    int n;
    n = write(sock,data,size);
    if (n < 0) error("ERROR writing to socket");
}
char* itoc(int n) {
	char bytes[4];
	bytes[0] = (n >> 24) & 0xFF;
	bytes[1] = (n >> 16) & 0xFF;
	bytes[2] = (n >> 8) & 0xFF;
	bytes[3] = n & 0xFF;
	return bytes;
}
int ctoi(int bytes[4]) {
    int i = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | (bytes[3]);
	return i;
}
