package br.com.oficina.os.bdd;

import br.com.oficina.os.application.port.out.EventPublisherPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public EventPublisherPort eventPublisherPort() {
        return (routingKey, event) -> {};
    }
}
