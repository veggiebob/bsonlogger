import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public class Log {
    public static final char tag = 'L';
    protected char key;
    protected String message;
    public Log () {

    }
    public Log (char _key, String _message) {
        key = _key;
        message = _message;
    }
    protected void setKey(char key) {
        this.key = key;
    }
    protected void setMessage (String message) {
        this.message = message;
    }
    public void encodeLog (JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        //always have this :(
        gen.writeFieldName("tag");
        gen.writeString(""+tag);
        gen.writeFieldName("key");
        gen.writeString(""+key);
        gen.writeFieldName("message");
        gen.writeString(message);
        gen.writeEndObject();
    }
    public void readLog (JsonParser jp) throws IOException {
        //event should not be START_OBJECT
        //instead it should be FIELD_NAME
        jp.nextToken();

        //FIELD_NAME
        setKey(jp.nextTextValue().charAt(0));
        //value
        jp.nextToken();
        //FIELD_NAME
        setMessage(jp.nextTextValue());
        //value
    }
    public String toString () {
        return "Log -> " + key + ": " + message;
    }
    public String print () {
        return key + "| " + message;
    }
}
