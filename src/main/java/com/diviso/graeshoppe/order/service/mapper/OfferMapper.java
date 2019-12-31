package com.diviso.graeshoppe.order.service.mapper;

import com.diviso.graeshoppe.order.domain.*;
import com.diviso.graeshoppe.order.service.dto.OfferDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Offer} and its DTO {@link OfferDTO}.
 */
@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface OfferMapper extends EntityMapper<OfferDTO, Offer> {

    @Override
	@Mapping(source = "order.id", target = "orderId")
    OfferDTO toDto(Offer offer);

    @Override
	@Mapping(source = "orderId", target = "order")
    Offer toEntity(OfferDTO offerDTO);

    default Offer fromId(Long id) {
        if (id == null) {
            return null;
        }
        Offer offer = new Offer();
        offer.setId(id);
        return offer;
    }
}
