package p1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;
import java.util.Random;

public class DataServer {
    public static void main(String[] args) throws IOException {
       // tumblingProcessingTime();
        //tumblingEventTime();
        ipData();
    }

    public static void tumblingProcessingTime() throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());

            BufferedReader br = new BufferedReader(new FileReader("avg"));

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String line;
                while ((line = br.readLine()) != null) {

                    out.println(line);
                    Thread.sleep(50);
                }

            } finally {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            listener.close();
        }
    }

    public static void ipData() throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());
            Thread.sleep(3000);
            BufferedReader br = new BufferedReader(new FileReader("ip_data.txt"));

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println("Send" + line);
                    out.println(line);
                    Thread.sleep(1000);
                }
                Thread.sleep(20000);
            } finally {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            listener.close();
        }
    }

    public static void tumblingEventTime() throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Random rand = new Random();
                Date d = new Date();
                while (true) {
                    int i = rand.nextInt(100);
                    String s = "" + System.currentTimeMillis() + "," + i;
                    System.out.println(s);
                    /* <timestamp>,<random-number> */
                    out.println(s);
                    Thread.sleep(50);
                }

            } finally {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            listener.close();
        }
    }

    public static void sesionProcessingTime() throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());

            BufferedReader br = new BufferedReader(new FileReader("avg"));

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    count++;

                    out.println(line);
                    if (count >= 10) {
                        count = 0;
                        Thread.sleep(1000);
                    } else
                        Thread.sleep(50);
                }

            } finally {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            listener.close();
        }
    }

    public static void sesionEventTime() throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Random rand = new Random();
                Date d = new Date();
                int count = 0;
                while (true) {
                    count++;
                    int i = rand.nextInt(100);
                    String s = "" + System.currentTimeMillis() + "," + i;
                    System.out.println(s);
                    /* <timestamp>,<random-number> */
                    out.println(s);
                    if (count >= 10) {
                        System.out.println("*********************");
                        Thread.sleep(1000);
                        count = 0;
                    } else
                        Thread.sleep(50);
                }

            } finally {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            listener.close();
        }
    }
}

