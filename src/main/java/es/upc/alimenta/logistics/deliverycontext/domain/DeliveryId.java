package es.upc.alimenta.logistics.deliverycontext.domain;

public record DeliveryId(String value) {
    public DeliveryId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DeliveryId cannot be null or empty");
        }
    }
}
