package br.com.aula.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;

public class GenderSerializer extends JsonObjectSerializer<String> {

    @Override
    protected void serializeObject(String value, JsonGenerator jgen,
                                   SerializerProvider provider) throws IOException {
        String formatedGender = "Male".equalsIgnoreCase(value) ? "M" : "F";
        jgen.writeString(formatedGender);
    }
}
