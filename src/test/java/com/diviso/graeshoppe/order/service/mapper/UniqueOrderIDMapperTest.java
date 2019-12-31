package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class UniqueOrderIDMapperTest {

    private UniqueOrderIDMapper uniqueOrderIDMapper;

    @BeforeEach
    public void setUp() {
        uniqueOrderIDMapper = new UniqueOrderIDMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(uniqueOrderIDMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(uniqueOrderIDMapper.fromId(null)).isNull();
    }
}
