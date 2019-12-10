import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.sql.Timestamp;

public class TimeLog extends Log {
    public static final char tag = 'T';
    protected long time;
    public TimeLog () {
        super();
    }
    public TimeLog (char _key, long _time, String message) {
        super(_key, message);
        time = _time;
    }
    @Override
    public void encodeLog (JsonGenerator gen) throws java.io.IOException {
        gen.writeStartObject();
        gen.writeFieldName("key");
        gen.writeString(""+key);
        gen.writeFieldName("message");
        gen.writeString(message);
        gen.writeFieldName("time");
        gen.writeNumber(time);
        gen.writeEndObject();
    }
    @Override
    public void readLog (JsonParser jp) throws java.io.IOException {
        jp.nextToken();
        setKey(jp.nextTextValue().charAt(0));
        jp.nextToken();
        setMessage(jp.nextTextValue());
        jp.nextToken();
        setTime(jp.nextLongValue(0L));
    }
    protected void setTime (long time) { this.time = time; }
    public String toString () {
        return "TimeLog -> " + new Timestamp(time).toString() + " " + key + ": " + message;
    }
    public String print () {
        return new Timestamp(time).toString() + " | " + key + " | " + message;
    }
}
