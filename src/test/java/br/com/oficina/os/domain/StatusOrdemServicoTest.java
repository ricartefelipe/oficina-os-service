package br.com.oficina.os.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StatusOrdemServicoTest {

    @Test
    void devePermitirTransicaoValida() {
        assertThatNoException().isThrownBy(
            () -> StatusOrdemServico.RECEBIDA.validarTransicaoPara(StatusOrdemServico.EM_DIAGNOSTICO)
        );
    }

    @Test
    void deveRejeitarTransicaoInvalida() {
        assertThatThrownBy(
            () -> StatusOrdemServico.RECEBIDA.validarTransicaoPara(StatusOrdemServico.FINALIZADA)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("Transicao invalida");
    }

    @Test
    void deveRejeitarTransicaoParaMesmoStatus() {
        assertThatThrownBy(
            () -> StatusOrdemServico.RECEBIDA.validarTransicaoPara(StatusOrdemServico.RECEBIDA)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveRejeitarStatusNull() {
        assertThatThrownBy(
            () -> StatusOrdemServico.RECEBIDA.validarTransicaoPara(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fluxoFelizCompleto() {
        assertThatNoException().isThrownBy(() -> {
            StatusOrdemServico.RECEBIDA.validarTransicaoPara(StatusOrdemServico.EM_DIAGNOSTICO);
            StatusOrdemServico.EM_DIAGNOSTICO.validarTransicaoPara(StatusOrdemServico.AGUARDANDO_APROVACAO);
            StatusOrdemServico.AGUARDANDO_APROVACAO.validarTransicaoPara(StatusOrdemServico.PAGAMENTO_PENDENTE);
            StatusOrdemServico.PAGAMENTO_PENDENTE.validarTransicaoPara(StatusOrdemServico.EM_EXECUCAO);
            StatusOrdemServico.EM_EXECUCAO.validarTransicaoPara(StatusOrdemServico.FINALIZADA);
            StatusOrdemServico.FINALIZADA.validarTransicaoPara(StatusOrdemServico.ENTREGUE);
        });
    }

    @Test
    void fluxoCancelamento() {
        assertThatNoException().isThrownBy(() -> {
            StatusOrdemServico.AGUARDANDO_APROVACAO.validarTransicaoPara(StatusOrdemServico.CANCELADA);
        });
    }

    @Test
    void statusFinaisNaoPermitemTransicao() {
        assertThat(StatusOrdemServico.ENTREGUE.proximosPermitidos()).isEmpty();
        assertThat(StatusOrdemServico.CANCELADA.proximosPermitidos()).isEmpty();
    }
}
