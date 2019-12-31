package com.diviso.graeshoppe.order.service.mapper;

import com.diviso.graeshoppe.order.domain.*;
import com.diviso.graeshoppe.order.service.dto.OrderLineDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderLine} and its DTO {@link OrderLineDTO}.
 */
@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface OrderLineMapper extends EntityMapper<OrderLineDTO, OrderLine> {

    @Override
	@Mapping(source = "order.id", target = "orderId")
    OrderLineDTO toDto(OrderLine orderLine);

    @Override
	@Mapping(target = "requiedAuxilaries", ignore = true)
    @Mapping(target = "removeRequiedAuxilaries", ignore = true)
    @Mapping(source = "orderId", target = "order")
    OrderLine toEntity(OrderLineDTO orderLineDTO);

    default OrderLine fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderLine orderLine = new OrderLine();
        orderLine.setId(id);
        return orderLine;
    }
}
