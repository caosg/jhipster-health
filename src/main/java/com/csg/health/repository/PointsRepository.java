package com.csg.health.repository;

import com.csg.health.domain.Points;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the Points entity.
 */
public interface PointsRepository extends JpaRepository<Points,Long> {

    Page<Points> findAllByOrderByDateDesc(Pageable pageable);

    @Query("select points from Points points where points.user.login = ?#{principal.username}")
    List<Points> findByUserIsCurrentUser();

    @Query("select points from Points points where points.user.login = ?#{principal.username} order by points.date desc")
    Page<Points> findAllByUserIsCurrentUser(Pageable pageable);

    List<Points> findAllByDateBetweenAndUserLogin(LocalDate firstDate, LocalDate secondDate, String login);
}