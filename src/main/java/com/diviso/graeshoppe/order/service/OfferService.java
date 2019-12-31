package com.diviso.graeshoppe.order.service;

import com.diviso.graeshoppe.order.service.dto.OfferDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.diviso.graeshoppe.order.domain.Offer}.
 */
public interface OfferService {

    /**
     * Save a offer.
     *
     * @param offerDTO the entity to save.
     * @return the persisted entity.
     */
    OfferDTO save(OfferDTO offerDTO);

    /**
     * Get all the offers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OfferDTO> findAll(Pageable pageable);


    /**
     * Get the "id" offer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OfferDTO> findOne(Long id);

    /**
     * Delete the "id" offer.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the offer corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OfferDTO> search(String query, Pageable pageable);
}
