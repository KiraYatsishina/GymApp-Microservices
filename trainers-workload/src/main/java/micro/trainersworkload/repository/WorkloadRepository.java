package micro.trainersworkload.repository;

import micro.trainersworkload.model.Workload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface WorkloadRepository extends MongoRepository<Workload, String> {

    Optional<Workload> findByUsername(String username);

    @Query("{ 'firstName': ?0, 'lastName': ?1 }")
    Optional<Workload> findByFirstNameAndLastName(String firstName, String lastName);
}
