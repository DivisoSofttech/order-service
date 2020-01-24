package com.diviso.graeshoppe.order.service;

import com.diviso.graeshoppe.avro.*;
import com.diviso.graeshoppe.notification.avro.Notification;
import com.diviso.graeshoppe.order.avro.ApprovalInfo;
import com.diviso.graeshoppe.order.avro.Order;
import com.diviso.graeshoppe.order.avro.OrderState;
import com.diviso.graeshoppe.payment.avro.Payment;

import com.diviso.graeshoppe.order.config.KafkaProperties;
import com.diviso.graeshoppe.order.models.OpenTask;
import com.diviso.graeshoppe.order.service.dto.OrderDTO;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaMessagingService {

	private final Logger log = LoggerFactory.getLogger(KafkaMessagingService.class);

	@Value("${topic.order.destination}")
	private String orderTopic;
	@Value("${topic.approvalInfo.destination}")
	private String approvaldetailsTopic;
	@Value("${topic.payment.destination}")
	private String paymentTopic;
	@Value("${topic.cancellation.destination}")
	private String cancellationTopic;
	@Value("${topic.refund.destination}")
	private String refundTopic;
	@Value("${topic.orderstate.destination}")
	private String orderStateTopic;

	@Autowired
	private OrderCommandService orderCommandService;

	private final OrderQueryService orderQueryService;

	@Value("${topic.notification.destination}")
	private String notificationTopic;
	private final KafkaProperties kafkaProperties;
	private KafkaProducer<String, Order> orderProducer;
	private KafkaProducer<String, Notification> notificatonProducer;
	private KafkaProducer<String, ApprovalInfo> approvalDetailsProducer;
	private KafkaProducer<String, OrderState> orderStateProducer;

	private ExecutorService sseExecutorService = Executors.newCachedThreadPool();

	public KafkaMessagingService(OrderQueryService orderQueryService, KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
		this.orderQueryService = orderQueryService;
		this.orderProducer = new KafkaProducer<>(kafkaProperties.getProducerProps());
		this.notificatonProducer = new KafkaProducer<>(kafkaProperties.getProducerProps());
		this.approvalDetailsProducer = new KafkaProducer<>(kafkaProperties.getProducerProps());
		this.orderStateProducer = new KafkaProducer<>(kafkaProperties.getProducerProps());

	}

	public PublishResult publishApprovalDetails(ApprovalInfo message) throws ExecutionException, InterruptedException {
		RecordMetadata metadata = approvalDetailsProducer.send(new ProducerRecord<>(approvaldetailsTopic, message))
				.get();
		return new PublishResult(metadata.topic(), metadata.partition(), metadata.offset(),
				Instant.ofEpochMilli(metadata.timestamp()));
	}

	public PublishResult publishOrderState(OrderState message) throws ExecutionException, InterruptedException {
		RecordMetadata metadata = orderStateProducer.send(new ProducerRecord<>(orderStateTopic, message)).get();
		return new PublishResult(metadata.topic(), metadata.partition(), metadata.offset(),
				Instant.ofEpochMilli(metadata.timestamp()));
	}

	public PublishResult publishOrder(Order message) throws ExecutionException, InterruptedException {
		RecordMetadata metadata = orderProducer.send(new ProducerRecord<>(orderTopic, message)).get();
		return new PublishResult(metadata.topic(), metadata.partition(), metadata.offset(),
				Instant.ofEpochMilli(metadata.timestamp()));
	}

	public PublishResult publishNotification(Notification message) throws ExecutionException, InterruptedException {
		RecordMetadata metadata = notificatonProducer.send(new ProducerRecord<>(notificationTopic, message)).get();
		return new PublishResult(metadata.topic(), metadata.partition(), metadata.offset(),
				Instant.ofEpochMilli(metadata.timestamp()));
	}

	public void subscribeCancellation() {
		log.info("Subscribing to cancellation");
		Map<String, Object> consumerProps = kafkaProperties.getConsumerProps();
		sseExecutorService.execute(() -> {
			KafkaConsumer<String, CancellationRequest> consumer = new KafkaConsumer<>(consumerProps);
			consumer.subscribe(Collections.singletonList(cancellationTopic));
			boolean exitLoop = false;
			while (!exitLoop) {
				try {
					ConsumerRecords<String, CancellationRequest> records = consumer.poll(Duration.ofSeconds(3));
					records.forEach(record -> {
						log.info("Record cancellationrequest consumed is " + record.value());
						CancellationRequest cancellationRequest = record.value();
						updateOrder(cancellationRequest);
					});

				} catch (Exception ex) {
					log.trace("Complete with error cancellationTopic {}", ex.getMessage(), ex);
					exitLoop = true;
					ex.printStackTrace();
				}
			}
			log.info("Out of the loop Consumer is going to close" + !exitLoop);
			consumer.close();
		});
	}

	public void subscribeRefundDetails() {
		log.info("Subscribing to refunddetails");
		Map<String, Object> consumerProps = kafkaProperties.getConsumerProps();
		sseExecutorService.execute(() -> {
			KafkaConsumer<String, RefundDetails> consumer = new KafkaConsumer<>(consumerProps);
			consumer.subscribe(Collections.singletonList(refundTopic));
			boolean exitLoop = false;
			while (!exitLoop) {
				try {
					ConsumerRecords<String, RefundDetails> records = consumer.poll(Duration.ofSeconds(3));
					records.forEach(record -> {
						log.info("Record refundDetails consumed is " + record.value());
						RefundDetails refundDetails = record.value();
						updateOrder(refundDetails);
					});

				} catch (Exception ex) {
					log.trace("Complete with error refundTopic {}", ex.getMessage(), ex);
					exitLoop = true;
					ex.printStackTrace();
				}
			}
			log.info("Out of the loop Consumer is going to close" + !exitLoop);
			consumer.close();
		});
	}

	public void subscribePayment() {
		Map<String, Object> consumerProps = kafkaProperties.getConsumerProps();
		sseExecutorService.execute(() -> {
			KafkaConsumer<String, Payment> consumer = new KafkaConsumer<>(consumerProps);
			consumer.subscribe(Collections.singletonList(paymentTopic));
			boolean exitLoop = false;
			while (!exitLoop) {
				try {
					ConsumerRecords<String, Payment> records = consumer.poll(Duration.ofSeconds(3));

					records.forEach(record -> {
						log.info("Record payment consumed is " + record.value());
						Payment payment = record.value();
						updateOrder(payment);
					});

				} catch (Exception ex) {
					log.trace("Complete with error paymentTopic {}", ex.getMessage(), ex);
					exitLoop = true;
					ex.printStackTrace();
				}
			}
			log.info("Out of the loop Consumer is going to close" + !exitLoop);
			consumer.close();
		});
	}

	public static class PublishResult {
		public final String topic;
		public final int partition;
		public final long offset;
		public final Instant timestamp;

		private PublishResult(String topic, int partition, long offset, Instant timestamp) {
			this.topic = topic;
			this.partition = partition;
			this.offset = offset;
			this.timestamp = timestamp;
		}
	}

	public void updateOrder(Payment payment) {
		CompletableFuture.runAsync(() -> {
			Optional<OrderDTO> orderDTO = orderCommandService.findByOrderID(payment.getTargetId());
			if (orderDTO.isPresent()) {
				orderDTO.get().setPaymentMode(payment.getPaymentType().toUpperCase());
				orderDTO.get().setPaymentRef(payment.getId().toString()); // in order to set the status need
																			// to check the
				OpenTask openTask = orderQueryService.getOpenTask("Accept Order", orderDTO.get().getOrderId(),
						orderDTO.get().getStoreId(), orderDTO.get().getProcessId()); // order flow if advanced
				// flow this // works
				orderDTO.get().setStatusId(6l); // payment-processed-unapproved
				orderDTO.get().setAcceptOrderId(openTask.getTaskId());
				orderCommandService.update(orderDTO.get());
				log.info("Order updated with payment ref" + payment.getTargetId());
			}

		});
	}

	private void updateOrder(CancellationRequest cancellationRequest) {
		CompletableFuture.runAsync(() -> {
			Optional<OrderDTO> orderDTO = orderCommandService.findByOrderID(cancellationRequest.getOrderId());
			if (orderDTO.isPresent()) {
				orderDTO.get().setStatusId(8l); // cancellation-requested
				orderDTO.get().setCancellationRef(cancellationRequest.getId());
				orderCommandService.update(orderDTO.get());
				log.info("Order is updated after cancellation with ref " + cancellationRequest.getId());
			}

		});
	}

	private void updateOrder(RefundDetails refundDetails) {
		CompletableFuture.runAsync(() -> {
			Optional<OrderDTO> orderDTO = orderCommandService.findByOrderID(refundDetails.getOrderId());
			if (orderDTO.isPresent()) {
				orderDTO.get().setStatusId(9l); // refund-completed
				orderCommandService.update(orderDTO.get());
				log.info("Order is updated after refund with ref " + refundDetails.getOrderId());
			}
		});
	}

	public void startConsumers() {
		subscribePayment();
		subscribeCancellation();
		subscribeRefundDetails();
	}
}
