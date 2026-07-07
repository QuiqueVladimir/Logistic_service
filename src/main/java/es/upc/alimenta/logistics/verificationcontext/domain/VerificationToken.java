package es.upc.alimenta.logistics.verificationcontext.domain;

public record VerificationToken(String value) {
    public VerificationToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
    }
}
