package main;

import simbad.gui.Simbad;
import maze.Maze;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    //Should split reading and writing into seperate threads
    private static void startSocket() throws IOException{
        ServerSocket serversocket = new ServerSocket(8080); // Create and connect the socket
        System.setProperty("j3d.implicitAntialiasing", "true");

        new Maze(5,5);
        //Scale everything correctly, its currently 10x10 instead of 2x2
        new Simbad(arena ,false);
        while (true) {
            Socket socket = serversocket.accept();
            if (last != null) last.interrupt();
            last = new Thread(()->{
                try {
                    DataInputStream dIn = new DataInputStream(socket.getInputStream());
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    BufferedImage bim  =  new BufferedImage(arena.camera.getImageWidth(), arena.camera.getImageHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    while (!socket.isClosed() && !Thread.interrupted()) {
                        if (dIn.available() > 0) {
                            switch (dIn.readByte()) {
                                case op_init:
                                    System.out.println("Connection established from pi");
                                    break;
                                case op_read_analog:
                                    dOut.writeByte(127);
                                    break;
                                case op_set_motor:
                                    setMotor(dIn.readByte(),dIn.readInt());
                                    break;
                                //For this, there is no point in simulating leds.
                                case op_write_digital:
                                    break;
                                case op_get_pixel:
                                    arena.camera.copyVisionImage(bim);
                                    int row = dIn.readInt();
                                    int col = dIn.readInt();
                                    Color c = new Color(bim.getRGB(row,col));
                                    System.out.println((c.getRed()+c.getGreen()+c.getBlue())/3);
                                    switch (dIn.readByte()) {
                                        case 0:
                                            dOut.writeByte(c.getRed());
                                            break;
                                        case 1:
                                            dOut.writeByte(c.getGreen());
                                            break;
                                        case 2:
                                            dOut.writeByte(c.getBlue());
                                            break;
                                        case 3:
                                            dOut.writeByte((c.getRed()+c.getGreen()+c.getBlue())/3);
                                            break;
                                    }
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
        arena.robot.setWheelVelocity(motor,value/255d);
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
