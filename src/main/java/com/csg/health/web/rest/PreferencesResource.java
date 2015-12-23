package com.csg.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.csg.health.domain.Preferences;
import com.csg.health.domain.User;
import com.csg.health.repository.PreferencesRepository;
import com.csg.health.repository.UserRepository;
import com.csg.health.repository.search.PreferencesSearchRepository;
import com.csg.health.security.SecurityUtils;
import com.csg.health.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Preferences.
 */
@RestController
@RequestMapping("/api")
public class PreferencesResource {

    private final Logger log = LoggerFactory.getLogger(PreferencesResource.class);

    @Inject
    private PreferencesRepository preferencesRepository;

    @Inject
    private PreferencesSearchRepository preferencesSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /preferencess -> Create a new preferences.
     */
    @RequestMapping(value = "/preferencess",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Preferences> createPreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to save Preferences : {}", preferences);
        if (preferences.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("preferences", "idexists", "A new preferences cannot already have an ID")).body(null);
        }
        Preferences result = preferencesRepository.save(preferences);
        preferencesSearchRepository.save(result);
        log.debug("Settings preferences for current user: {}", SecurityUtils.getCurrentUserLogin());
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        user.setPreferences(result);
        userRepository.save(user);
        return ResponseEntity.created(new URI("/api/preferencess/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("preferences", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /preferencess -> Updates an existing preferences.
     */
    @RequestMapping(value = "/preferencess",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Preferences> updatePreferences(@Valid @RequestBody Preferences preferences) throws URISyntaxException {
        log.debug("REST request to update Preferences : {}", preferences);
        if (preferences.getId() == null) {
            return createPreferences(preferences);
        }
        Preferences result = preferencesRepository.save(preferences);
        preferencesSearchRepository.save(result);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("preferences", preferences.getId().toString()))
                .body(result);
    }

    /**
     * GET  /preferencess -> get all the preferencess.
     */
    @RequestMapping(value = "/preferencess",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Preferences> getAllPreferencess(@RequestParam(required = false) String filter) {
        if ("user-is-null".equals(filter)) {
            log.debug("REST request to get all Preferencess where user is null");
            return StreamSupport
                    .stream(preferencesRepository.findAll().spliterator(), false)
                    .filter(preferences -> preferences.getUser() == null)
                    .collect(Collectors.toList());
        }
        log.debug("REST request to get all Preferencess");
        return preferencesRepository.findAll();
    }

    /**
     * GET  /preferencess/:id -> get the "id" preferences.
     */
    @RequestMapping(value = "/preferencess/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Preferences> getPreferences(@PathVariable Long id) {
        log.debug("REST request to get Preferences : {}", id);
        Preferences preferences = preferencesRepository.findOne(id);
        return Optional.ofNullable(preferences)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /preferencess/:id -> delete the "id" preferences.
     */
    @RequestMapping(value = "/preferencess/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePreferences(@PathVariable Long id) {
        log.debug("REST request to delete Preferences : {}", id);
        if (SecurityUtils.getCurrentUser() != null) {
            User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
            user.setPreferences(null);
            userRepository.save(user);
        }
        preferencesRepository.delete(id);
        preferencesSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("preferences", id.toString())).build();
    }

    /**
     * SEARCH  /_search/preferencess/:query -> search for the preferences corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/preferencess/{query}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Preferences> searchPreferencess(@PathVariable String query) {
        log.debug("REST request to search Preferencess for query {}", query);
        return StreamSupport
                .stream(preferencesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * GET  /my-preferences -> get the current user's preferences.
     */
    @RequestMapping(value = "/my-preferences",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Preferences> getUserPreferences() {
        String username = SecurityUtils.getCurrentUserLogin();
        log.debug("REST request to get Preferences : {}", username);
        User user = userRepository.findOneByLogin(username).get();
        if (user.getPreferences() != null) {
            return new ResponseEntity<>(user.getPreferences(), HttpStatus.OK);
        } else {
            Preferences defaultPreferences = new Preferences();
            defaultPreferences.setWeeklyGoal(10); // default
            return new ResponseEntity<>(defaultPreferences, HttpStatus.OK);
        }
    }
}
