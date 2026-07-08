package es.upc.alimenta.logistics.verificationcontext.application;

import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryId;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryStatus;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryRepository;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.RedisGeoService;
import es.upc.alimenta.logistics.shared.infrastructure.messaging.kafka.publishers.LogisticEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchiveDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final RedisGeoService redisGeoService;
    private final LogisticEventPublisher eventPublisher;

    public ArchiveDeliveryUseCase(DeliveryRepository deliveryRepository, RedisGeoService redisGeoService,
                                   LogisticEventPublisher eventPublisher) {
        this.deliveryRepository = deliveryRepository;
        this.redisGeoService = redisGeoService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(String rawDeliveryId, double destLat, double destLon) {
        Delivery delivery = deliveryRepository.findById(rawDeliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (delivery.getStatus() != DeliveryStatus.COLLECTED) {
            throw new IllegalStateException("Delivery is not in COLLECTED state");
        }

        String routeHistory = "PATH_LOG:[start->dest]";

        delivery.complete(routeHistory);
        deliveryRepository.save(delivery);

        redisGeoService.clearDeliveryData(new DeliveryId(rawDeliveryId));

        eventPublisher.publishDeliveryCompleted(delivery.getId(), delivery.getDonationId(), delivery.getShelterId());
    }
}
