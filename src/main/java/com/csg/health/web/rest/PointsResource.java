package com.csg.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.csg.health.domain.Points;
import com.csg.health.repository.PointsRepository;
import com.csg.health.repository.UserRepository;
import com.csg.health.repository.search.PointsSearchRepository;
import com.csg.health.security.AuthoritiesConstants;
import com.csg.health.security.SecurityUtils;
import com.csg.health.web.rest.dto.PointsPerWeek;
import com.csg.health.web.rest.util.HeaderUtil;
import com.csg.health.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing Points.
 */
@RestController
@RequestMapping("/api")
public class PointsResource {

    private final Logger log = LoggerFactory.getLogger(PointsResource.class);

    @Inject
    private PointsRepository pointsRepository;

    @Inject
    private PointsSearchRepository pointsSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /pointss -> Create a new points.
     */
    @RequestMapping(value = "/pointss",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Points> createPoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to save Points : {}", points);
        if (points.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("points", "idexists", "A new points cannot already have an ID")).body(null);
        }
        // Set users ,if role is not a ADMIN,using current user.
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            log.debug("No user passed in,using current user:{}", SecurityUtils.getCurrentUserLogin());
            points.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        }
        Points result = pointsRepository.save(points);
        pointsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pointss/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("points", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /pointss -> Updates an existing points.
     */
    @RequestMapping(value = "/pointss",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Points> updatePoints(@Valid @RequestBody Points points) throws URISyntaxException {
        log.debug("REST request to update Points : {}", points);
        if (points.getId() == null) {
            return createPoints(points);
        }
        Points result = pointsRepository.save(points);
        pointsSearchRepository.save(result);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("points", points.getId().toString()))
                .body(result);
    }

    /**
     * GET  /pointss -> get all the pointss.
     */
    @RequestMapping(value = "/pointss",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Points>> getAllPointss(Pageable pageable)
            throws URISyntaxException {
        log.debug("REST request to get a page of Pointss");
        Page<Points> page;
        // get points by user
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            page = pointsRepository.findAllByOrderByDateDesc(pageable);
        } else {
            page = pointsRepository.findAllByUserIsCurrentUser(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pointss");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pointss/:id -> get the "id" points.
     */
    @RequestMapping(value = "/pointss/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Points> getPoints(@PathVariable Long id) {
        log.debug("REST request to get Points : {}", id);
        Points points = pointsRepository.findOne(id);
        return Optional.ofNullable(points)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pointss/:id -> delete the "id" points.
     */
    @RequestMapping(value = "/pointss/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePoints(@PathVariable Long id) {
        log.debug("REST request to delete Points : {}", id);
        pointsRepository.delete(id);
        pointsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("points", id.toString())).build();
    }

    /**
     * SEARCH  /_search/pointss/:query -> search for the points corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/pointss/{query}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Points> searchPointss(@PathVariable String query) {
        log.debug("REST request to search Pointss for query {}", query);
        return StreamSupport
                .stream(pointsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * GET  /points -> get all the points for the current week.
     */
    @RequestMapping(value = "/points-this-week",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PointsPerWeek> getPointsThisWeek() {
        // Get current date
        LocalDate now = LocalDate.now();
        // Get first day of week
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // Get last day of week
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        log.debug("Current date:{} ,Looking for points between: {} and {}", now, startOfWeek, endOfWeek);
        List<Points> points = pointsRepository.findAllByDateBetween(startOfWeek, endOfWeek);
        // filter by current user and sum the points
        Integer numPoints = points.stream()
                .filter(p -> p.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin()))
                .mapToInt(p -> p.getExercise() + p.getMeals() + p.getAlcohol())
                .sum();
        PointsPerWeek count = new PointsPerWeek(startOfWeek, numPoints);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
