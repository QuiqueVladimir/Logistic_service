package es.upc.alimenta.logistics.verificationcontext.infrastructure;

import es.upc.alimenta.logistics.verificationcontext.application.ArchiveDeliveryUseCase;
import es.upc.alimenta.logistics.verificationcontext.application.ConfirmDeliveryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deliveries")
public class VerificationController {

    private final ConfirmDeliveryUseCase confirmDeliveryUseCase;
    private final ArchiveDeliveryUseCase archiveDeliveryUseCase;

    public VerificationController(ConfirmDeliveryUseCase confirmDeliveryUseCase, ArchiveDeliveryUseCase archiveDeliveryUseCase) {
        this.confirmDeliveryUseCase = confirmDeliveryUseCase;
        this.archiveDeliveryUseCase = archiveDeliveryUseCase;
    }

    public static class ConfirmRequest {
        public String verification_token;
    }

    public static class ArchiveRequest {
        public double destination_latitude;
        public double destination_longitude;
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmDelivery(@PathVariable("id") String deliveryId, @RequestBody ConfirmRequest request) {
        confirmDeliveryUseCase.execute(deliveryId, request.verification_token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveDelivery(@PathVariable("id") String deliveryId, @RequestBody ArchiveRequest request) {
        archiveDeliveryUseCase.execute(deliveryId, request.destination_latitude, request.destination_longitude);
        return ResponseEntity.ok().build();
    }
}
