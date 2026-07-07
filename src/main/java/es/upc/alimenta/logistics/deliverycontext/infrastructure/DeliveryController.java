package es.upc.alimenta.logistics.deliverycontext.infrastructure;

import es.upc.alimenta.logistics.deliverycontext.application.StartDeliveryUseCase;
import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final StartDeliveryUseCase startDeliveryUseCase;

    public DeliveryController(StartDeliveryUseCase startDeliveryUseCase) {
        this.startDeliveryUseCase = startDeliveryUseCase;
    }

    public static class StartDeliveryRequest {
        public String donation_id;
        public String shelter_id;
        public double restaurant_latitude;
        public double restaurant_longitude;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> startDelivery(@RequestBody StartDeliveryRequest request) {
        Delivery delivery = startDeliveryUseCase.execute(
                request.donation_id,
                request.shelter_id,
                request.restaurant_latitude,
                request.restaurant_longitude
        );

        Map<String, Object> response = new HashMap<>();
        response.put("delivery_id", delivery.getId());
        response.put("status", delivery.getStatus().name());
        response.put("verification_token", delivery.getVerificationToken());
        response.put("created_at", delivery.getStartedAt());

        return ResponseEntity.ok(response);
    }
}
