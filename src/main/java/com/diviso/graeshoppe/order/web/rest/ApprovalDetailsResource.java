package com.diviso.graeshoppe.order.web.rest;
import com.diviso.graeshoppe.order.resource.assembler.CommandResource;
import com.diviso.graeshoppe.order.service.ApprovalDetailsService;
import com.diviso.graeshoppe.order.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import com.diviso.graeshoppe.order.service.dto.ApprovalDetailsDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ApprovalDetails.
 */
@RestController
@RequestMapping("/api")
public class ApprovalDetailsResource {

    private final Logger log = LoggerFactory.getLogger(ApprovalDetailsResource.class);
    @Value("${jhipster.clientApp.name}")
    private String applicationName;
    private static final String ENTITY_NAME = "orderApprovalDetails";

    private final ApprovalDetailsService approvalDetailsService;

    public ApprovalDetailsResource(ApprovalDetailsService approvalDetailsService) {
        this.approvalDetailsService = approvalDetailsService;
    }

    /**
     * POST  /approval-details : Create a new approvalDetails.
     *
     * @param approvalDetailsDTO the approvalDetailsDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new approvalDetailsDTO, or with status 400 (Bad Request) if the approvalDetails has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/approval-details/{taskId}")
    public ResponseEntity<CommandResource> createApprovalDetails(@RequestBody ApprovalDetailsDTO approvalDetailsDTO,@PathVariable String taskId) throws URISyntaxException {
    	log.debug("REST request to save ApprovalDetails : {}", approvalDetailsDTO);
        if (approvalDetailsDTO.getId() != null) {
            throw new BadRequestAlertException("A new approvalDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CommandResource result = approvalDetailsService.save(approvalDetailsDTO,taskId );
        return ResponseEntity.created(new URI("/api/approval-details/" + result.getSelfId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getSelfId().toString()))
            .body(result);
    }

    /**
     * PUT  /approval-details : Updates an existing approvalDetails.
     *
     * @param approvalDetailsDTO the approvalDetailsDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated approvalDetailsDTO,
     * or with status 400 (Bad Request) if the approvalDetailsDTO is not valid,
     * or with status 500 (Internal Server Error) if the approvalDetailsDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/approval-details")
    public ResponseEntity<ApprovalDetailsDTO> updateApprovalDetails(@RequestBody ApprovalDetailsDTO approvalDetailsDTO) throws URISyntaxException {
        log.debug("REST request to update ApprovalDetails : {}", approvalDetailsDTO);
        if (approvalDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ApprovalDetailsDTO result = approvalDetailsService.update(approvalDetailsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true,ENTITY_NAME, approvalDetailsDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /approval-details : get all the approvalDetails.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of approvalDetails in body
     */
    @GetMapping("/approval-details")
    public ResponseEntity<List<ApprovalDetailsDTO>> getAllApprovalDetails(Pageable pageable) {
        log.debug("REST request to get a page of ApprovalDetails");
        Page<ApprovalDetailsDTO> page = approvalDetailsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /approval-details/:id : get the "id" approvalDetails.
     *
     * @param id the id of the approvalDetailsDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the approvalDetailsDTO, or with status 404 (Not Found)
     */
    @GetMapping("/approval-details/{id}")
    public ResponseEntity<ApprovalDetailsDTO> getApprovalDetails(@PathVariable Long id) {
        log.debug("REST request to get ApprovalDetails : {}", id);
        Optional<ApprovalDetailsDTO> approvalDetailsDTO = approvalDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(approvalDetailsDTO);
    }

    /**
     * DELETE  /approval-details/:id : delete the "id" approvalDetails.
     *
     * @param id the id of the approvalDetailsDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/approval-details/{id}")
    public ResponseEntity<Void> deleteApprovalDetails(@PathVariable Long id) {
        log.debug("REST request to delete ApprovalDetails : {}", id);
        approvalDetailsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true,ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/approval-details?query=:query : search for the approvalDetails corresponding
     * to the query.
     *
     * @param query the query of the approvalDetails search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/approval-details")
    public ResponseEntity<List<ApprovalDetailsDTO>> searchApprovalDetails(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ApprovalDetails for query {}", query);
        Page<ApprovalDetailsDTO> page = approvalDetailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
