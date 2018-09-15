package ua.tor.platform.web.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

public class IncrementorRequest {
    @NotNull
    @JsonProperty("id")
    private ObjectId objectId;

    public IncrementorRequest(@NotNull ObjectId objectId) {
        this.objectId = objectId;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }
}
