package com.fuel.buddy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fuel.buddy.domain.Assets;

import com.fuel.buddy.repository.AssetsRepository;
import com.fuel.buddy.repository.UserRepository;
import com.fuel.buddy.repository.search.AssetsSearchRepository;
import com.fuel.buddy.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing Assets.
 */
@RestController
@RequestMapping("/api")
public class AssetsResource {

    private final Logger log = LoggerFactory.getLogger(AssetsResource.class);

    @Inject
    private AssetsRepository assetsRepository;

    @Inject
    private AssetsSearchRepository assetsSearchRepository;


    @Inject
    private UserRepository userRepository;

    /**
     * POST  /assets : Create a new assets.
     *
     * @param assets the assets to create
     * @return the ResponseEntity with status 201 (Created) and with body the new assets, or with status 400 (Bad Request) if the assets has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/assets")
    @Timed
    public ResponseEntity<Assets> createAssets(@Valid @RequestBody Assets assets) throws URISyntaxException {


        log.debug("REST request to save Assets : {}", assets);
        if (assets.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("assets", "idexists", "A new assets cannot already have an ID")).body(null);
        }
        Assets result = assetsRepository.save(assets);
        assetsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/assets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("assets", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /assets : Updates an existing assets.
     *
     * @param assets the assets to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated assets,
     * or with status 400 (Bad Request) if the assets is not valid,
     * or with status 500 (Internal Server Error) if the assets couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/assets")
    @Timed
    public ResponseEntity<Assets> updateAssets(@Valid @RequestBody Assets assets) throws URISyntaxException {
        log.debug("REST request to update Assets : {}", assets);
        if (assets.getId() == null) {
            return createAssets(assets);
        }
        Assets result = assetsRepository.save(assets);
        assetsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("assets", assets.getId().toString()))
            .body(result);
    }

    /**
     * GET  /assets : get all the assets.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of assets in body
     */
    @GetMapping("/assets")
    @Timed
    public List<Assets> getAllAssets() {
        log.debug("REST request to get all Assets");
        List<Assets> assets = assetsRepository.findAll();
        return assets;
    }

    /**
     * GET  /assets/:id : get the "id" assets.
     *
     * @param id the id of the assets to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the assets, or with status 404 (Not Found)
     */
    @GetMapping("/assets/{id}")
    @Timed
    public ResponseEntity<Assets> getAssets(@PathVariable Long id) {
        log.debug("REST request to get Assets : {}", id);
        Assets assets = assetsRepository.findOne(id);
        return Optional.ofNullable(assets)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /assets/:id : delete the "id" assets.
     *
     * @param id the id of the assets to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/assets/{id}")
    @Timed
    public ResponseEntity<Void> deleteAssets(@PathVariable Long id) {
        log.debug("REST request to delete Assets : {}", id);
        assetsRepository.delete(id);
        assetsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("assets", id.toString())).build();
    }

    /**
     * SEARCH  /_search/assets?query=:query : search for the assets corresponding
     * to the query.
     *
     * @param query the query of the assets search
     * @return the result of the search
     */
    @GetMapping("/_search/assets")
    @Timed
    public List<Assets> searchAssets(@RequestParam String query) {
        log.debug("REST request to search Assets for query {}", query);
        return StreamSupport
            .stream(assetsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
