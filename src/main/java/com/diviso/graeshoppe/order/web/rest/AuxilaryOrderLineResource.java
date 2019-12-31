package com.diviso.graeshoppe.order.web.rest;

import com.diviso.graeshoppe.order.service.AuxilaryOrderLineService;
import com.diviso.graeshoppe.order.web.rest.errors.BadRequestAlertException;
import com.diviso.graeshoppe.order.service.dto.AuxilaryOrderLineDTO;

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
 * REST controller for managing {@link com.diviso.graeshoppe.order.domain.AuxilaryOrderLine}.
 */
@RestController
@RequestMapping("/api")
public class AuxilaryOrderLineResource {

    private final Logger log = LoggerFactory.getLogger(AuxilaryOrderLineResource.class);

    private static final String ENTITY_NAME = "orderAuxilaryOrderLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuxilaryOrderLineService auxilaryOrderLineService;

    public AuxilaryOrderLineResource(AuxilaryOrderLineService auxilaryOrderLineService) {
        this.auxilaryOrderLineService = auxilaryOrderLineService;
    }

    /**
     * {@code POST  /auxilary-order-lines} : Create a new auxilaryOrderLine.
     *
     * @param auxilaryOrderLineDTO the auxilaryOrderLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auxilaryOrderLineDTO, or with status {@code 400 (Bad Request)} if the auxilaryOrderLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/auxilary-order-lines")
    public ResponseEntity<AuxilaryOrderLineDTO> createAuxilaryOrderLine(@RequestBody AuxilaryOrderLineDTO auxilaryOrderLineDTO) throws URISyntaxException {
        log.debug("REST request to save AuxilaryOrderLine : {}", auxilaryOrderLineDTO);
        if (auxilaryOrderLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new auxilaryOrderLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuxilaryOrderLineDTO result = auxilaryOrderLineService.save(auxilaryOrderLineDTO);
        return ResponseEntity.created(new URI("/api/auxilary-order-lines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /auxilary-order-lines} : Updates an existing auxilaryOrderLine.
     *
     * @param auxilaryOrderLineDTO the auxilaryOrderLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auxilaryOrderLineDTO,
     * or with status {@code 400 (Bad Request)} if the auxilaryOrderLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auxilaryOrderLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/auxilary-order-lines")
    public ResponseEntity<AuxilaryOrderLineDTO> updateAuxilaryOrderLine(@RequestBody AuxilaryOrderLineDTO auxilaryOrderLineDTO) throws URISyntaxException {
        log.debug("REST request to update AuxilaryOrderLine : {}", auxilaryOrderLineDTO);
        if (auxilaryOrderLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AuxilaryOrderLineDTO result = auxilaryOrderLineService.save(auxilaryOrderLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, auxilaryOrderLineDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /auxilary-order-lines} : get all the auxilaryOrderLines.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auxilaryOrderLines in body.
     */
    @GetMapping("/auxilary-order-lines")
    public ResponseEntity<List<AuxilaryOrderLineDTO>> getAllAuxilaryOrderLines(Pageable pageable) {
        log.debug("REST request to get a page of AuxilaryOrderLines");
        Page<AuxilaryOrderLineDTO> page = auxilaryOrderLineService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /auxilary-order-lines/:id} : get the "id" auxilaryOrderLine.
     *
     * @param id the id of the auxilaryOrderLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auxilaryOrderLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/auxilary-order-lines/{id}")
    public ResponseEntity<AuxilaryOrderLineDTO> getAuxilaryOrderLine(@PathVariable Long id) {
        log.debug("REST request to get AuxilaryOrderLine : {}", id);
        Optional<AuxilaryOrderLineDTO> auxilaryOrderLineDTO = auxilaryOrderLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auxilaryOrderLineDTO);
    }

    /**
     * {@code DELETE  /auxilary-order-lines/:id} : delete the "id" auxilaryOrderLine.
     *
     * @param id the id of the auxilaryOrderLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/auxilary-order-lines/{id}")
    public ResponseEntity<Void> deleteAuxilaryOrderLine(@PathVariable Long id) {
        log.debug("REST request to delete AuxilaryOrderLine : {}", id);
        auxilaryOrderLineService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/auxilary-order-lines?query=:query} : search for the auxilaryOrderLine corresponding
     * to the query.
     *
     * @param query the query of the auxilaryOrderLine search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/auxilary-order-lines")
    public ResponseEntity<List<AuxilaryOrderLineDTO>> searchAuxilaryOrderLines(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of AuxilaryOrderLines for query {}", query);
        Page<AuxilaryOrderLineDTO> page = auxilaryOrderLineService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
