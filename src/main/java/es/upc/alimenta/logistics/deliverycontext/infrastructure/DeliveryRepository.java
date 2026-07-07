package es.upc.alimenta.logistics.deliverycontext.infrastructure;

import es.upc.alimenta.logistics.deliverycontext.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    List<Delivery> findByStatus(es.upc.alimenta.logistics.deliverycontext.domain.DeliveryStatus status);
}
