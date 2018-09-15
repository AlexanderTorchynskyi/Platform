package ua.tor.platform.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.tor.platform.persistent.Status;
import ua.tor.platform.persistent.Vacancy;


/**
 * 
 * @author alex
 *
 */
@Repository
public interface IVacancyRepository extends MongoRepository<Vacancy, ObjectId> {

	List<Vacancy> findByCrawlerId(ObjectId crawlerId);
	
	List<Vacancy> findByStatus(Status status);

}
