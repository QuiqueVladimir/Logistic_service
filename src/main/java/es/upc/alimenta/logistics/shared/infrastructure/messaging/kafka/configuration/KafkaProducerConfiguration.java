package es.upc.alimenta.logistics.shared.infrastructure.messaging.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfiguration {

    @Bean
    public NewTopic deliveryCancelledTopic() {
        return TopicBuilder.name("logistic.delivery-cancelled")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryCompletedTopic() {
        return TopicBuilder.name("logistic.delivery-completed")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
