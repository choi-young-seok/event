package io.api.event.util.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;
import java.util.Arrays;

/**
 * Errors객체는 JavaBean Spec을 따르지 않으므로 BeanSerializer를 통해 JSON으로 변환될 수 없음
 * 따라서 Errors객체를 JSON으로 변환하는 Serializer를 구현하고, ObjectMapper가 Errors객체를 반환하는 경우에
 * 해당 Serializer를 통해 Errors객체를 JSON으로 변환 할 수 있도록 @JsonComponent annotation을 명시하여
 * 해당 Serializer를 ObjectMppaer에 등록한다.
 * */
@JsonComponent
@Slf4j
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        errors.getFieldErrors().stream().forEach(error -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", error.getObjectName());
                jsonGenerator.writeStringField("field", error.getField());
                jsonGenerator.writeStringField("code", error.getCode());
                jsonGenerator.writeStringField("defaultMessage", error.getDefaultMessage());
                Object rejectedValue = error.getRejectedValue();
                if(rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
                }
                jsonGenerator.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        errors.getGlobalErrors().stream().forEach(error -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", error.getObjectName());
                jsonGenerator.writeStringField("code", error.getCode());
                jsonGenerator.writeStringField("defaultMessage", error.getDefaultMessage());

                Object[] arguments = error.getArguments();
                if(arguments != null){
                    jsonGenerator.writeStringField("argument", Arrays.toString(error.getArguments()));
                }
                jsonGenerator.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        jsonGenerator.writeEndArray();
    }
}
