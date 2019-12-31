package com.diviso.graeshoppe.order.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.diviso.graeshoppe.order.web.rest.TestUtil;

public class ApprovalDetailsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApprovalDetails.class);
        ApprovalDetails approvalDetails1 = new ApprovalDetails();
        approvalDetails1.setId(1L);
        ApprovalDetails approvalDetails2 = new ApprovalDetails();
        approvalDetails2.setId(approvalDetails1.getId());
        assertThat(approvalDetails1).isEqualTo(approvalDetails2);
        approvalDetails2.setId(2L);
        assertThat(approvalDetails1).isNotEqualTo(approvalDetails2);
        approvalDetails1.setId(null);
        assertThat(approvalDetails1).isNotEqualTo(approvalDetails2);
    }
}
