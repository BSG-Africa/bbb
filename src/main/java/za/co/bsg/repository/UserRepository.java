package za.co.bsg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.bsg.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findUserByUsername(String username);
}
