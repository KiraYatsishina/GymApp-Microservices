package micro.authserver.repository;

import micro.authserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    long countByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

}
