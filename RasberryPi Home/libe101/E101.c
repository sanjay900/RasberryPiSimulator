/*
This should really be based on asingle socket.
*/
#include <stdlib.h>
#include <strings.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
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
void itoc(int n, char* bytes);
void ctoi(char bytes[4], int *n);
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
        return 0;
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
	char* speed_b = malloc(4);
	itoc(speed,speed_b);
    char data[] = {op_set_motor,motor,speed_b[0],speed_b[1],speed_b[2],speed_b[3]};
    sendData(data,6);
}
int read_analog(int chan) {
    char data[] = {op_read_analog,chan};
    sendData(data,2);
    char* datas = malloc(1);
    char* recieved_int = malloc(4);
    int recieved = 0;
    int rc = 0;
    while (rc < 4) {
    	char return_status = read(sock, datas, 1);
    	if (return_status < 0) {
	    return 0;
    	}
	recieved_int[rc] = datas[0];
	rc++;
	//printf("%d\n",rc);
    }
    ctoi(recieved_int,&recieved);
    return recieved;
}
unsigned char get_pixel(int row, int col, int color) {
	char* row_b = malloc(4);
	char* col_b = malloc(4);
	itoc(row,row_b);
	itoc(col,col_b);
	char data[] = {op_get_pixel,row_b[0],row_b[1],row_b[2],row_b[3],col_b[0],col_b[1],col_b[2],col_b[3],color};
    sendData(data,10);
    char* b = malloc(1);
    read(sock, b, 1);
    return b[0]+127;
}
void take_picture() {
char data[] = {op_take_picture};
    sendData(data,1);
}
void sendData(char* data, int size)
{
    int n;
    n = write(sock,data,size);
    if (n < 0) error("ERROR writing to socket");
}
void itoc(int n, char* bytes) {
	*bytes = (n >> 24) & 0xFF;
	*(bytes+1) = (n >> 16) & 0xFF;
	*(bytes+2) = (n >> 8) & 0xFF;
	*(bytes+3) = n & 0xFF;
}
void ctoi(char bytes[4], int *n) {
    (*n) = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | (bytes[3]);
}

int connect_to_server( char server_addr[15],int port) {

}
int send_to_server(char message[24]) {

}
int receive_from_server(char message[24]){

}
