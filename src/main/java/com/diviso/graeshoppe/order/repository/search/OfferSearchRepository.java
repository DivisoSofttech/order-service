package com.diviso.graeshoppe.order.repository.search;

import com.diviso.graeshoppe.order.domain.Offer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Offer} entity.
 */
public interface OfferSearchRepository extends ElasticsearchRepository<Offer, Long> {
}
