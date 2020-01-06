package com.diviso.graeshoppe.order.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.diviso.graeshoppe.order.client.bpmn.api.HistoryApi;
import com.diviso.graeshoppe.order.client.bpmn.api.TasksApi;
import com.diviso.graeshoppe.order.client.bpmn.model.DataResponse;
import com.diviso.graeshoppe.order.domain.DeliveryInfo;
import com.diviso.graeshoppe.order.models.OpenTask;
import com.diviso.graeshoppe.order.repository.OrderRepository;
import com.diviso.graeshoppe.order.service.OrderQueryService;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;
import com.diviso.graeshoppe.order.service.mapper.OrderMapper;
import com.diviso.graeshoppe.order.web.rest.OrderQueryResource;

@Service
public class OrderQueryServiceImpl implements OrderQueryService {
	private final Logger log = LoggerFactory.getLogger(OrderQueryResource.class);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TasksApi tasksApi;

	@Autowired
	private HistoryApi historyApi;
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

	@Override
	public OpenTask getOpenTask(String taskName, String orderId, String storeId,String processInstanceId) {
		return getTasks(taskName, null, storeId, null, null, null, null, null, null, null,processInstanceId).stream()
				.filter(openTask -> openTask.getOrderId().equals(orderId)).findFirst().get();
	}

	public List<OpenTask> getTasks(String name, String nameLike, String assignee, String assigneeLike,
			String candidateUser, String candidateGroup, String candidateGroups, String createdOn, String createdBefore,
			String createdAfter,String processInstanceId) {

		ResponseEntity<DataResponse> response = tasksApi.getTasks(name, nameLike, null, null, null, null, assignee,
				assigneeLike, null, null, null, null, candidateUser, candidateGroup, candidateGroups, null, null, null,
				processInstanceId, null, null, null, null, null, null, null, null, createdOn, createdBefore, createdAfter, null,
				null, null, null, null, null, null, null, null, null, null, null, null,
				/* pageable.getPageNumber()+"" */"0", null, "desc", /* pageable.getPageSize()+"" */"150");
		List<LinkedHashMap<String, String>> myTasks = (List<LinkedHashMap<String, String>>) response.getBody()
				.getData();

		List<OpenTask> tasks = new ArrayList<OpenTask>();
		// Page<OpenTask> page=new PageImpl<>(tasks, pageable,
		// response.getBody().getTotal());
		myTasks.forEach(task -> {
			OpenTask openTask = new OpenTask();
			String taskProcessInstanceId = task.get("processInstanceId");
			String taskName = task.get("name");
			String taskId = task.get("id");
			openTask.setTaskId(taskId);
			openTask.setTaskName(taskName);
			openTask.setOrderId(getOrder(taskProcessInstanceId));
			tasks.add(openTask);
			System.out.println(
					"TaskName is " + taskName + " taskid is " + taskId + " processinstanceId " + taskProcessInstanceId);
		});

		return tasks;
	}

	public String getOrder(String taskProcessInstanceId) {
		log.info("Process Instance id is  " + taskProcessInstanceId);
		List<LinkedHashMap<String, String>> initiateOrderTask = (List<LinkedHashMap<String, String>>) getHistoricTaskusingProcessInstanceIdAndName(
				taskProcessInstanceId, "Initiate Order").getBody().getData();

		Long taskId = initiateOrderTask.stream().mapToLong(obj -> Long.parseLong(obj.get("id"))).max().getAsLong();

		ResponseEntity<DataResponse> orderData = historyApi.getHistoricDetailInfo(null, taskProcessInstanceId, null,
				null, taskId.toString(), true, false);

		List<LinkedHashMap<String, String>> orderDetailsForm = (List<LinkedHashMap<String, String>>) orderData.getBody()
				.getData();

		log.info("Number of tasks in the collection " + initiateOrderTask.size());
		log.info("Task Id of the initiateorder is " + taskId);
		String orderId = null;
		for (LinkedHashMap<String, String> formMap : orderDetailsForm) {
			String propertyId = formMap.get("propertyId");
			if (propertyId.equals("orderId")) {
				orderId = formMap.get("propertyValue");
				log.info("Order Id is ##################" + orderId);
			}
		}
		return orderId;
	}

	public ResponseEntity<DataResponse> getHistoricTaskusingProcessInstanceIdAndName(String processInstanceId,
			String name) {
		return historyApi.listHistoricTaskInstances(null, processInstanceId, null, null, null, null, null, null, null,
				null, null, name, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

	}

}
