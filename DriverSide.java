import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.undercouch.bson4jackson.BsonFactory;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

public class DriverSide implements Runnable { // client side
    public static int bytes_to_int (byte[] b) {
        return ((((((b[0] << 8) + b[1]) << 8) + b[2]) << 8) + b[3]) << 8;
    }
    @Override
    public void run () {

        //get the data from the TCP socket

        String hostName = "127.0.0.1";
        //local is 127.0.0.1
        //my computer 192.168.0.109
        //kitchen computer 192.168.0.4
        int portNumber = 49152;
        System.out.println("Started client");
        try (
                Socket echoSocket = new Socket(hostName, portNumber);
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
            ArrayList<TestBean> logs = new ArrayList<>();
            JsonToken currentEvent;
            parseIt.nextToken();
            while ((currentEvent = parseIt.nextToken()) != JsonToken.START_ARRAY) {
                System.out.println("passed event " + currentEvent);
            }
            while ((currentEvent = parseIt.nextToken()) != JsonToken.END_ARRAY) {
                if (currentEvent == JsonToken.START_OBJECT) {
                    logs.add(new TestBean());
                    logs.get(logs.size() - 1).readLog(parseIt);
                }
            }
            // data is gotten (hopefully)
            System.out.println("Data recieved by client:");
            for (TestBean b : logs)
                System.out.println(b);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
