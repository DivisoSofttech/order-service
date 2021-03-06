package com.diviso.graeshoppe.order.web.rest;

import com.diviso.graeshoppe.order.service.UniqueOrderIDService;
import com.diviso.graeshoppe.order.web.rest.errors.BadRequestAlertException;
import com.diviso.graeshoppe.order.service.dto.UniqueOrderIDDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.diviso.graeshoppe.order.domain.UniqueOrderID}.
 */
@RestController
@RequestMapping("/api")
public class UniqueOrderIDResource {

    private final Logger log = LoggerFactory.getLogger(UniqueOrderIDResource.class);

    private static final String ENTITY_NAME = "orderUniqueOrderId";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UniqueOrderIDService uniqueOrderIDService;

    public UniqueOrderIDResource(UniqueOrderIDService uniqueOrderIDService) {
        this.uniqueOrderIDService = uniqueOrderIDService;
    }

    /**
     * {@code POST  /unique-order-ids} : Create a new uniqueOrderID.
     *
     * @param uniqueOrderIDDTO the uniqueOrderIDDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uniqueOrderIDDTO, or with status {@code 400 (Bad Request)} if the uniqueOrderID has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/unique-order-ids")
    public ResponseEntity<UniqueOrderIDDTO> createUniqueOrderID(@RequestBody UniqueOrderIDDTO uniqueOrderIDDTO) throws URISyntaxException {
        log.debug("REST request to save UniqueOrderID : {}", uniqueOrderIDDTO);
        if (uniqueOrderIDDTO.getId() != null) {
            throw new BadRequestAlertException("A new uniqueOrderID cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UniqueOrderIDDTO result = uniqueOrderIDService.save(uniqueOrderIDDTO);
        return ResponseEntity.created(new URI("/api/unique-order-ids/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /unique-order-ids} : Updates an existing uniqueOrderID.
     *
     * @param uniqueOrderIDDTO the uniqueOrderIDDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uniqueOrderIDDTO,
     * or with status {@code 400 (Bad Request)} if the uniqueOrderIDDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uniqueOrderIDDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/unique-order-ids")
    public ResponseEntity<UniqueOrderIDDTO> updateUniqueOrderID(@RequestBody UniqueOrderIDDTO uniqueOrderIDDTO) throws URISyntaxException {
        log.debug("REST request to update UniqueOrderID : {}", uniqueOrderIDDTO);
        if (uniqueOrderIDDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UniqueOrderIDDTO result = uniqueOrderIDService.save(uniqueOrderIDDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uniqueOrderIDDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /unique-order-ids} : get all the uniqueOrderIDS.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of uniqueOrderIDS in body.
     */
    @GetMapping("/unique-order-ids")
    public ResponseEntity<List<UniqueOrderIDDTO>> getAllUniqueOrderIDS(Pageable pageable) {
        log.debug("REST request to get a page of UniqueOrderIDS");
        Page<UniqueOrderIDDTO> page = uniqueOrderIDService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /unique-order-ids/:id} : get the "id" uniqueOrderID.
     *
     * @param id the id of the uniqueOrderIDDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uniqueOrderIDDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/unique-order-ids/{id}")
    public ResponseEntity<UniqueOrderIDDTO> getUniqueOrderID(@PathVariable Long id) {
        log.debug("REST request to get UniqueOrderID : {}", id);
        Optional<UniqueOrderIDDTO> uniqueOrderIDDTO = uniqueOrderIDService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uniqueOrderIDDTO);
    }

    /**
     * {@code DELETE  /unique-order-ids/:id} : delete the "id" uniqueOrderID.
     *
     * @param id the id of the uniqueOrderIDDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/unique-order-ids/{id}")
    public ResponseEntity<Void> deleteUniqueOrderID(@PathVariable Long id) {
        log.debug("REST request to delete UniqueOrderID : {}", id);
        uniqueOrderIDService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/unique-order-ids?query=:query} : search for the uniqueOrderID corresponding
     * to the query.
     *
     * @param query the query of the uniqueOrderID search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/unique-order-ids")
    public ResponseEntity<List<UniqueOrderIDDTO>> searchUniqueOrderIDS(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UniqueOrderIDS for query {}", query);
        Page<UniqueOrderIDDTO> page = uniqueOrderIDService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
