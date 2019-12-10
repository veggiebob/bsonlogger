import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BSONDataReaderWriter {
    /**
     NOTES
     I'm going to assume there's 2 log types: messages (string) and quantities (numbers)
     there's less than 26 tags, each specified by a single character (specific values aren't really important until implementation in the robot code) (edited)
     a log contains a tag and a message
     a numerical log is a subset of a log; it also contains a number (and the corresponding number type). Number types aren't specified in JSON, but there are number types in BSON
        see: https://docs.mongodb.com/manual/reference/bson-types/
        (we should include number types: int, double, long)
     a log has methods:
        parse_token ( token ) -> void //read the data out of a token parsed from the JSON
        encode_log ( json ) -> JSON //take an existing JSON object and write the data to a *tag*
        toString () -> String //get a string representing the information
     */
    public static void main (String[] args) throws IOException {

        /*
        ObjectMapper mapper = new ObjectMapper();

        //json to object
        String test_json = "{\"name\": \"yeet\", \"id\": \"3\"}";
        TestBean boop = mapper.readValue(test_json, TestBean.class);
        System.out.println("json to object -------");
        System.out.println(boop);

        //object to json
        TestBean test_object = new TestBean("yeet2", 1000);
        String gen_json = mapper.writeValueAsString(test_object);
        System.out.println("object to json -------");
        System.out.println(gen_json);

        //some BSON
        //stolen from https://michelkraemer.com/binary-json-with-bson4jackson/
        System.out.println("BSON tests -------");
        TestBean bob = new TestBean();
        bob.setName("Bob");

        BsonFactory factory = new BsonFactory();

        //this is essentially robot side: write the json and encode in a byte stream
        //create a dummy object that was "sent" over here
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator gen = factory.createJsonGenerator(baos);
        gen.writeStartObject();
        gen.writeFieldName("name");
        gen.writeString("moooo");
        gen.close();

        //deserialize data (essentially the driver's side)
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        JsonParser parser = factory.createJsonParser(bais);
        TestBean clone_of_bob = new TestBean(); // make an empty container for the data dyNamically
        // dynamic class containers for data whattt
        // maybe it needs a kind of recursive class that can contain itself that can hold the data in the
        // navigate with events: see https://docs.oracle.com/javaee/7/api/javax/json/stream/JsonParser.html
        //      and https://docs.oracle.com/javaee/7/api/javax/json/stream/JsonParser.Event.html
        parser.nextToken();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            System.out.println("fieldname is " + fieldname);
            parser.nextToken();
            if (fieldname.equals("name")) {
                clone_of_bob.setName(parser.getText() + " clone");
            }
        }
        System.out.println("old bob: " + bob);
        System.out.println("new bob: " + clone_of_bob);
        */

        // general test

        // robot side
        TestBean[] beans = new TestBean[10];
        for (int i = 0; i<beans.length; i++) {
            beans[i] = new TestBean("bean # " + i, i);
            System.out.println("created a bean " + beans[i]);
        }
        ByteArrayOutputStream test_data = new ByteArrayOutputStream();
        BsonGenerator jwriter = new BsonFactory().createJsonGenerator(test_data);
        jwriter.writeStartObject();
        jwriter.writeFieldName("the_logs");
        jwriter.writeStartArray();
        for (int i = 0; i<beans.length; i++) {
            //use the log to log themselves
            beans[i].encodeLog(jwriter);
        }
        jwriter.writeEndArray();
        jwriter.writeEndObject();
        jwriter.close();

        // recieving side
        // data sent is test_data
        ByteArrayInputStream from_robot = new ByteArrayInputStream(test_data.toByteArray());
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
        for (TestBean b: logs)
            System.out.println(b);

    }
}
