package es.upc.alimenta.logistics.deliverycontext.application;

import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryId;
import es.upc.alimenta.logistics.deliverycontext.domain.GeoLocation;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryRepository;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.RedisGeoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StartDeliveryUseCase {

    private final DeliveryRepository repository;
    private final RedisGeoService redisGeoService;

    public StartDeliveryUseCase(DeliveryRepository repository, RedisGeoService redisGeoService) {
        this.repository = repository;
        this.redisGeoService = redisGeoService;
    }

    @Transactional
    public Delivery execute(String donationId, String shelterId, double restaurantLat, double restaurantLon) {
        DeliveryId deliveryId = new DeliveryId("DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        String verificationToken = "HASH_" + UUID.randomUUID().toString().replace("-", "").toUpperCase();

        Delivery delivery = new Delivery(deliveryId, donationId, shelterId, verificationToken);
        repository.save(delivery);


        GeoLocation destination = new GeoLocation(restaurantLat, restaurantLon);
        redisGeoService.addDestination(deliveryId, destination);

        return delivery;
    }
}
