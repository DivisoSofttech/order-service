package com.diviso.graeshoppe.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diviso.graeshoppe.order.domain.DeliveryInfo;
import com.diviso.graeshoppe.order.repository.OrderRepository;
import com.diviso.graeshoppe.order.service.OrderQueryService;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;
import com.diviso.graeshoppe.order.service.mapper.OrderMapper;

@Service
public class OrderQueryServiceImpl implements OrderQueryService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderMapper orderMapper;

	@Override
	public OrderDTO findByOrderId(String orderId) {

		return orderMapper.toDto(orderRepository.findByOrderId(orderId).get());
	}

	@Override
	public Long countByCustomerIdAndStatusName(String customerId, String statusName) {
	
		return orderRepository.countByCustomerIdAndStatus_Name(customerId, statusName);
	}

	@Override
	public OrderDTO findByDeliveryInfoId(Long id) {
		return orderRepository.findByDeliveryInfo_Id(id).map(orderMapper::toDto).get();
	}

	@Override
	public long countByStoreIdAndCustomerId(String storeId, String customerId) {
		return orderRepository.countByStoreIdAndCustomerId(storeId, customerId);
	}

	@Override
	public DeliveryInfo findDeliveryInfoByOrderId(String orderId) {
		
		return orderRepository.findDeliveryInfoByOrderId(orderId);
	}

	
}
