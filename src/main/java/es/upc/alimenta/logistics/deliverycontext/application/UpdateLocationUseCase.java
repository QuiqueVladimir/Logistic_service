package es.upc.alimenta.logistics.deliverycontext.application;

import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryId;
import es.upc.alimenta.logistics.deliverycontext.domain.GeoLocation;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryWebSocketController;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.RedisGeoService;
import org.springframework.stereotype.Service;

@Service
public class UpdateLocationUseCase {

    private final RedisGeoService redisGeoService;
    private final DeliveryWebSocketController webSocketController;

    public UpdateLocationUseCase(RedisGeoService redisGeoService, DeliveryWebSocketController webSocketController) {
        this.redisGeoService = redisGeoService;
        this.webSocketController = webSocketController;
    }

    public void execute(String rawDeliveryId, double currentLat, double currentLon) {
        DeliveryId deliveryId = new DeliveryId(rawDeliveryId);
        GeoLocation currentLocation = new GeoLocation(currentLat, currentLon);

        redisGeoService.updateCurrentLocation(deliveryId, currentLocation);

        Double distance = redisGeoService.calculateDistanceToDestination(deliveryId);

        if (distance != null && distance < 50.0) {
            String eventPayload = String.format("{\"event\": \"ALBERGUE_ARRIVED\", \"delivery_id\": \"%s\"}", deliveryId.value());
            webSocketController.broadcastMessage(eventPayload);
        }
    }
}
