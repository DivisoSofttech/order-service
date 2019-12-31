package com.diviso.graeshoppe.order.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class DeliveryInfoDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeliveryInfoDTO.class);
        DeliveryInfoDTO deliveryInfoDTO1 = new DeliveryInfoDTO();
        deliveryInfoDTO1.setId(1L);
        DeliveryInfoDTO deliveryInfoDTO2 = new DeliveryInfoDTO();
        assertThat(deliveryInfoDTO1).isNotEqualTo(deliveryInfoDTO2);
        deliveryInfoDTO2.setId(deliveryInfoDTO1.getId());
        assertThat(deliveryInfoDTO1).isEqualTo(deliveryInfoDTO2);
        deliveryInfoDTO2.setId(2L);
        assertThat(deliveryInfoDTO1).isNotEqualTo(deliveryInfoDTO2);
        deliveryInfoDTO1.setId(null);
        assertThat(deliveryInfoDTO1).isNotEqualTo(deliveryInfoDTO2);
    }
}
