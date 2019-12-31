package com.diviso.graeshoppe.order.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UniqueOrderIDSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UniqueOrderIDSearchRepositoryMockConfiguration {

    @MockBean
    private UniqueOrderIDSearchRepository mockUniqueOrderIDSearchRepository;

}
