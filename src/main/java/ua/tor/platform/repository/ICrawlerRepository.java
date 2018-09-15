package ua.tor.platform.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.persistent.Crawler;

import java.util.List;

/**
 * 
 * @author alex
 *
 */
public interface ICrawlerRepository extends MongoRepository<Crawler, ObjectId> {

	Crawler findOneById(ObjectId id);

	List<Crawler> findAllBySearchCondition(String searchCondition);
}
