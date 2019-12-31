package com.diviso.graeshoppe.order.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A OrderLine.
 */
@Entity
@Table(name = "order_line")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orderline")
public class OrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @Column(name = "total")
    private Double total;

    @OneToMany(mappedBy = "orderLine")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<AuxilaryOrderLine> requiedAuxilaries = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("orderLines")
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public OrderLine productId(Long productId) {
        this.productId = productId;
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderLine quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public OrderLine pricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getTotal() {
        return total;
    }

    public OrderLine total(Double total) {
        this.total = total;
        return this;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Set<AuxilaryOrderLine> getRequiedAuxilaries() {
        return requiedAuxilaries;
    }

    public OrderLine requiedAuxilaries(Set<AuxilaryOrderLine> auxilaryOrderLines) {
        this.requiedAuxilaries = auxilaryOrderLines;
        return this;
    }

    public OrderLine addRequiedAuxilaries(AuxilaryOrderLine auxilaryOrderLine) {
        this.requiedAuxilaries.add(auxilaryOrderLine);
        auxilaryOrderLine.setOrderLine(this);
        return this;
    }

    public OrderLine removeRequiedAuxilaries(AuxilaryOrderLine auxilaryOrderLine) {
        this.requiedAuxilaries.remove(auxilaryOrderLine);
        auxilaryOrderLine.setOrderLine(null);
        return this;
    }

    public void setRequiedAuxilaries(Set<AuxilaryOrderLine> auxilaryOrderLines) {
        this.requiedAuxilaries = auxilaryOrderLines;
    }

    public Order getOrder() {
        return order;
    }

    public OrderLine order(Order order) {
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
        if (!(o instanceof OrderLine)) {
            return false;
        }
        return id != null && id.equals(((OrderLine) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", quantity=" + getQuantity() +
            ", pricePerUnit=" + getPricePerUnit() +
            ", total=" + getTotal() +
            "}";
    }
}
