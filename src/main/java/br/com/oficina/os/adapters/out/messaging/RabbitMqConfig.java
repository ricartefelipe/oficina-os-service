package br.com.oficina.os.adapters.out.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "oficina.events";
    public static final String DLX = "oficina.dlx";

    @Bean
    public TopicExchange mainExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange dlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private Queue durableWithDlq(String name) {
        return QueueBuilder.durable(name)
            .withArgument("x-dead-letter-exchange", DLX)
            .withArgument("x-dead-letter-routing-key", name + ".dlq")
            .build();
    }

    @Bean public Queue queueOrcamentoAprovado() { return durableWithDlq("os-service.orcamento.aprovado"); }
    @Bean public Queue queueOrcamentoRecusado() { return durableWithDlq("os-service.orcamento.recusado"); }
    @Bean public Queue queuePagamentoConfirmado() { return durableWithDlq("os-service.pagamento.confirmado"); }
    @Bean public Queue queuePagamentoFalhou() { return durableWithDlq("os-service.pagamento.falhou"); }
    @Bean public Queue queueExecucaoFinalizada() { return durableWithDlq("os-service.execucao.finalizada"); }

    @Bean
    public Binding bindOrcamentoAprovado(Queue queueOrcamentoAprovado, TopicExchange mainExchange) {
        return BindingBuilder.bind(queueOrcamentoAprovado).to(mainExchange).with("orcamento.aprovado");
    }

    @Bean
    public Binding bindOrcamentoRecusado(Queue queueOrcamentoRecusado, TopicExchange mainExchange) {
        return BindingBuilder.bind(queueOrcamentoRecusado).to(mainExchange).with("orcamento.recusado");
    }

    @Bean
    public Binding bindPagamentoConfirmado(Queue queuePagamentoConfirmado, TopicExchange mainExchange) {
        return BindingBuilder.bind(queuePagamentoConfirmado).to(mainExchange).with("pagamento.confirmado");
    }

    @Bean
    public Binding bindPagamentoFalhou(Queue queuePagamentoFalhou, TopicExchange mainExchange) {
        return BindingBuilder.bind(queuePagamentoFalhou).to(mainExchange).with("pagamento.falhou");
    }

    @Bean
    public Binding bindExecucaoFinalizada(Queue queueExecucaoFinalizada, TopicExchange mainExchange) {
        return BindingBuilder.bind(queueExecucaoFinalizada).to(mainExchange).with("execucao.finalizada");
    }
}
