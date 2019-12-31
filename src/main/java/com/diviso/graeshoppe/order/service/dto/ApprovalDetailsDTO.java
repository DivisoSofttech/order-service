package com.diviso.graeshoppe.order.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.diviso.graeshoppe.order.domain.ApprovalDetails} entity.
 */
public class ApprovalDetailsDTO implements Serializable {

    private Long id;

    private Instant acceptedAt;

    private Instant expectedDelivery;

    private String decision;

    private String orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Instant getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(Instant expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApprovalDetailsDTO approvalDetailsDTO = (ApprovalDetailsDTO) o;
        if (approvalDetailsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), approvalDetailsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ApprovalDetailsDTO{" +
            "id=" + getId() +
            ", acceptedAt='" + getAcceptedAt() + "'" +
            ", expectedDelivery='" + getExpectedDelivery() + "'" +
            ", decision='" + getDecision() + "'" +
            "}";
    }

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
