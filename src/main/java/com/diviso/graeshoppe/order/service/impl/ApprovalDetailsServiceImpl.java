package com.diviso.graeshoppe.order.service.impl;

import com.diviso.graeshoppe.order.service.ApprovalDetailsService;
import com.diviso.graeshoppe.order.service.NotificationCommandService;
import com.diviso.graeshoppe.order.service.OrderCommandService;
import com.diviso.graeshoppe.order.client.bpmn.api.FormsApi;
import com.diviso.graeshoppe.order.client.bpmn.api.TasksApi;
import com.diviso.graeshoppe.order.client.bpmn.model.RestFormProperty;
import com.diviso.graeshoppe.order.client.bpmn.model.SubmitFormRequest;
import com.diviso.graeshoppe.order.client.customer.api.CustomerResourceApi;
import com.diviso.graeshoppe.order.domain.ApprovalDetails;
import com.diviso.graeshoppe.order.repository.ApprovalDetailsRepository;
import com.diviso.graeshoppe.order.repository.OrderRepository;
import com.diviso.graeshoppe.order.repository.search.ApprovalDetailsSearchRepository;
import com.diviso.graeshoppe.order.resource.assembler.CommandResource;
import com.diviso.graeshoppe.order.resource.assembler.ResourceAssembler;
import com.diviso.graeshoppe.order.security.SecurityUtils;
import com.diviso.graeshoppe.order.service.dto.ApprovalDetailsDTO;
import com.diviso.graeshoppe.order.service.dto.NotificationDTO;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;
import com.diviso.graeshoppe.order.service.mapper.ApprovalDetailsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ApprovalDetails.
 */
@Service
@Transactional
public class ApprovalDetailsServiceImpl implements ApprovalDetailsService {

	private final Logger log = LoggerFactory.getLogger(ApprovalDetailsServiceImpl.class);

	@Autowired
	private FormsApi formsApi;

	@Autowired
	private TasksApi tasksApi;
	@Autowired
	private CustomerResourceApi customerResourceApi;
	@Autowired
	private NotificationCommandService notificationService;
	@Autowired
	private OrderCommandService orderService;
	
	@Autowired
	private OrderRepository orderRespository;
	@Autowired
	private ResourceAssembler resourceAssembler;

	private final ApprovalDetailsRepository approvalDetailsRepository;

	private final ApprovalDetailsMapper approvalDetailsMapper;

	private final ApprovalDetailsSearchRepository approvalDetailsSearchRepository;

	public ApprovalDetailsServiceImpl(ApprovalDetailsRepository approvalDetailsRepository,
			ApprovalDetailsMapper approvalDetailsMapper,
			ApprovalDetailsSearchRepository approvalDetailsSearchRepository) {
		this.approvalDetailsRepository = approvalDetailsRepository;
		this.approvalDetailsMapper = approvalDetailsMapper;
		this.approvalDetailsSearchRepository = approvalDetailsSearchRepository;
	}

	/**
	 * Save a approvalDetails.
	 *
	 * @param approvalDetailsDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public CommandResource save(ApprovalDetailsDTO approvalDetailsDTO, String taskId) {
		log.debug("Request to save ApprovalDetails : {}", approvalDetailsDTO);
		ApprovalDetails approvalDetails = approvalDetailsMapper.toEntity(approvalDetailsDTO);
		approvalDetails = approvalDetailsRepository.save(approvalDetails);
		ApprovalDetailsDTO result = approvalDetailsMapper.toDto(approvalDetails);
		approvalDetailsSearchRepository.save(approvalDetails);
		CommandResource result1 = acceptOrder(approvalDetailsDTO, taskId);
		result1.setSelfId(result.getId());
		OrderDTO orderDTO = orderService.findByOrderID(approvalDetailsDTO.getOrderId()).get();
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setDate(approvalDetailsDTO.getAcceptedAt());
		if (approvalDetailsDTO.getDecision().equals("accepted")) {
			notificationDTO.setMessage("Your Order has been accepted cheers");
			notificationDTO.setTitle("Order Accepted");
		} else {
			notificationDTO.setMessage("Sorry Your Order has been rejected");
			notificationDTO.setTitle("Order Rejected");
		}
		notificationDTO.setTargetId(approvalDetailsDTO.getOrderId());
		notificationDTO.setStatus("unread");
		notificationDTO.setReceiverId(orderDTO.getCustomerId());
		notificationDTO.setType("Approved-Notification");
		NotificationDTO resultNotification=notificationService.save(notificationDTO); // sending notifications from here to the customer
		notificationService.publishNotificationToMessageBroker(resultNotification);
		orderDTO.setApprovalDetailsId(result.getId());
		orderDTO.setStatusId(7l); //payment-processed-approved
		orderService.update(orderDTO);
//		Long phone=0l;
//		DeliveryInfo deliveryInfo=orderRespository.findDeliveryInfoByOrderId(orderDTO.getOrderId());
//		if(deliveryInfo.getDeliveryAddress()!=null) {
//			phone = deliveryInfo.getDeliveryAddress().getPhone();
//		} else {
//			Customer customer = customerResourceApi.findByReferenceUsingGET(orderDTO.getCustomerId()).getBody();
//			phone = customer.getContact().getMobileNumber();
//		}
//		orderService.publishMesssage(approvalDetailsDTO.getOrderId(),phone,"CREATE");
		return result1;
	}

	public CommandResource acceptOrder(ApprovalDetailsDTO acceptOrderRequest, String taskId) {

		String processInstanceId = tasksApi.getTask(taskId).getBody().getProcessInstanceId();
		log.info("ProcessInstanceId is+ " + processInstanceId);
		SubmitFormRequest formRequest = new SubmitFormRequest();
		List<RestFormProperty> properties = new ArrayList<RestFormProperty>();
		RestFormProperty decision = new RestFormProperty();
		decision.setId("decision");
		decision.setName("decision");
		decision.setType("String");
		decision.setValue(acceptOrderRequest.getDecision());
		properties.add(decision);

		String stringDate = Date.from(acceptOrderRequest.getExpectedDelivery()).toString();
		String date = stringDate.substring(4, 10);
		String time = stringDate.substring(11, 16);
		RestFormProperty deliveryTime = new RestFormProperty();
		deliveryTime.setId("deliveryTime");
		deliveryTime.setName("deliveryTime");
		deliveryTime.setType("String");
		deliveryTime.setValue(date + " " + time);
		properties.add(deliveryTime);

		formRequest.setProperties(properties);
		formRequest.setAction("completed");
		formRequest.setTaskId(taskId);
		formsApi.submitForm(formRequest);
		// orderRepository.findByOrderId(acceptOrderRequest.getOrderId());
		// sendNotification(acceptOrderRequest.getOrderId(),
		// acceptOrderRequest.getCustomerId());
		CommandResource commandResource = resourceAssembler.toResource(processInstanceId);
		return commandResource;
	}

	// @SendToUser("/queue/notification")
	public NotificationDTO sendNotification(String orderId, String customerId) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setDate(Instant.now());
		notificationDTO.setMessage("Your order request has been accepted by the restaurant");
		notificationDTO.setTitle("Order Accepted");
		notificationDTO.setTargetId(orderId);
		notificationDTO.setType("AcceptOrder");
		notificationDTO.setStatus("unread");
		notificationDTO.setReceiverId(customerId);
		NotificationDTO result = notificationService.save(notificationDTO);
		log.info("Current User is " + SecurityUtils.getCurrentUserLogin().get());
		// template.convertAndSend("/topic/test", "test");
		// String username=SecurityUtils.getCurrentUserLogin().get();
		// template.convertAndSendToUser(username, "/queue/notification",
		// "Sample message hello " + username);

		// SimpleDateFormat("HH:mm:ss").format(new Date())+"- "+"sample message");

//		System.out.println("getMessageChannel " + template.getMessageChannel() + " getUserDestinationPrefix "
//				+ template.getUserDestinationPrefix() + " getDefaultDestination " + template.getDefaultDestination()
//				+ " getSendTimeout " + template.getSendTimeout() + " getMessageConverter "
//				+ template.getMessageConverter());

		return result;
	}

	/**
	 * Save a approvalDetails.
	 *
	 * @param approvalDetailsDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ApprovalDetailsDTO update(ApprovalDetailsDTO approvalDetailsDTO) {
		log.debug("Request to save ApprovalDetails : {}", approvalDetailsDTO);
		ApprovalDetails approvalDetails = approvalDetailsMapper.toEntity(approvalDetailsDTO);
		approvalDetails = approvalDetailsRepository.save(approvalDetails);
		ApprovalDetailsDTO result = approvalDetailsMapper.toDto(approvalDetails);
		approvalDetailsSearchRepository.save(approvalDetails);
		return result;
	}

	/**
	 * Get all the approvalDetails.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ApprovalDetailsDTO> findAll(Pageable pageable) {
		log.debug("Request to get all ApprovalDetails");
		return approvalDetailsRepository.findAll(pageable).map(approvalDetailsMapper::toDto);
	}

	/**
	 * Get one approvalDetails by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ApprovalDetailsDTO> findOne(Long id) {
		log.debug("Request to get ApprovalDetails : {}", id);
		return approvalDetailsRepository.findById(id).map(approvalDetailsMapper::toDto);
	}

	/**
	 * Delete the approvalDetails by id.
	 *
	 * @param id the id of the entity
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete ApprovalDetails : {}", id);
		approvalDetailsRepository.deleteById(id);
		approvalDetailsSearchRepository.deleteById(id);
	}

	/**
	 * Search for the approvalDetails corresponding to the query.
	 *
	 * @param query    the query of the search
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ApprovalDetailsDTO> search(String query, Pageable pageable) {
		log.debug("Request to search for a page of ApprovalDetails for query {}", query);
		return approvalDetailsSearchRepository.search(queryStringQuery(query), pageable)
				.map(approvalDetailsMapper::toDto);
	}
}
