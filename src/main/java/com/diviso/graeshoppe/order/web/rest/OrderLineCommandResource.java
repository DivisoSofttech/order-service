package com.diviso.graeshoppe.order.web.rest;
import com.diviso.graeshoppe.order.service.OrderLineService;
import com.diviso.graeshoppe.order.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import com.diviso.graeshoppe.order.service.dto.OrderLineDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OrderLine.
 */
@RestController
@RequestMapping("/api")
public class OrderLineCommandResource {

    private final Logger log = LoggerFactory.getLogger(OrderLineCommandResource.class);

    private static final String ENTITY_NAME = "orderOrderLine";

    private final OrderLineService orderLineService;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public OrderLineCommandResource(OrderLineService orderLineService) {
        this.orderLineService = orderLineService;
    }

    /**
     * POST  /order-lines : Create a new orderLine.
     *
     * @param orderLineDTO the orderLineDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orderLineDTO, or with status 400 (Bad Request) if the orderLine has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/order-lines")
    public ResponseEntity<OrderLineDTO> createOrderLine(@RequestBody OrderLineDTO orderLineDTO) throws URISyntaxException {
        log.debug("REST request to save OrderLine : {}", orderLineDTO);
        if (orderLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderLineDTO result = orderLineService.save(orderLineDTO);
        OrderLineDTO result1=orderLineService.save(result);
        return ResponseEntity.created(new URI("/api/order-lines/" + result1.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName,true,ENTITY_NAME, result1.getId().toString()))
            .body(result1);
    }

    /**
     * PUT  /order-lines : Updates an existing orderLine.
     *
     * @param orderLineDTO the orderLineDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orderLineDTO,
     * or with status 400 (Bad Request) if the orderLineDTO is not valid,
     * or with status 500 (Internal Server Error) if the orderLineDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/order-lines")
    public ResponseEntity<OrderLineDTO> updateOrderLine(@RequestBody OrderLineDTO orderLineDTO) throws URISyntaxException {
        log.debug("REST request to update OrderLine : {}", orderLineDTO);
        OrderLineDTO result = null;
        if (orderLineDTO.getId() == null) {
            //throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        	result = createOrderLine(orderLineDTO).getBody();
        }else {
             result = orderLineService.save(orderLineDTO);

        }
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /order-lines : get all the orderLines.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of orderLines in body
     */
    @GetMapping("/order-lines")
    public ResponseEntity<List<OrderLineDTO>> getAllOrderLines(Pageable pageable) {
        log.debug("REST request to get a page of OrderLines");
        Page<OrderLineDTO> page = orderLineService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /order-lines/:id : get the "id" orderLine.
     *
     * @param id the id of the orderLineDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderLineDTO, or with status 404 (Not Found)
     */
    @GetMapping("/order-lines/{id}")
    public ResponseEntity<OrderLineDTO> getOrderLine(@PathVariable Long id) {
        log.debug("REST request to get OrderLine : {}", id);
        Optional<OrderLineDTO> orderLineDTO = orderLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderLineDTO);
    }

    /**
     * DELETE  /order-lines/:id : delete the "id" orderLine.
     *
     * @param id the id of the orderLineDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/order-lines/{id}")
    public ResponseEntity<Void> deleteOrderLine(@PathVariable Long id) {
        log.debug("REST request to delete OrderLine : {}", id);
        orderLineService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName,true,ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/order-lines?query=:query : search for the orderLine corresponding
     * to the query.
     *
     * @param query the query of the orderLine search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/order-lines")
    public ResponseEntity<List<OrderLineDTO>> searchOrderLines(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of OrderLines for query {}", query);
        Page<OrderLineDTO> page = orderLineService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
    @GetMapping("/findbyorderid/{orderId}")
    public  ResponseEntity<List<OrderLineDTO>> findByOrderId(@PathVariable String orderId){
    	return new ResponseEntity<List<OrderLineDTO>>(orderLineService.findByOrderId(orderId),HttpStatus.OK);
    }

    @GetMapping("/deleteByProductIdAndOrderId/{productId}/{orderId}")
    public  ResponseEntity<List<OrderLineDTO>> deleteByProductIdAndOrderId(@PathVariable Long productId,@PathVariable Long orderId){
    	orderLineService.deleteByProductIdAndOrderId(productId,orderId);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName,true,ENTITY_NAME, productId.toString())).build();
    }
}
