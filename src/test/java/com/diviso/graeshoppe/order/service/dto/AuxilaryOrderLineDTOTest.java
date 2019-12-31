package com.diviso.graeshoppe.order.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class AuxilaryOrderLineDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuxilaryOrderLineDTO.class);
        AuxilaryOrderLineDTO auxilaryOrderLineDTO1 = new AuxilaryOrderLineDTO();
        auxilaryOrderLineDTO1.setId(1L);
        AuxilaryOrderLineDTO auxilaryOrderLineDTO2 = new AuxilaryOrderLineDTO();
        assertThat(auxilaryOrderLineDTO1).isNotEqualTo(auxilaryOrderLineDTO2);
        auxilaryOrderLineDTO2.setId(auxilaryOrderLineDTO1.getId());
        assertThat(auxilaryOrderLineDTO1).isEqualTo(auxilaryOrderLineDTO2);
        auxilaryOrderLineDTO2.setId(2L);
        assertThat(auxilaryOrderLineDTO1).isNotEqualTo(auxilaryOrderLineDTO2);
        auxilaryOrderLineDTO1.setId(null);
        assertThat(auxilaryOrderLineDTO1).isNotEqualTo(auxilaryOrderLineDTO2);
    }
}
