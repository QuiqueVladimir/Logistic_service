package es.upc.alimenta.logistics.deliverycontext.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    private String id;

    private String donationId;
    private String shelterId;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    
    private String verificationToken;

    private LocalDateTime startedAt;
    private LocalDateTime collectedAt;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String routeHistory;

    protected Delivery() {}

    public Delivery(DeliveryId id, String donationId, String shelterId, String verificationToken) {
        this.id = id.value();
        this.donationId = donationId;
        this.shelterId = shelterId;
        this.verificationToken = verificationToken;
        this.status = DeliveryStatus.IN_TRANSIT;
        this.startedAt = LocalDateTime.now();
        this.routeHistory = "";
    }

    public String getId() {
        return id;
    }

    public String getDonationId() {
        return donationId;
    }

    public String getShelterId() {
        return shelterId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getRouteHistory() {
        return routeHistory;
    }

    public void collect() {
        this.status = DeliveryStatus.COLLECTED;
        this.collectedAt = LocalDateTime.now();
    }

    public void complete(String routeHistory) {
        this.status = DeliveryStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.routeHistory = routeHistory;
    }

    public void cancelByInactivity() {
        this.status = DeliveryStatus.CANCELLED_BY_INACTIVITY;
        this.completedAt = LocalDateTime.now();
    }
}
