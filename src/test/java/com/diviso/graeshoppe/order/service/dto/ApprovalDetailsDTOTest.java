package com.diviso.graeshoppe.order.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class ApprovalDetailsDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalDetailsDTO.class);
        ApprovalDetailsDTO approvalDetailsDTO1 = new ApprovalDetailsDTO();
        approvalDetailsDTO1.setId(1L);
        ApprovalDetailsDTO approvalDetailsDTO2 = new ApprovalDetailsDTO();
        assertThat(approvalDetailsDTO1).isNotEqualTo(approvalDetailsDTO2);
        approvalDetailsDTO2.setId(approvalDetailsDTO1.getId());
        assertThat(approvalDetailsDTO1).isEqualTo(approvalDetailsDTO2);
        approvalDetailsDTO2.setId(2L);
        assertThat(approvalDetailsDTO1).isNotEqualTo(approvalDetailsDTO2);
        approvalDetailsDTO1.setId(null);
        assertThat(approvalDetailsDTO1).isNotEqualTo(approvalDetailsDTO2);
    }
}
