package br.com.oficina.os.adapters.out.messaging;

import br.com.oficina.os.application.port.in.AtualizarStatusOsUseCase;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;

class OsSagaEventListenerTest {

    private AtualizarStatusOsUseCase atualizarStatus;
    private OsSagaEventListener listener;

    @BeforeEach
    void setUp() {
        atualizarStatus = Mockito.mock(AtualizarStatusOsUseCase.class);
        listener = new OsSagaEventListener(atualizarStatus);
    }

    @Test
    void onOrcamentoAprovado_deveAtualizarParaPagamentoPendente() {
        UUID id = UUID.randomUUID();
        listener.onOrcamentoAprovado(Map.of("osId", id.toString()));
        verify(atualizarStatus).atualizar(id, StatusOrdemServico.PAGAMENTO_PENDENTE);
    }

    @Test
    void onOrcamentoRecusado_deveAtualizarParaCancelada() {
        UUID id = UUID.randomUUID();
        listener.onOrcamentoRecusado(Map.of("osId", id.toString()));
        verify(atualizarStatus).atualizar(id, StatusOrdemServico.CANCELADA);
    }

    @Test
    void onPagamentoConfirmado_deveAtualizarParaEmExecucao() {
        UUID id = UUID.randomUUID();
        listener.onPagamentoConfirmado(Map.of("osId", id.toString()));
        verify(atualizarStatus).atualizar(id, StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    void onPagamentoFalhou_deveAtualizarParaCancelada() {
        UUID id = UUID.randomUUID();
        listener.onPagamentoFalhou(Map.of("osId", id.toString()));
        verify(atualizarStatus).atualizar(id, StatusOrdemServico.CANCELADA);
    }

    @Test
    void onExecucaoFinalizada_deveAtualizarParaFinalizada() {
        UUID id = UUID.randomUUID();
        listener.onExecucaoFinalizada(Map.of("osId", id.toString()));
        verify(atualizarStatus).atualizar(id, StatusOrdemServico.FINALIZADA);
    }
}
