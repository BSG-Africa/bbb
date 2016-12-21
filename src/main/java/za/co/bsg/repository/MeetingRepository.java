package za.co.bsg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    <S extends Meeting> List<S> findByCreatedBy(User user);

    <S extends Meeting> List<S> findByModerator(User moderator);

    <S extends Meeting> List<S> findByStatus(String status);
}
