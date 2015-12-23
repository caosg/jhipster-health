package com.csg.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.csg.health.domain.Weight;
import com.csg.health.repository.UserRepository;
import com.csg.health.repository.WeightRepository;
import com.csg.health.repository.search.WeightSearchRepository;
import com.csg.health.security.AuthoritiesConstants;
import com.csg.health.security.SecurityUtils;
import com.csg.health.web.rest.dto.WeightByPeriod;
import com.csg.health.web.rest.util.HeaderUtil;
import com.csg.health.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing Weight.
 */
@RestController
@RequestMapping("/api")
public class WeightResource {

    private final Logger log = LoggerFactory.getLogger(WeightResource.class);

    @Inject
    private WeightRepository weightRepository;

    @Inject
    private WeightSearchRepository weightSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /weights -> Create a new weight.
     */
    @RequestMapping(value = "/weights",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weight> createWeight(@Valid @RequestBody Weight weight) throws URISyntaxException {
        log.debug("REST request to save Weight : {}", weight);
        if (weight.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("weight", "idexists", "A new weight cannot already have an ID")).body(null);
        }
        // set weight's user
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin());
            weight.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        }
        if (weight.getTimestamp() == null) {
            weight.setTimestamp(ZonedDateTime.now());
        }
        Weight result = weightRepository.save(weight);
        weightSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/weights/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("weight", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /weights -> Updates an existing weight.
     */
    @RequestMapping(value = "/weights",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weight> updateWeight(@Valid @RequestBody Weight weight) throws URISyntaxException {
        log.debug("REST request to update Weight : {}", weight);
        if (weight.getId() == null) {
            return createWeight(weight);
        }
        Weight result = weightRepository.save(weight);
        weightSearchRepository.save(result);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("weight", weight.getId().toString()))
                .body(result);
    }

    /**
     * GET  /weights -> get all the weights.
     */
    @RequestMapping(value = "/weights",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Weight>> getAllWeights(Pageable pageable)
            throws URISyntaxException {
        log.debug("REST request to get a page of Weights");
        Page<Weight> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN))
            page = weightRepository.findAllByOrderByTimestampDesc(pageable);
        else
            page = weightRepository.findAllByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/weights");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bp-by-days -> get all the weigh-ins by last x days.
     */
    @RequestMapping(value = "/weight-by-days/{days}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WeightByPeriod> getByDays(@PathVariable int days) {
        LocalDate today = LocalDate.now();
        LocalDate previousDate = today.minusDays(days);
        ZonedDateTime daysAgo = previousDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime rightNow = today.atStartOfDay(ZoneId.systemDefault());
        List<Weight> weighIns = weightRepository.findAllByTimestampBetweenAndUserLoginOrderByTimestampDesc(daysAgo, rightNow,SecurityUtils.getCurrentUserLogin());
        WeightByPeriod response = new WeightByPeriod("Last " + days + " Days", weighIns);
        log.debug("Last ({}-{}) {} Days,weight ints is {}",daysAgo,rightNow,days,weighIns.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET  /bp-by-days -> get all the blood pressure readings for a particular month.
     */
    @RequestMapping(value = "/weight-by-month/{date}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WeightByPeriod> getByMonth(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate firstDay = date.withDayOfMonth(1);
        LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

        List<Weight> weighIns = weightRepository.
                findAllByTimestampBetweenAndUserLoginOrderByTimestampDesc(firstDay.atStartOfDay(ZoneId.systemDefault()),
                        lastDay.plusDays(1).atStartOfDay(ZoneId.systemDefault()),SecurityUtils.getCurrentUserLogin());

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
        String yearAndMonth = firstDay.format(formatter);

        WeightByPeriod response = new WeightByPeriod(yearAndMonth, weighIns);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET  /weights/:id -> get the "id" weight.
     */
    @RequestMapping(value = "/weights/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weight> getWeight(@PathVariable Long id) {
        log.debug("REST request to get Weight : {}", id);
        Weight weight = weightRepository.findOne(id);
        return Optional.ofNullable(weight)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /weights/:id -> delete the "id" weight.
     */
    @RequestMapping(value = "/weights/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWeight(@PathVariable Long id) {
        log.debug("REST request to delete Weight : {}", id);
        weightRepository.delete(id);
        weightSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("weight", id.toString())).build();
    }

    /**
     * SEARCH  /_search/weights/:query -> search for the weight corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/weights/{query}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Weight> searchWeights(@PathVariable String query) {
        log.debug("REST request to search Weights for query {}", query);
        return StreamSupport
                .stream(weightSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }
}
