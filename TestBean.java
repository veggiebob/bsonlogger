import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public class TestBean {
    private String name;
    private int id;

    public TestBean () {
        name = "none";
        id = 0;
    }
    public TestBean (String _name, int _id) {
        name = _name;
        id = _id;
    }
    public String getName () {
        return name;
    }
    public int getId () {
        return id;
    }
    public void setName (String _name) {
        name = _name;
    }
    public void setId (int _id) {
        id = _id;
    }
    public void encodeLog (JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("name");
        gen.writeString(name);
        gen.writeFieldName("id");
        gen.writeNumber(id);
        gen.writeEndObject();
    }
    public void readLog (JsonParser jp) throws IOException {
        //event is START_OBJECT
        jp.nextToken();
        //event is FIELD_NAME
        setName(jp.nextTextValue());
        //event is the string value
        jp.nextToken();
        //event is FIELD_NAME
        int value = jp.nextIntValue(-1);
        //event is the integer value
        setId(value);
        //the next one should be END_OBJECT
    }
    public String toString () {
        return "TestBean named " + name + " with id " + id;
    }
}
