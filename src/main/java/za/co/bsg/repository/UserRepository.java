package za.co.bsg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.bsg.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
