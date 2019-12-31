package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class OrderLineMapperTest {

    private OrderLineMapper orderLineMapper;

    @BeforeEach
    public void setUp() {
        orderLineMapper = new OrderLineMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(orderLineMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(orderLineMapper.fromId(null)).isNull();
    }
}
