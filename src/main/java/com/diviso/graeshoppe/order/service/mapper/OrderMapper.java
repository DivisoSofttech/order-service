package com.diviso.graeshoppe.order.service.mapper;

import com.diviso.graeshoppe.order.domain.*;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring", uses = {DeliveryInfoMapper.class, ApprovalDetailsMapper.class, StatusMapper.class})
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {

    @Mapping(source = "deliveryInfo.id", target = "deliveryInfoId")
    @Mapping(source = "approvalDetails.id", target = "approvalDetailsId")
    @Mapping(source = "status.id", target = "statusId")
    OrderDTO toDto(Order order);

    @Mapping(source = "deliveryInfoId", target = "deliveryInfo")
    @Mapping(source = "approvalDetailsId", target = "approvalDetails")
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "removeOrderLines", ignore = true)
    @Mapping(target = "appliedOffers", ignore = true)
    @Mapping(target = "removeAppliedOffers", ignore = true)
    @Mapping(source = "statusId", target = "status")
    Order toEntity(OrderDTO orderDTO);

    default Order fromId(Long id) {
        if (id == null) {
            return null;
        }
        Order order = new Order();
        order.setId(id);
        return order;
    }
}
