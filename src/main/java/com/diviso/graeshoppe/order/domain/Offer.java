package com.diviso.graeshoppe.order.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Offer.
 */
@Entity
@Table(name = "offer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "offerline")
public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "offer_ref")
    private String offerRef;

    @Column(name = "order_discount_amount")
    private Double orderDiscountAmount;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties("appliedOffers")
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfferRef() {
        return offerRef;
    }

    public Offer offerRef(String offerRef) {
        this.offerRef = offerRef;
        return this;
    }

    public void setOfferRef(String offerRef) {
        this.offerRef = offerRef;
    }

    public Double getOrderDiscountAmount() {
        return orderDiscountAmount;
    }

    public Offer orderDiscountAmount(Double orderDiscountAmount) {
        this.orderDiscountAmount = orderDiscountAmount;
        return this;
    }

    public void setOrderDiscountAmount(Double orderDiscountAmount) {
        this.orderDiscountAmount = orderDiscountAmount;
    }

    public String getDescription() {
        return description;
    }

    public Offer description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Order getOrder() {
        return order;
    }

    public Offer order(Order order) {
        this.order = order;
        return this;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offer)) {
            return false;
        }
        return id != null && id.equals(((Offer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Offer{" +
            "id=" + getId() +
            ", offerRef='" + getOfferRef() + "'" +
            ", orderDiscountAmount=" + getOrderDiscountAmount() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
