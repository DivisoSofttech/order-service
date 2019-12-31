package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class OfferMapperTest {

    private OfferMapper offerMapper;

    @BeforeEach
    public void setUp() {
        offerMapper = new OfferMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(offerMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(offerMapper.fromId(null)).isNull();
    }
}
