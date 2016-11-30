package za.co.bsg.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.bsg.models.User;

public interface AppUserRepository extends JpaRepository<User, Long> {
    public User findOneByUsername(String username);
}
