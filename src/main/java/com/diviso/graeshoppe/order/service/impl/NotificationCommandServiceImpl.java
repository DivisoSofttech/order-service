package com.diviso.graeshoppe.order.service.impl;

import com.diviso.graeshoppe.order.service.KafkaMessagingService;
import com.diviso.graeshoppe.order.service.NotificationCommandService;
import com.diviso.graeshoppe.order.service.KafkaMessagingService.PublishResult;
import com.diviso.graeshoppe.notification.avro.Notification.Builder;
import com.diviso.graeshoppe.order.domain.Notification;
import com.diviso.graeshoppe.order.repository.NotificationRepository;
import com.diviso.graeshoppe.order.repository.search.NotificationSearchRepository;
import com.diviso.graeshoppe.order.service.dto.NotificationDTO;
import com.diviso.graeshoppe.order.service.mapper.NotificationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Notification.
 */
@Service
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {

	private final Logger log = LoggerFactory.getLogger(NotificationCommandServiceImpl.class);

	private final NotificationRepository notificationRepository;

	@Autowired
	private KafkaMessagingService kafkaMessagingService;
	private final NotificationMapper notificationMapper;

	private final NotificationSearchRepository notificationSearchRepository;

	public NotificationCommandServiceImpl(NotificationRepository notificationRepository,
			NotificationMapper notificationMapper, NotificationSearchRepository notificationSearchRepository) {
		this.notificationRepository = notificationRepository;
		this.notificationMapper = notificationMapper;
		this.notificationSearchRepository = notificationSearchRepository;
	}

	/**
	 * Save a notification.
	 *
	 * @param notificationDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public NotificationDTO save(NotificationDTO notificationDTO) {
		log.debug("Request to save Notification : {}", notificationDTO);
		Notification notification = notificationMapper.toEntity(notificationDTO);
		notification = notificationRepository.save(notification);
		NotificationDTO result = notificationMapper.toDto(notification);
		notificationSearchRepository.save(notification);
		return result;
	}

	@Override
	public void publishNotificationToMessageBroker(NotificationDTO notification) {
		Builder messageBuilder = com.diviso.graeshoppe.notification.avro.Notification.newBuilder()
				.setDate(notification.getDate().toEpochMilli()).setId(notification.getId())
				.setMessage(notification.getMessage()).setTargetId(notification.getTargetId())
				.setReceiverId(notification.getReceiverId()).setType(notification.getType())
				.setImageContentType(notification.getImageContentType()).setTitle(notification.getTitle())
				.setStatus(notification.getStatus());
		if (notification.getImage() != null) {
			messageBuilder.setImage(ByteBuffer.wrap(notification.getImage()));
		}

		try {
			PublishResult result = kafkaMessagingService.publishNotification(messageBuilder.build());
			log.info("Message has been sent to the message broker " + result);
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.error("Error occured while sending notification top kafka " + e.getMessage());
		} catch (InterruptedException e) {
			log.error("Error occured while sending notification top kafka " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Get all the notifications.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDTO> findAll(Pageable pageable) {
		log.debug("Request to get all Notifications");
		return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
	}

	/**
	 * Get one notification by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<NotificationDTO> findOne(Long id) {
		log.debug("Request to get Notification : {}", id);
		return notificationRepository.findById(id).map(notificationMapper::toDto);
	}

	/**
	 * Delete the notification by id.
	 *
	 * @param id the id of the entity
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete Notification : {}", id);
		notificationRepository.deleteById(id);
		notificationSearchRepository.deleteById(id);
	}

	/**
	 * Search for the notification corresponding to the query.
	 *
	 * @param query    the query of the search
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDTO> search(String query, Pageable pageable) {
		log.debug("Request to search for a page of Notifications for query {}", query);
		return notificationSearchRepository.search(queryStringQuery(query), pageable).map(notificationMapper::toDto);
	}
}
