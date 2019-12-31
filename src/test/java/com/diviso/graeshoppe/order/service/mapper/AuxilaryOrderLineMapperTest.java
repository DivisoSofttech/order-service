package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class AuxilaryOrderLineMapperTest {

    private AuxilaryOrderLineMapper auxilaryOrderLineMapper;

    @BeforeEach
    public void setUp() {
        auxilaryOrderLineMapper = new AuxilaryOrderLineMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(auxilaryOrderLineMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(auxilaryOrderLineMapper.fromId(null)).isNull();
    }
}
