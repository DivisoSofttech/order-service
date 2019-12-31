package com.diviso.graeshoppe.order.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.diviso.graeshoppe.order.domain.AuxilaryOrderLine} entity.
 */
public class AuxilaryOrderLineDTO implements Serializable {

    private Long id;

    private Long productId;

    private Integer quantity;

    private Double pricePerUnit;

    private Double total;


    private Long orderLineId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Long getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Long orderLineId) {
        this.orderLineId = orderLineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuxilaryOrderLineDTO auxilaryOrderLineDTO = (AuxilaryOrderLineDTO) o;
        if (auxilaryOrderLineDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), auxilaryOrderLineDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AuxilaryOrderLineDTO{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", quantity=" + getQuantity() +
            ", pricePerUnit=" + getPricePerUnit() +
            ", total=" + getTotal() +
            ", orderLineId=" + getOrderLineId() +
            "}";
    }
}
