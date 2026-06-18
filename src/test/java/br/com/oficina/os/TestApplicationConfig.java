package br.com.oficina.os;

import br.com.oficina.os.application.port.out.EventPublisherPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestApplicationConfig {

    @Bean
    @Primary
    public EventPublisherPort eventPublisherPort() {
        return (routingKey, event) -> {};
    }
}
