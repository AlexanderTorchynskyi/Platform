package ua.tor.platform.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.tor.platform.model.StopWord;

public interface IStopWordRepository extends MongoRepository<StopWord, ObjectId> {
	
}
