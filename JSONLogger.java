import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class JSONLogger {
    public ArrayList<Log> logs;
    public JSONLogger () {
        logs = new ArrayList<>();
    }
    public void add (char key, String message) {
        logs.add(new Log(key, message));
    }
    public void add (char key, long time, String message) {
        logs.add(new TimeLog(key, time, message));
    }
    public void recieveData () {
        recieveData("127.0.0.1", 49152);
    }
    public void recieveData (String ip, int port) {
        System.out.println("Started client");
        try (
                Socket echoSocket = new Socket(ip, port);
                InputStream incoming_data = echoSocket.getInputStream()
        ) {
            System.out.println("client: connected");
            while (true) // wait for the data
                if (incoming_data.available() > 0)
                    break;
            System.out.println("client: recieved data");
            byte[] packet = new byte[incoming_data.available()];
            incoming_data.read(packet);
            ByteArrayInputStream from_robot = new ByteArrayInputStream(packet);

            //ByteArrayInputStream from_robot = new ByteArrayInputStream(test_data.toByteArray());
            JsonParser parseIt = new BsonFactory().createJsonParser(from_robot);
            JsonToken currentEvent;
            parseIt.nextToken();
            while ((currentEvent = parseIt.nextToken()) != JsonToken.START_ARRAY) {
                //System.out.println("passed event " + currentEvent);
            }
            while ((currentEvent = parseIt.nextToken()) != JsonToken.END_ARRAY) {
                if (currentEvent == JsonToken.START_OBJECT) {
                    Log newLog;
                    parseIt.nextToken();
                    char tag = parseIt.nextTextValue().charAt(0);
                    switch (tag) {
                        case Log.tag:
                            newLog = new Log();
                            break;
                        case TimeLog.tag:
                            newLog = new TimeLog();
                            break;
                        default:
                            newLog = new Log();
                    }
                    logs.add(newLog);
                    logs.get(logs.size() - 1).readLog(parseIt);
                }
            }
            System.out.println("client: Data recieved by client");
            System.out.println("client: Closing socket");
            echoSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void sendData () { sendData(49152); }
    public void sendData (int port) {
        ByteArrayOutputStream test_data = new ByteArrayOutputStream();
        try {
            BsonGenerator jwriter = new BsonFactory().createJsonGenerator(test_data);
            jwriter.writeStartObject();
            jwriter.writeFieldName("the_logs");
            jwriter.writeStartArray();
            for (Log log: logs) {
                //use the log to log themselves
                log.encodeLog(jwriter);
            }
            jwriter.writeEndArray();
            jwriter.writeEndObject();
            jwriter.close();
            System.out.println("Server: Created data successfully.");
        } catch (IOException e) {
            System.out.println("Server: Failed to write data . . . ?");
        }

        try (
                ServerSocket serverSocket =
                        new ServerSocket(port);//create socket
                Socket clientSocket = serverSocket.accept();//connect
                OutputStream out = clientSocket.getOutputStream()
        ) {
            out.write(test_data.toByteArray());
            serverSocket.close();
            System.out.println("Server: Successfully connected to client.");
        } catch (IOException e) {
            System.out.println("Server: Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    public String toString () {
        String str = "";
        for (Log l: logs) {
            str += l.toString() + "\n";
        }
        return str;
    }
    //todo: public void writeToFile (String location) {}
    public static void main (String[] args) {
        JSONLogger js = new JSONLogger();
        js.add('b', "bbbbbbb");
        js.add('g', 29387592835L, "git cool");
        //should normally start this recieving end AFTER the send, but
        new Thread(new Runnable() {
            @Override
            public void run() {
                js.recieveData();
            }
        }).start();
        js.sendData();
        System.out.println("data recieved:");
        System.out.println(js);
    }
}
