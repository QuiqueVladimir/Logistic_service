package es.upc.alimenta.logistics.shared.infrastructure.config;

import es.upc.alimenta.logistics.deliverycontext.infrastructure.DeliveryWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DeliveryWebSocketController deliveryWebSocketController;

    public WebSocketConfig(DeliveryWebSocketController deliveryWebSocketController) {
        this.deliveryWebSocketController = deliveryWebSocketController;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deliveryWebSocketController, "/ws/delivery/track").setAllowedOrigins("*");
    }
}
