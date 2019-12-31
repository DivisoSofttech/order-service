package com.diviso.graeshoppe.order.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class UniqueOrderIDDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UniqueOrderIDDTO.class);
        UniqueOrderIDDTO uniqueOrderIDDTO1 = new UniqueOrderIDDTO();
        uniqueOrderIDDTO1.setId(1L);
        UniqueOrderIDDTO uniqueOrderIDDTO2 = new UniqueOrderIDDTO();
        assertThat(uniqueOrderIDDTO1).isNotEqualTo(uniqueOrderIDDTO2);
        uniqueOrderIDDTO2.setId(uniqueOrderIDDTO1.getId());
        assertThat(uniqueOrderIDDTO1).isEqualTo(uniqueOrderIDDTO2);
        uniqueOrderIDDTO2.setId(2L);
        assertThat(uniqueOrderIDDTO1).isNotEqualTo(uniqueOrderIDDTO2);
        uniqueOrderIDDTO1.setId(null);
        assertThat(uniqueOrderIDDTO1).isNotEqualTo(uniqueOrderIDDTO2);
    }
}
