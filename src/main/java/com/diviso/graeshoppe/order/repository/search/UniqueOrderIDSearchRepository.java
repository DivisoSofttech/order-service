package com.diviso.graeshoppe.order.repository.search;

import com.diviso.graeshoppe.order.domain.UniqueOrderID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UniqueOrderID} entity.
 */
public interface UniqueOrderIDSearchRepository extends ElasticsearchRepository<UniqueOrderID, Long> {
}
