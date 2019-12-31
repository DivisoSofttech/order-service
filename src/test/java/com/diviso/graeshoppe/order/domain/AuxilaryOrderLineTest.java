package com.diviso.graeshoppe.order.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class AuxilaryOrderLineTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuxilaryOrderLine.class);
        AuxilaryOrderLine auxilaryOrderLine1 = new AuxilaryOrderLine();
        auxilaryOrderLine1.setId(1L);
        AuxilaryOrderLine auxilaryOrderLine2 = new AuxilaryOrderLine();
        auxilaryOrderLine2.setId(auxilaryOrderLine1.getId());
        assertThat(auxilaryOrderLine1).isEqualTo(auxilaryOrderLine2);
        auxilaryOrderLine2.setId(2L);
        assertThat(auxilaryOrderLine1).isNotEqualTo(auxilaryOrderLine2);
        auxilaryOrderLine1.setId(null);
        assertThat(auxilaryOrderLine1).isNotEqualTo(auxilaryOrderLine2);
    }
}
