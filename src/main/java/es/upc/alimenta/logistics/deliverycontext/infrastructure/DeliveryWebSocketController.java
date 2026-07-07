package es.upc.alimenta.logistics.deliverycontext.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upc.alimenta.logistics.deliverycontext.application.UpdateLocationUseCase;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeliveryWebSocketController extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UpdateLocationUseCase updateLocationUseCase;

    public DeliveryWebSocketController(@Lazy UpdateLocationUseCase updateLocationUseCase) {
        this.updateLocationUseCase = updateLocationUseCase;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());
        
        if (payload.has("delivery_id") && payload.has("current_latitude") && payload.has("current_longitude")) {
            String deliveryId = payload.get("delivery_id").asText();
            double lat = payload.get("current_latitude").asDouble();
            double lon = payload.get("current_longitude").asDouble();

            updateLocationUseCase.execute(deliveryId, lat, lon);
        }
    }

    public void broadcastMessage(String payload) {
        TextMessage textMessage = new TextMessage(payload);
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {

            }
        });
    }
}
