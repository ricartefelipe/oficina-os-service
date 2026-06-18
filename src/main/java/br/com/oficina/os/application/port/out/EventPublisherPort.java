package br.com.oficina.os.application.port.out;

public interface EventPublisherPort {
    void publish(String routingKey, Object event);
}
