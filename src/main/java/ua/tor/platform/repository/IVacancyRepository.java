package ua.tor.platform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.sun.corba.se.spi.ior.ObjectId;
import ua.tor.platform.model.Vacancy;


/**
 * 
 * @author alex
 *
 */
@Repository
public interface IVacancyRepository extends MongoRepository<Vacancy, ObjectId> {

}
