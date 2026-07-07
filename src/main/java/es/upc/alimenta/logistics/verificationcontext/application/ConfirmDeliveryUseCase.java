package es.upc.alimenta.logistics.verificationcontext.application;

import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryStatus;
import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryRepository;
import es.upc.alimenta.logistics.verificationcontext.domain.ResponsibilityReceipt;
import es.upc.alimenta.logistics.verificationcontext.domain.VerificationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Service
public class ConfirmDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final EntityManager entityManager;

    public ConfirmDeliveryUseCase(DeliveryRepository deliveryRepository, EntityManager entityManager) {
        this.deliveryRepository = deliveryRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void execute(String deliveryId, String rawToken) {
        VerificationToken token = new VerificationToken(rawToken);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (!delivery.getVerificationToken().equals(token.value())) {
            throw new IllegalArgumentException("Invalid verification token");
        }

        if (delivery.getStatus() != DeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException("Delivery cannot be confirmed in current state");
        }

        delivery.collect();
        deliveryRepository.save(delivery);


        ResponsibilityReceipt receipt = new ResponsibilityReceipt(deliveryId);
        entityManager.persist(receipt);
    }
}
