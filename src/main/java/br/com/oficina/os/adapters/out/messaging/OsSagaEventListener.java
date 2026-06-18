package br.com.oficina.os.adapters.out.messaging;

import br.com.oficina.os.application.port.in.AtualizarStatusOsUseCase;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class OsSagaEventListener {

    private static final Logger log = LoggerFactory.getLogger(OsSagaEventListener.class);

    private final AtualizarStatusOsUseCase atualizarStatus;

    public OsSagaEventListener(AtualizarStatusOsUseCase atualizarStatus) {
        this.atualizarStatus = atualizarStatus;
    }

    @RabbitListener(queues = "os-service.orcamento.aprovado")
    public void onOrcamentoAprovado(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: orcamento aprovado para OS {}", osId);
        atualizarStatus.atualizar(osId, StatusOrdemServico.PAGAMENTO_PENDENTE);
    }

    @RabbitListener(queues = "os-service.orcamento.recusado")
    public void onOrcamentoRecusado(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: orcamento recusado para OS {} — compensando", osId);
        atualizarStatus.atualizar(osId, StatusOrdemServico.CANCELADA);
    }

    @RabbitListener(queues = "os-service.pagamento.confirmado")
    public void onPagamentoConfirmado(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: pagamento confirmado para OS {}", osId);
        atualizarStatus.atualizar(osId, StatusOrdemServico.EM_EXECUCAO);
    }

    @RabbitListener(queues = "os-service.pagamento.falhou")
    public void onPagamentoFalhou(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.warn("Saga: pagamento falhou para OS {} — compensando", osId);
        atualizarStatus.atualizar(osId, StatusOrdemServico.CANCELADA);
    }

    @RabbitListener(queues = "os-service.execucao.finalizada")
    public void onExecucaoFinalizada(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: execucao finalizada para OS {}", osId);
        atualizarStatus.atualizar(osId, StatusOrdemServico.FINALIZADA);
    }
}
