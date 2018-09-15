package ua.tor.platform.persistent;

import java.util.Set;
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
@Document(collection = "parsed_vacancy")
public class ParsedVacancy {


	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	@JsonSerialize(using = ToStringSerializer.class)
	@Field("crawler_id")
	private ObjectId crawlerId;
	private Status status;
	private Set<String> description;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCrawlerId() {
		return crawlerId;
	}

	public void setCrawlerId(ObjectId crawlerId) {
		this.crawlerId = crawlerId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Set<String> getDescription() {
		return description;
	}

	public void setDescription(Set<String> description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ParsedVacancy [id=" + id + ", crawlerId=" + crawlerId + ", status=" + status
				+ ", description=" + description + "]";
	}
}
