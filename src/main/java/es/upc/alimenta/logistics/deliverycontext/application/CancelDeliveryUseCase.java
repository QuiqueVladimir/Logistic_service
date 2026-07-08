package es.upc.alimenta.logistics.deliverycontext.application;

import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryId;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryStatus;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryRepository;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.RedisGeoService;
import es.upc.alimenta.logistics.shared.infrastructure.messaging.kafka.publishers.LogisticEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CancelDeliveryUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CancelDeliveryUseCase.class);
    private final DeliveryRepository repository;
    private final RedisGeoService redisGeoService;
    private final LogisticEventPublisher eventPublisher;

    private static final long INACTIVITY_THRESHOLD_MS = 20 * 60 * 1000;

    public CancelDeliveryUseCase(DeliveryRepository repository, RedisGeoService redisGeoService,
                                 LogisticEventPublisher eventPublisher) {
        this.repository = repository;
        this.redisGeoService = redisGeoService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void execute() {
        List<Delivery> activeDeliveries = repository.findByStatus(DeliveryStatus.IN_TRANSIT);
        long now = System.currentTimeMillis();

        for (Delivery delivery : activeDeliveries) {
            DeliveryId id = new DeliveryId(delivery.getId());
            Long lastUpdate = redisGeoService.getLastUpdateTime(id);

            boolean shouldCancel;
            if (lastUpdate == null) {
                long startedAtMs = delivery.getStartedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                shouldCancel = now - startedAtMs > INACTIVITY_THRESHOLD_MS;
            } else {
                shouldCancel = now - lastUpdate > INACTIVITY_THRESHOLD_MS;
            }

            if (shouldCancel) {
                cancel(delivery, id);
            }
        }
    }

    private void cancel(Delivery delivery, DeliveryId id) {
        delivery.cancelByInactivity();
        repository.save(delivery);
        redisGeoService.clearDeliveryData(id);

        logger.info("Delivery {} CANCELLED_BY_INACTIVITY. Notifying matching service to release donation {} and penalize shelter {}.",
                delivery.getId(), delivery.getDonationId(), delivery.getShelterId());

        eventPublisher.publishDeliveryCancelled(delivery.getId(), delivery.getDonationId(), delivery.getShelterId());
    }
}
