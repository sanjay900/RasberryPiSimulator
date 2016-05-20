package main;

import simbad.gui.Simbad;
import maze.Maze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sanjay on 19/04/16.
 */
public class Main {
    public static MyArena arena = new MyArena();
    public static void main(String[] args) throws IOException {
        startSocket();
    }
    static Thread last = null;
    static int[] data;
    //Should split reading and writing into seperate threads
    private static void startSocket() throws IOException{
        ServerSocket serversocket = new ServerSocket(8080); // Create and connect the socket
        System.setProperty("j3d.implicitAntialiasing", "true");

        new Maze(5,5);
        new Simbad(arena ,false);
        while (true) {
            Socket socket = serversocket.accept();
            if (last != null) last.interrupt();
            last = new Thread(()->{
                try {
                    DataInputStream dIn = new DataInputStream(socket.getInputStream());
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    int row,col,red,green,blue;
                    data = ((DataBufferInt) arena.camera.visionImage.getRaster().getDataBuffer()).getData();
                    while (!socket.isClosed() && !Thread.interrupted()) {
                        if (dIn.available() > 0) {
                            switch (dIn.readByte()) {
                                case op_init:
                                    System.out.println("Connection established from pi");
                                    break;
                                case op_read_analog:
                                    byte sensor = dIn.readByte();
                                    dOut.writeInt((int)(arena.b.getMeasurement(sensor)*255));
                                    dOut.flush();
                                    break;
                                case op_set_motor:
                                    setMotor(dIn.readByte(),dIn.readInt());
                                    break;
                                //For this, there is no point in simulating leds.
                                case op_write_digital:
                                    break;
                                case op_take_picture:
                                    break;
                                case op_get_pixel:
                                    row = dIn.readInt();
                                    col = dIn.readInt();
                                    red = (data[col*bim.getWidth()+row] >> 16) & 0x000000FF;
                                    green = (data[col*bim.getWidth()+row] >>8 ) & 0x000000FF;
                                    blue = (data[col*bim.getWidth()+row]) & 0x000000FF;
                                    switch (dIn.readByte()) {
                                        case 0:
                                            dOut.write(red-127);
                                            break;
                                        case 1:
                                            dOut.write(green-127);
                                            break;
                                        case 2:
                                            dOut.write(blue-127);
                                            break;
                                        case 3:
                                            dOut.write(((red+green+blue)/3)-127);
                                            break;
                                    }
                                    dOut.flush();
                            }
                        }


                    }
                    System.out.println("Connection from pi lost");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            last.start();
        }

    }
    private static void setMotor(int motor, int value) {
        arena.robot.setWheelVelocity(motor,-value/255d);
    }
    static final int op_init = 1;
    static final int op_take_picture = 2;
    static final int op_get_pixel = 3;
    static final int op_set_pixel = 4;
    static final int op_open_screen_stream = 5;
    static final int op_close_screen_stream = 6;
    static final int op_update_screen = 7;
    static final int op_display_picture = 8;
    static final int op_save_picture = 9;
    static final int op_set_motor = 10;
    static final int op_read_analog = 11;
    static final int op_select_IO = 12;
    static final int op_write_digital = 13;
    static final int op_read_digital = 14;
    static final int op_set_PWM = 15;
    static final int op_connect_to_server = 16;
    static final int op_send_to_server = 17;
    static final int op_recieve_from_server = 18;

}
