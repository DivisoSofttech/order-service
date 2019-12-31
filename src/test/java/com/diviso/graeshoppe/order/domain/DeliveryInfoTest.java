package com.diviso.graeshoppe.order.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class DeliveryInfoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryInfo.class);
        DeliveryInfo deliveryInfo1 = new DeliveryInfo();
        deliveryInfo1.setId(1L);
        DeliveryInfo deliveryInfo2 = new DeliveryInfo();
        deliveryInfo2.setId(deliveryInfo1.getId());
        assertThat(deliveryInfo1).isEqualTo(deliveryInfo2);
        deliveryInfo2.setId(2L);
        assertThat(deliveryInfo1).isNotEqualTo(deliveryInfo2);
        deliveryInfo1.setId(null);
        assertThat(deliveryInfo1).isNotEqualTo(deliveryInfo2);
    }
}
