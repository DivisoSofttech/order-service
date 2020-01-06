package com.diviso.graeshoppe.order.web.rest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diviso.graeshoppe.order.client.bpmn.api.HistoryApi;
import com.diviso.graeshoppe.order.client.bpmn.api.TasksApi;
import com.diviso.graeshoppe.order.client.bpmn.model.DataResponse;
import com.diviso.graeshoppe.order.models.OpenTask;
import com.diviso.graeshoppe.order.service.OrderQueryService;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")

public class OrderQueryResource {



	
	@Autowired
	private OrderQueryService orderQueryService;
	
	
	@GetMapping("/taskDetails/{taskName}/{orderId}/{storeId}/{processId}")
	public OpenTask getTaskDetails(@PathVariable String taskName,@PathVariable String orderId,@PathVariable String storeId,String processId) {
		
		return orderQueryService.getOpenTask(taskName,orderId,storeId,processId);
		
		
	}

	
	@GetMapping("/count-by-customerid-statusname/{customerId}/{statusName}")
	public ResponseEntity<Long> countByCustomerIdAndStatusName(@PathVariable String customerId,@PathVariable String statusName) {
		return new ResponseEntity<Long>(orderQueryService.countByCustomerIdAndStatusName(customerId,statusName), HttpStatus.OK);
	}
	
	@GetMapping("/findByDeliveryInfoId/{id}")
	public ResponseEntity<OrderDTO> findByDeliveryInfoId(@PathVariable Long id){
		return new ResponseEntity<OrderDTO>(orderQueryService.findByDeliveryInfoId(id), HttpStatus.OK);
	}
	
	@GetMapping("/count-by-storeid-customerid/{storeId}/{customerId}")
	public ResponseEntity<Long> countByStoreIdAndCustomerId(@PathVariable String storeId,@PathVariable String customerId) {
		return new ResponseEntity<Long>(orderQueryService.countByStoreIdAndCustomerId(storeId,customerId),HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	

}
