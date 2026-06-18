package br.com.oficina.os.adapters.out.messaging;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

class RabbitMqEventPublisherTest {

    @Test
    void publish_deveEncaminharParaRabbitTemplate() {
        RabbitTemplate template = Mockito.mock(RabbitTemplate.class);
        RabbitMqEventPublisher publisher = new RabbitMqEventPublisher(template);
        Object event = Map.of("osId", "abc");

        publisher.publish("os.aberta", event);

        verify(template).convertAndSend(RabbitMqConfig.EXCHANGE, "os.aberta", event);
    }
}
