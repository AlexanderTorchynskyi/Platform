package ua.tor.platform.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.persistent.ProgramingLanguage;

public interface IProgrammingLanguageRepository extends MongoRepository<ProgramingLanguage, ObjectId> {

}
