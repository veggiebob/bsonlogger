import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.*;

public class RobotSide { // also the Server Side
    public static void main (String[] args) {
        TestBean[] beans = new TestBean[10];
        for (int i = 0; i<beans.length; i++) {
            beans[i] = new TestBean("bean # " + i, (int)(Math.random() * 1000.0));
            System.out.println("created a bean " + beans[i]);
        }
        ByteArrayOutputStream test_data = new ByteArrayOutputStream();
        try {
            BsonGenerator jwriter = new BsonFactory().createJsonGenerator(test_data);
            jwriter.writeStartObject();
            jwriter.writeFieldName("the_logs");
            jwriter.writeStartArray();
            for (int i = 0; i < beans.length; i++) {
                //use the log to log themselves
                beans[i].encodeLog(jwriter);
            }
            jwriter.writeEndArray();
            jwriter.writeEndObject();
            jwriter.close();
            System.out.println("Server: Created data successfully.");
        } catch (IOException e) {
            System.out.println("Server: Failed to write data . . . ?");
        }

        //start the client
        new Thread(new DriverSide()).start();

        //set up a server and send the datA
        int port = 49152;
        try (
                ServerSocket serverSocket =
                        new ServerSocket(port);//create socket
                Socket clientSocket = serverSocket.accept();//connect
                OutputStream out = clientSocket.getOutputStream()
        ) {
            out.write(test_data.toByteArray());
            System.out.println("Server: Successfully connected to client.");
        } catch (IOException e) {
            System.out.println("Server: Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
