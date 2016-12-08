package za.co.bsg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.bsg.model.Meeting;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    <S extends Meeting> List<S> findByCreatedBy(int userId);
}
