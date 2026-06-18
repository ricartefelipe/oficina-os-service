package br.com.oficina.os.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrdemServicoTest {

    private static final List<OrdemServico.ItemServicoCmd> UM_SERVICO = List.of(
        new OrdemServico.ItemServicoCmd("srv-1", "Troca de oleo", new BigDecimal("120.00"), 1)
    );

    @Test
    void deveIniciarComStatusRecebida() {
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, UM_SERVICO, List.of());
        assertThat(os.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
    }

    @Test
    void deveGerarTrackingCode() {
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, UM_SERVICO, List.of());
        assertThat(os.trackingCode()).isNotBlank().hasSize(12);
    }

    @Test
    void trackingCodesDevemSerUnicos() {
        var os1 = OrdemServico.abrir("111.111.111-11", "AAA1111", "Toyota", "Corolla", 2020, UM_SERVICO, List.of());
        var os2 = OrdemServico.abrir("222.222.222-22", "BBB2222", "Honda", "Civic", 2021, UM_SERVICO, List.of());
        assertThat(os1.trackingCode()).isNotEqualTo(os2.trackingCode());
    }

    @Test
    void deveCalcularValorEstimado() {
        var servicos = List.of(
            new OrdemServico.ItemServicoCmd("srv-1", "Troca de oleo", new BigDecimal("120.00"), 2),
            new OrdemServico.ItemServicoCmd("srv-2", "Alinhamento", new BigDecimal("80.00"), 1)
        );
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, servicos, List.of());
        assertThat(os.valorEstimado()).isEqualByComparingTo("320.00");
    }

    @Test
    void deveCalcularValorComPecas() {
        var servicos = List.of(new OrdemServico.ItemServicoCmd("srv-1", "Servico", new BigDecimal("100.00"), 1));
        var pecas = List.of(new OrdemServico.ItemPecaCmd("peca-1", "Filtro", new BigDecimal("50.00"), 2));
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, servicos, pecas);
        assertThat(os.valorEstimado()).isEqualByComparingTo("200.00");
    }

    @Test
    void deveFalharSemServicos() {
        assertThatThrownBy(
            () -> OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, List.of(), List.of())
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Pelo menos um servico");
    }

    @Test
    void deveAtualizarStatus() {
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, UM_SERVICO, List.of());
        os.atualizarStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(os.status()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(os.atualizadoEm()).isNotNull();
    }

    @Test
    void deveRejeitarStatusInvalido() {
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020, UM_SERVICO, List.of());
        assertThatThrownBy(
            () -> os.atualizarStatus(StatusOrdemServico.FINALIZADA)
        ).isInstanceOf(IllegalStateException.class);
    }
}
