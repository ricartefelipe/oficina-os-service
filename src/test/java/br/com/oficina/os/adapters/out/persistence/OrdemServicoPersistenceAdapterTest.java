package br.com.oficina.os.adapters.out.persistence;

import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(OrdemServicoPersistenceAdapter.class)
class OrdemServicoPersistenceAdapterTest {

    @Autowired
    private OrdemServicoPersistenceAdapter adapter;

    private OrdemServico criarOs() {
        return OrdemServico.abrir(
            "98765432100", "XYZ9999", "Honda", "Civic", 2021,
            List.of(new OrdemServico.ItemServicoCmd("s1", "Alinhamento", new BigDecimal("80.00"), 1)),
            List.of()
        );
    }

    @Test
    void salvarEBuscarPorId_deveFuncionar() {
        var salva = adapter.salvar(criarOs());
        var encontrada = adapter.buscarPorId(salva.id());
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().clienteCpfCnpj()).isEqualTo("98765432100");
    }

    @Test
    void buscarPorTrackingCode_deveFuncionar() {
        var salva = adapter.salvar(criarOs());
        var encontrada = adapter.buscarPorTrackingCode(salva.trackingCode());
        assertThat(encontrada).isPresent();
    }

    @Test
    void listarAtivas_deveExcluirFinalizadasECanceladas() {
        var ativa = adapter.salvar(criarOs());
        var cancelada = adapter.salvar(criarOs());
        cancelada.atualizarStatus(StatusOrdemServico.CANCELADA);
        adapter.salvar(cancelada);

        var ativas = adapter.listarAtivas();
        assertThat(ativas).contains(ativa);
        assertThat(ativas).doesNotContain(cancelada);
    }
}
