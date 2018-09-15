package ua.tor.platform.persistent;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "programing_laguages")
public class ProgramingLanguage {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @Field("name")
    private String name;

    public ProgramingLanguage(String name) {
        this.name = name;
    }

    public ProgramingLanguage() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
