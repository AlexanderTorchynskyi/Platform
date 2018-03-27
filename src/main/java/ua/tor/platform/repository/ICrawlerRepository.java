package ua.tor.platform.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.model.Crawler;

/**
 * 
 * @author alex
 *
 */
public interface ICrawlerRepository extends MongoRepository<Crawler, ObjectId> {
	Crawler findOneById(ObjectId id);
}
