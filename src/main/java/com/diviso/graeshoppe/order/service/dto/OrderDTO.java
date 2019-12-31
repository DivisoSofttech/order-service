package com.diviso.graeshoppe.order.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.diviso.graeshoppe.order.domain.Order} entity.
 */
public class OrderDTO implements Serializable {

    private Long id;

    private String orderId;

    private String customerId;

    private String storeId;

    private Instant date;

    private Double grandTotal;

    private Double subTotal;

    private String paymentRef;

    private String paymentMode;

    private String allergyNote;

    private Instant preOrderDate;

    private String email;

    private String timeZone;


    private Long deliveryInfoId;

    private Long approvalDetailsId;

    private Long statusId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getAllergyNote() {
        return allergyNote;
    }

    public void setAllergyNote(String allergyNote) {
        this.allergyNote = allergyNote;
    }

    public Instant getPreOrderDate() {
        return preOrderDate;
    }

    public void setPreOrderDate(Instant preOrderDate) {
        this.preOrderDate = preOrderDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Long getDeliveryInfoId() {
        return deliveryInfoId;
    }

    public void setDeliveryInfoId(Long deliveryInfoId) {
        this.deliveryInfoId = deliveryInfoId;
    }

    public Long getApprovalDetailsId() {
        return approvalDetailsId;
    }

    public void setApprovalDetailsId(Long approvalDetailsId) {
        this.approvalDetailsId = approvalDetailsId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (orderDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orderDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", orderId='" + getOrderId() + "'" +
            ", customerId='" + getCustomerId() + "'" +
            ", storeId='" + getStoreId() + "'" +
            ", date='" + getDate() + "'" +
            ", grandTotal=" + getGrandTotal() +
            ", subTotal=" + getSubTotal() +
            ", paymentRef='" + getPaymentRef() + "'" +
            ", paymentMode='" + getPaymentMode() + "'" +
            ", allergyNote='" + getAllergyNote() + "'" +
            ", preOrderDate='" + getPreOrderDate() + "'" +
            ", email='" + getEmail() + "'" +
            ", timeZone='" + getTimeZone() + "'" +
            ", deliveryInfoId=" + getDeliveryInfoId() +
            ", approvalDetailsId=" + getApprovalDetailsId() +
            ", statusId=" + getStatusId() +
            "}";
    }
}
