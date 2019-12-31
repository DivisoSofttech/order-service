package com.diviso.graeshoppe.order.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.diviso.graeshoppe.order.domain.DeliveryInfo} entity.
 */
public class DeliveryInfoDTO implements Serializable {

    private Long id;

    private String deliveryType;

    private Double deliveryCharge;

    private String deliveryNotes;


    private Long deliveryAddressId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long addressId) {
        this.deliveryAddressId = addressId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeliveryInfoDTO deliveryInfoDTO = (DeliveryInfoDTO) o;
        if (deliveryInfoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), deliveryInfoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DeliveryInfoDTO{" +
            "id=" + getId() +
            ", deliveryType='" + getDeliveryType() + "'" +
            ", deliveryCharge=" + getDeliveryCharge() +
            ", deliveryNotes='" + getDeliveryNotes() + "'" +
            ", deliveryAddressId=" + getDeliveryAddressId() +
            "}";
    }
}
