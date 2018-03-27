package ua.tor.platform.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.model.ParsedVacancy;

/**
 * 
 * @author alex
 *
 */
public interface IParsedVacancyRepository extends MongoRepository<ParsedVacancy, ObjectId> {

}
