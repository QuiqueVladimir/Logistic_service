package es.upc.alimenta.logistics.shared.infrastructure.messaging.kafka.publishers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogisticEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LogisticEventPublisher.class);

    private static final String TOPIC_CANCELLED = "logistic.delivery-cancelled";
    private static final String TOPIC_COMPLETED = "logistic.delivery-completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LogisticEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDeliveryCancelled(String deliveryId, String donationId, String shelterId) {
        var payload = Map.of(
                "eventType", "DeliveryCancelled",
                "deliveryId", deliveryId,
                "donationId", donationId,
                "shelterId", shelterId,
                "reason", "INACTIVITY_TIMEOUT");

        kafkaTemplate.send(TOPIC_CANCELLED, donationId, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish DeliveryCancelled for deliveryId={}", deliveryId, ex);
                    } else {
                        log.info("Published DeliveryCancelled for deliveryId={}, donationId={}", deliveryId, donationId);
                    }
                });
    }

    public void publishDeliveryCompleted(String deliveryId, String donationId, String shelterId) {
        var payload = Map.of(
                "eventType", "DeliveryCompleted",
                "deliveryId", deliveryId,
                "donationId", donationId,
                "shelterId", shelterId);

        kafkaTemplate.send(TOPIC_COMPLETED, donationId, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish DeliveryCompleted for deliveryId={}", deliveryId, ex);
                    } else {
                        log.info("Published DeliveryCompleted for deliveryId={}, donationId={}", deliveryId, donationId);
                    }
                });
    }
}
