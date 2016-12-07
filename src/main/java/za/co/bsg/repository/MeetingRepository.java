package za.co.bsg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.bsg.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
