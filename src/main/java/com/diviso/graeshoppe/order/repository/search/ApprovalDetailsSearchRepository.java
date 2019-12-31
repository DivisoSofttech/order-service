package com.diviso.graeshoppe.order.repository.search;

import com.diviso.graeshoppe.order.domain.ApprovalDetails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ApprovalDetails} entity.
 */
public interface ApprovalDetailsSearchRepository extends ElasticsearchRepository<ApprovalDetails, Long> {
}
