package ua.tor.platform.persistent;

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
@Document(collection = "vacancy")
public class Vacancy {

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	@JsonSerialize(using = ToStringSerializer.class)
	@Field("crawler_id")
	private ObjectId crawlerId;
	private Status status;
	private String title;
	private String link;
	private String city;
	private String company;
	private String description;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Vacancy [id=" + id + ", crawlerId=" + crawlerId + ", status=" + status + ", title="
				+ title + ", link=" + link + ", city=" + city + ", company=" + company
				+ ", description=" + description + "]";
	}
}
