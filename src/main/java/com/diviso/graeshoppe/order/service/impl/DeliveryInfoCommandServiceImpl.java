package com.diviso.graeshoppe.order.service.impl;

import com.diviso.graeshoppe.order.service.AddressService;
import com.diviso.graeshoppe.order.service.DeliveryInfoService;
import com.diviso.graeshoppe.order.service.NotificationCommandService;
import com.diviso.graeshoppe.order.service.OrderCommandService;
import com.diviso.graeshoppe.order.service.OrderQueryService;
import com.diviso.graeshoppe.order.client.bpmn.api.FormsApi;
import com.diviso.graeshoppe.order.client.bpmn.api.TasksApi;
import com.diviso.graeshoppe.order.client.bpmn.model.RestFormProperty;
import com.diviso.graeshoppe.order.client.bpmn.model.SubmitFormRequest;
import com.diviso.graeshoppe.order.client.customer.api.CustomerResourceApi;
import com.diviso.graeshoppe.order.client.customer.model.Customer;
import com.diviso.graeshoppe.order.client.store.api.StoreResourceApi;
import com.diviso.graeshoppe.order.domain.DeliveryInfo;
import com.diviso.graeshoppe.order.repository.DeliveryInfoRepository;
import com.diviso.graeshoppe.order.repository.search.DeliveryInfoSearchRepository;
import com.diviso.graeshoppe.order.resource.assembler.CommandResource;
import com.diviso.graeshoppe.order.resource.assembler.ResourceAssembler;
import com.diviso.graeshoppe.order.service.dto.DeliveryInfoDTO;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;
import com.diviso.graeshoppe.order.service.mapper.DeliveryInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing DeliveryInfo.
 */
@Service
@Transactional
public class DeliveryInfoCommandServiceImpl implements DeliveryInfoService {

	private final Logger log = LoggerFactory.getLogger(DeliveryInfoCommandServiceImpl.class);
	@Autowired
	private CustomerResourceApi customerResourceApi;
	private final DeliveryInfoRepository deliveryInfoRepository;

	@Autowired
	private OrderCommandService orderService;

	@Autowired
	private OrderQueryService orderQueryService;

	@Autowired
	private NotificationCommandService notificationService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private TasksApi tasksApi;
	@Autowired
	private FormsApi formsApi;

	@Autowired
	private StoreResourceApi storeResourceApi;

	
	@Autowired
	private ResourceAssembler resourceAssembler;
	private final DeliveryInfoMapper deliveryInfoMapper;

	private final DeliveryInfoSearchRepository deliveryInfoSearchRepository;

	public DeliveryInfoCommandServiceImpl(DeliveryInfoRepository deliveryInfoRepository,
			DeliveryInfoMapper deliveryInfoMapper, DeliveryInfoSearchRepository deliveryInfoSearchRepository) {
		this.deliveryInfoRepository = deliveryInfoRepository;
		this.deliveryInfoMapper = deliveryInfoMapper;
		this.deliveryInfoSearchRepository = deliveryInfoSearchRepository;
	}

	/**
	 * Save a deliveryInfo.
	 *
	 * @param deliveryInfoDTO the entity to save
	 * @return the persisted entityorderId
	 */
	@Override
	public CommandResource save(DeliveryInfoDTO deliveryInfoDTO, String taskId, String orderId) {
		log.debug("Request to save DeliveryInfo : {}", deliveryInfoDTO);
		DeliveryInfo deliveryInfo = deliveryInfoMapper.toEntity(deliveryInfoDTO);
		deliveryInfo = deliveryInfoRepository.save(deliveryInfo);
		DeliveryInfoDTO result = deliveryInfoMapper.toDto(deliveryInfo);
		deliveryInfoSearchRepository.save(deliveryInfo);
		OrderDTO orderDTO = orderService.findByOrderID(orderId).get();
		//Store store = storeResourceApi.findByRegNoUsingGET(orderDTO.getStoreId()).getBody();
		String storeMail = storeResourceApi.findByRegNoUsingGET(orderDTO.getStoreId()).getBody().getEmail();
		Customer customer = customerResourceApi.findByReferenceUsingGET(orderDTO.getCustomerId()).getBody();
		Long phone = customer.getContact().getMobileNumber();
		Long phoneCode = customer.getContact().getPhoneCode();
		String customerEmail = customer.getContact().getEmail();
		CommandResource commandResource = confirmDelivery(taskId, phone, phoneCode, deliveryInfoDTO.getDeliveryType(),
				customerEmail, storeMail);
		commandResource.setSelfId(result.getId());
		update(result);
		orderDTO.setDeliveryInfoId(deliveryInfo.getId()); // updating the delivery info in corresponding order
		commandResource.setOrderId(orderId);
		
		//if(store.getStoreSettings().getOrderAcceptType() == "advanced") {
			orderDTO.setStatusId(2l); // order is marked as pending for payment
			orderService.update(orderDTO);
		//} else {
//			if (commandResource.getNextTaskName().equals("Accept Order")) {
//				NotificationDTO notificationDTO = new NotificationDTO();
//				notificationDTO.setDate(orderDTO.getDate());
//				notificationDTO.setMessage("You have new order request");
//				notificationDTO.setTitle("Order Request");
//				notificationDTO.setTargetId(orderId);
//				notificationDTO.setStatus("unread");
//				notificationDTO.setReceiverId(orderDTO.getStoreId());
//				notificationDTO.setType("Pending-Notification");
//				NotificationDTO resultNotification = notificationService.save(notificationDTO); // sending notifications
//																				// from here to the store
//				Boolean status = notificationService.publishNotificationToMessageBroker(resultNotification);
//				log.info("Notification publish status is " + status);
//				orderDTO.setStatusId(2l); // order is unapproved
//				orderService.update(orderDTO);
//			} else if (commandResource.getNextTaskName().equals("Process Payment")) {
//				
//				orderDTO.setStatusId(3l); // order is auto approved
//				orderService.update(orderDTO);
//
//			}
//		}
		
		return commandResource;
	}

	public CommandResource confirmDelivery(String taskId, Long phone, Long phoneCode, String deliveryType,
			String customerMail, String storeMail) {
		String processInstanceId = tasksApi.getTask(taskId).getBody().getProcessInstanceId();
		SubmitFormRequest formRequest = new SubmitFormRequest();
		List<RestFormProperty> properties = new ArrayList<RestFormProperty>();
		RestFormProperty phoneProperty = new RestFormProperty();
		phoneProperty.setId("phone");
		phoneProperty.setName("phone");
		phoneProperty.setType("long");
		phoneProperty.setValue(phone.toString());
		properties.add(phoneProperty);

		RestFormProperty phoneCodeProperty = new RestFormProperty();
		phoneCodeProperty.setId("phoneCode");
		phoneCodeProperty.setName("phoneCode");
		phoneCodeProperty.setType("long");
		phoneCodeProperty.setValue(phoneCode.toString());
		properties.add(phoneCodeProperty);

		RestFormProperty deliveryTypeProperty = new RestFormProperty();
		deliveryTypeProperty.setId("deliveryType");
		deliveryTypeProperty.setName("deliveryType");
		deliveryTypeProperty.setType("string");
		deliveryTypeProperty.setValue(deliveryType);
		properties.add(deliveryTypeProperty);

		RestFormProperty customerEmail = new RestFormProperty();
		customerEmail.setId("customerEmail");
		customerEmail.setName("customerEmail");
		customerEmail.setType("string");
		customerEmail.setValue(customerMail);
		properties.add(customerEmail);

		RestFormProperty storeEmail = new RestFormProperty();
		storeEmail.setId("storeEmail");
		storeEmail.setName("storeEmail");
		storeEmail.setType("string");
		storeEmail.setValue(storeMail);
		properties.add(storeEmail);

		formRequest.setProperties(properties);
		formRequest.setAction("completed");
		formRequest.setTaskId(taskId);
		formsApi.submitForm(formRequest);
		return resourceAssembler.toResource(processInstanceId);
	}

	@Override
	public DeliveryInfoDTO update(DeliveryInfoDTO deliveryInfoDTO) {
		log.debug("Request to save DeliveryInfo : {}", deliveryInfoDTO);
		DeliveryInfo deliveryInfo = deliveryInfoMapper.toEntity(deliveryInfoDTO);
		deliveryInfo = deliveryInfoRepository.save(deliveryInfo);
		DeliveryInfoDTO result = deliveryInfoMapper.toDto(deliveryInfo);
		deliveryInfoSearchRepository.save(deliveryInfo);
		/*OrderDTO order = orderQueryService.findByDeliveryInfoId(deliveryInfoDTO.getId());
		Long phone = 0l;
		if (deliveryInfo.getDeliveryAddress() != null) {
			if (deliveryInfo.getDeliveryAddress().getPhone() != null) {
				phone = deliveryInfo.getDeliveryAddress().getPhone();
			}
		} else {
			Customer customer = customerResourceApi.findByReferenceUsingGET(order.getCustomerId()).getBody();
			phone = customer.getContact().getMobileNumber();
		}
		 // orderService.publishMesssage(order.getOrderId(), phone, "UPDATE"); // sending order to MOM */
		return result;
	}

	/**
	 * Get all the deliveryInfos.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DeliveryInfoDTO> findAll(Pageable pageable) {
		log.debug("Request to get all DeliveryInfos");
		return deliveryInfoRepository.findAll(pageable).map(deliveryInfoMapper::toDto);
	}

	/**
	 * Get one deliveryInfo by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DeliveryInfoDTO> findOne(Long id) {
		log.debug("Request to get DeliveryInfo : {}", id);
		return deliveryInfoRepository.findById(id).map(deliveryInfoMapper::toDto);
	}

	/**
	 * Delete the deliveryInfo by id.
	 *
	 * @param id the id of the entity
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete DeliveryInfo : {}", id);
		deliveryInfoRepository.deleteById(id);
		deliveryInfoSearchRepository.deleteById(id);
	}

	/**
	 * Search for the deliveryInfo corresponding to the query.
	 *
	 * @param query    the query of the search
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DeliveryInfoDTO> search(String query, Pageable pageable) {
		log.debug("Request to search for a page of DeliveryInfos for query {}", query);
		return deliveryInfoSearchRepository.search(queryStringQuery(query), pageable).map(deliveryInfoMapper::toDto);
	}

	
}
