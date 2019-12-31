package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class DeliveryInfoMapperTest {

    private DeliveryInfoMapper deliveryInfoMapper;

    @BeforeEach
    public void setUp() {
        deliveryInfoMapper = new DeliveryInfoMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(deliveryInfoMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(deliveryInfoMapper.fromId(null)).isNull();
    }
}
