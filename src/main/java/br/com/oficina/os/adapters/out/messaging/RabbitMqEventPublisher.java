package br.com.oficina.os.adapters.out.messaging;

import br.com.oficina.os.application.port.out.EventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
class RabbitMqEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    RabbitMqEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String routingKey, Object event) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, routingKey, event);
    }
}
