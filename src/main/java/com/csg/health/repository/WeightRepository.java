package com.csg.health.repository;

import com.csg.health.domain.Weight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Weight entity.
 */
public interface WeightRepository extends JpaRepository<Weight,Long> {

    @Query("select weight from Weight weight where weight.user.login = ?#{principal.username}")
    List<Weight> findByUserIsCurrentUser();

    Page<Weight> findAllByOrderByTimestampDesc(Pageable pageable);

    @Query("select weight from Weight weight where weight.user.login = ?#{principal.username} order by weight.timestamp desc")
    Page<Weight> findAllByUserIsCurrentUser(Pageable pageable);


    List<Weight> findAllByTimestampBetweenAndUserLoginOrderByTimestampDesc(ZonedDateTime daysAgo, ZonedDateTime rightNow, String currentUserLogin);
}
