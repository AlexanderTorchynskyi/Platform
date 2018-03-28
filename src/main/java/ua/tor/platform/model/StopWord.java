package ua.tor.platform.model;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @author alex
 *
 */
@Document(collection = "stop_word")
public class StopWord {

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	private String key;
	@Field(value = "created_date")
	private Date createdDate;
	@Field(value = "modified_date")
	private Date modifiedDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String toString() {
		return "StopWord [id=" + id + ", key=" + key + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + "]";
	}
}
