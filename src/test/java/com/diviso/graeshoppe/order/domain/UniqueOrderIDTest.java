package com.diviso.graeshoppe.order.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class UniqueOrderIDTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UniqueOrderID.class);
        UniqueOrderID uniqueOrderID1 = new UniqueOrderID();
        uniqueOrderID1.setId(1L);
        UniqueOrderID uniqueOrderID2 = new UniqueOrderID();
        uniqueOrderID2.setId(uniqueOrderID1.getId());
        assertThat(uniqueOrderID1).isEqualTo(uniqueOrderID2);
        uniqueOrderID2.setId(2L);
        assertThat(uniqueOrderID1).isNotEqualTo(uniqueOrderID2);
        uniqueOrderID1.setId(null);
        assertThat(uniqueOrderID1).isNotEqualTo(uniqueOrderID2);
    }
}
