package com.diviso.graeshoppe.order.service.impl;

import com.diviso.graeshoppe.order.service.OfferService;
import com.diviso.graeshoppe.order.domain.Offer;
import com.diviso.graeshoppe.order.repository.OfferRepository;
import com.diviso.graeshoppe.order.repository.search.OfferSearchRepository;
import com.diviso.graeshoppe.order.service.dto.OfferDTO;
import com.diviso.graeshoppe.order.service.mapper.OfferMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Offer}.
 */
@Service
@Transactional
public class OfferServiceImpl implements OfferService {

    private final Logger log = LoggerFactory.getLogger(OfferServiceImpl.class);

    private final OfferRepository offerRepository;

    private final OfferMapper offerMapper;

    private final OfferSearchRepository offerSearchRepository;

    public OfferServiceImpl(OfferRepository offerRepository, OfferMapper offerMapper, OfferSearchRepository offerSearchRepository) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
        this.offerSearchRepository = offerSearchRepository;
    }

    /**
     * Save a offer.
     *
     * @param offerDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public OfferDTO save(OfferDTO offerDTO) {
        log.debug("Request to save Offer : {}", offerDTO);
        Offer offer = offerMapper.toEntity(offerDTO);
        offer = offerRepository.save(offer);
        OfferDTO result = offerMapper.toDto(offer);
        offerSearchRepository.save(offer);
        return updateToEs(result);
    }
    
    private OfferDTO updateToEs(OfferDTO offerDTO) {
        log.debug("Request to save Offer : {}", offerDTO);
        Offer offer = offerMapper.toEntity(offerDTO);
        offer = offerRepository.save(offer);
        OfferDTO result = offerMapper.toDto(offer);
        offerSearchRepository.save(offer);
        return result;
    }
    

    /**
     * Get all the offers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OfferDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Offers");
        return offerRepository.findAll(pageable)
            .map(offerMapper::toDto);
    }


    /**
     * Get one offer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OfferDTO> findOne(Long id) {
        log.debug("Request to get Offer : {}", id);
        return offerRepository.findById(id)
            .map(offerMapper::toDto);
    }

    /**
     * Delete the offer by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Offer : {}", id);
        offerRepository.deleteById(id);
        offerSearchRepository.deleteById(id);
    }

    /**
     * Search for the offer corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OfferDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Offers for query {}", query);
        return offerSearchRepository.search(queryStringQuery(query), pageable)
            .map(offerMapper::toDto);
    }
}
