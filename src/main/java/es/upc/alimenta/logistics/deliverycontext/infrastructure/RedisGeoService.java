package es.upc.alimenta.logistics.deliverycontext.infrastructure;

import es.upc.alimenta.logistics.deliverycontext.domain.DeliveryId;
import es.upc.alimenta.logistics.deliverycontext.domain.GeoLocation;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisGeoService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "delivery:locations";

    public RedisGeoService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addDestination(DeliveryId deliveryId, GeoLocation location) {
        String member = "delivery:" + deliveryId.value() + ":destination";
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(location.longitude(), location.latitude()), member);
    }

    public void updateCurrentLocation(DeliveryId deliveryId, GeoLocation location) {
        String member = "delivery:" + deliveryId.value() + ":current";
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(location.longitude(), location.latitude()), member);

        redisTemplate.opsForValue().set("delivery:" + deliveryId.value() + ":last_update", String.valueOf(System.currentTimeMillis()));
    }

    public Double calculateDistanceToDestination(DeliveryId deliveryId) {
        String currentMember = "delivery:" + deliveryId.value() + ":current";
        String destinationMember = "delivery:" + deliveryId.value() + ":destination";

        Distance distance = redisTemplate.opsForGeo().distance(GEO_KEY, currentMember, destinationMember, Metrics.KILOMETERS);
        if (distance != null) {
            return distance.getValue() * 1000;
        }
        return null;
    }
    
    public Long getLastUpdateTime(DeliveryId deliveryId) {
        String timestamp = redisTemplate.opsForValue().get("delivery:" + deliveryId.value() + ":last_update");
        return timestamp != null ? Long.parseLong(timestamp) : null;
    }

    public void clearDeliveryData(DeliveryId deliveryId) {
        redisTemplate.opsForZSet().remove(GEO_KEY, "delivery:" + deliveryId.value() + ":current", "delivery:" + deliveryId.value() + ":destination");
        redisTemplate.delete("delivery:" + deliveryId.value() + ":last_update");
    }
}
