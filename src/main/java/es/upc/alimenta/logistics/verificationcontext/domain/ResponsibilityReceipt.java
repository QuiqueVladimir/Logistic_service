package es.upc.alimenta.logistics.verificationcontext.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "responsibility_receipts")
public class ResponsibilityReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryId;
    private LocalDateTime signedAt;
    private String termsAccepted;

    protected ResponsibilityReceipt() {}

    public ResponsibilityReceipt(String deliveryId) {
        this.deliveryId = deliveryId;
        this.signedAt = LocalDateTime.now();
        this.termsAccepted = "SANITARY_RESPONSIBILITY_ACCEPTED";
    }

    public Long getId() {
        return id;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public String getTermsAccepted() {
        return termsAccepted;
    }
}
