package ua.tor.platform.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.model.ParsedVacancy;

/**
 * 
 * @author alex
 *
 */
public interface IParsedVacancyRepository extends MongoRepository<ParsedVacancy, ObjectId> {

	List<ParsedVacancy> findByCrawlerId(ObjectId id);

	long countByCrawlerId(ObjectId id);
}
