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
@Document(collection = "crawler")
public class Crawler {

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	@Field(value = "search_condition")
	private String searchCondition;
	private Status status;
	@Field(value = "error_message")
	private String errorMessage;
	@Field(value = "created_date")
	private Date createdDate;
	@Field(value = "modified_date")
	private Date modifiedDate;

	public Crawler() {}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public String getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}

	@Override
	public String toString() {
		return "Crawler [id=" + id + ", searchCondition=" + searchCondition + ", status=" + status
				+ ", errorMessage=" + errorMessage + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + "]";
	}
}

