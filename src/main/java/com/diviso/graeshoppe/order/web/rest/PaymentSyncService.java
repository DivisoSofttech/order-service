package com.diviso.graeshoppe.order.web.rest;

import java.util.Optional;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.diviso.graeshoppe.order.client.customer.api.CustomerResourceApi;
import com.diviso.graeshoppe.order.client.customer.model.Customer;
import com.diviso.graeshoppe.order.config.SinkConfiguration;
import com.diviso.graeshoppe.order.domain.Address;
import com.diviso.graeshoppe.order.domain.DeliveryInfo;
import com.diviso.graeshoppe.order.service.DeliveryInfoService;
import com.diviso.graeshoppe.order.service.OrderCommandService;
import com.diviso.graeshoppe.order.service.dto.AddressDTO;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;
import com.diviso.graeshoppe.payment.avro.Payment;
import com.esotericsoftware.minlog.Log;

@EnableBinding(SinkConfiguration.class)
public class PaymentSyncService {
	private final Logger LOG = LoggerFactory.getLogger(PaymentSyncService.class);

	@Autowired
	private OrderCommandService orderService;
	@Autowired
	private CustomerResourceApi customerResourceApi;
	@Autowired
	private DeliveryInfoService deliveryInfoService;
	@StreamListener(SinkConfiguration.PAYMENT)
	public void listenToPayment(KStream<String, Payment> message) {
		message.foreach((key,value) -> {
			LOG.info("message consumed payment "+value);
			Optional<OrderDTO> orderDTO = orderService.findByOrderID(value.getTargetId());
			if(orderDTO.isPresent()) {
				orderDTO.get().setPaymentMode(value.getPaymentType().toUpperCase());
				orderDTO.get().setPaymentRef(value.getId().toString());
				orderDTO.get().setStatusId(4l);
				orderService.update(orderDTO.get());
				LOG.info("Order updated with payment ref");
				Customer customer = customerResourceApi.findByReferenceUsingGET(orderDTO.get().getCustomerId()).getBody();
				Long phone = customer.getContact().getMobileNumber();
				Optional<DeliveryInfo> deliveryInfo = deliveryInfoService.findByOrderId(orderDTO.get().getOrderId());
				if(deliveryInfo.isPresent()) {
					if (deliveryInfo.get().getDeliveryAddress() != null) {
						Address address =deliveryInfo.get().getDeliveryAddress();
						if(address.getPhone()!=null) {
							phone = address.getPhone();
						}
					}
				}
				LOG.info("Phone number is publishing to kafka "+ phone);
				orderService.publishMesssage(orderDTO.get().getOrderId(), phone, "CREATE"); // sending order to MOM

			}
			
		});
		
		
	}
	
}
