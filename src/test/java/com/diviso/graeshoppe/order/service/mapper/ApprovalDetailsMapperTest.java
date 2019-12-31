package com.diviso.graeshoppe.order.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class ApprovalDetailsMapperTest {

    private ApprovalDetailsMapper approvalDetailsMapper;

    @BeforeEach
    public void setUp() {
        approvalDetailsMapper = new ApprovalDetailsMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(approvalDetailsMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(approvalDetailsMapper.fromId(null)).isNull();
    }
}
