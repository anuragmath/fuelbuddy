package com.fuel.buddy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fuel.buddy.domain.Fuel;

import com.fuel.buddy.repository.FuelRepository;
import com.fuel.buddy.repository.search.FuelSearchRepository;
import com.fuel.buddy.security.AuthoritiesConstants;
import com.fuel.buddy.web.rest.util.HeaderUtil;
import com.fuel.buddy.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
 * REST controller for managing Fuel.
 */
@RestController
@RequestMapping("/api")
public class FuelResource {

    private final Logger log = LoggerFactory.getLogger(FuelResource.class);

    @Inject
    private FuelRepository fuelRepository;

    @Inject
    private FuelSearchRepository fuelSearchRepository;

    /**
     * POST  /fuels : Create a new fuel.
     *
     * @param fuel the fuel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new fuel, or with status 400 (Bad Request) if the fuel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/fuels")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Fuel> createFuel(@Valid @RequestBody Fuel fuel) throws URISyntaxException {
        log.debug("REST request to save Fuel : {}", fuel);
        if (fuel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("fuel", "idexists", "A new fuel cannot already have an ID")).body(null);
        }
        Fuel result = fuelRepository.save(fuel);
        fuelSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/fuels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("fuel", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /fuels : Updates an existing fuel.
     *
     * @param fuel the fuel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated fuel,
     * or with status 400 (Bad Request) if the fuel is not valid,
     * or with status 500 (Internal Server Error) if the fuel couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/fuels")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Fuel> updateFuel(@Valid @RequestBody Fuel fuel) throws URISyntaxException {
        log.debug("REST request to update Fuel : {}", fuel);
        if (fuel.getId() == null) {
            return createFuel(fuel);
        }
        Fuel result = fuelRepository.save(fuel);
        fuelSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("fuel", fuel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /fuels : get all the fuels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of fuels in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/fuels")
    @Timed
    public ResponseEntity<List<Fuel>> getAllFuels(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Fuels");
        Page<Fuel> page = fuelRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/fuels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /fuels/:id : get the "id" fuel.
     *
     * @param id the id of the fuel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the fuel, or with status 404 (Not Found)
     */
    @GetMapping("/fuels/{id}")
    @Timed
    public ResponseEntity<Fuel> getFuel(@PathVariable Long id) {
        log.debug("REST request to get Fuel : {}", id);
        Fuel fuel = fuelRepository.findOne(id);
        return Optional.ofNullable(fuel)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /fuels/:id : delete the "id" fuel.
     *
     * @param id the id of the fuel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/fuels/{id}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteFuel(@PathVariable Long id) {
        log.debug("REST request to delete Fuel : {}", id);
        fuelRepository.delete(id);
        fuelSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("fuel", id.toString())).build();
    }

    /**
     * SEARCH  /_search/fuels?query=:query : search for the fuel corresponding
     * to the query.
     *
     * @param query the query of the fuel search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/fuels")
    @Timed
    public ResponseEntity<List<Fuel>> searchFuels(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Fuels for query {}", query);
        Page<Fuel> page = fuelSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/fuels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
