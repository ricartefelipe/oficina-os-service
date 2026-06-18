package br.com.oficina.os.adapters.out.persistence;

import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrdemServicoPersistenceAdapterTest {

    private final OrdemServicoJpaRepository repository = Mockito.mock(OrdemServicoJpaRepository.class);
    private final OrdemServicoPersistenceAdapter adapter = new OrdemServicoPersistenceAdapter(repository);

    private OrdemServico criarOs() {
        return OrdemServico.abrir(
            "98765432100", "XYZ9999", "Honda", "Civic", 2021,
            List.of(new OrdemServico.ItemServicoCmd("s1", "Alinhamento", new BigDecimal("80.00"), 1)),
            List.of()
        );
    }

    @Test
    void salvar_deveDelegar() {
        var os = criarOs();
        when(repository.save(os)).thenReturn(os);

        var resultado = adapter.salvar(os);

        assertThat(resultado).isEqualTo(os);
        verify(repository).save(os);
    }

    @Test
    void buscarPorId_deveDelegar() {
        var id = UUID.randomUUID();
        var os = criarOs();
        when(repository.findById(id)).thenReturn(Optional.of(os));

        var resultado = adapter.buscarPorId(id);

        assertThat(resultado).isPresent();
        verify(repository).findById(id);
    }

    @Test
    void buscarPorTrackingCode_deveDelegar() {
        var os = criarOs();
        when(repository.findByTrackingCode("TC-001")).thenReturn(Optional.of(os));

        var resultado = adapter.buscarPorTrackingCode("TC-001");

        assertThat(resultado).isPresent();
        verify(repository).findByTrackingCode("TC-001");
    }

    @Test
    void listarAtivas_deveExcluirStatusFinais() {
        var os = criarOs();
        when(repository.findByStatusNotIn(any())).thenReturn(List.of(os));

        var ativas = adapter.listarAtivas();

        assertThat(ativas).containsExactly(os);
        verify(repository).findByStatusNotIn(
            List.of(StatusOrdemServico.FINALIZADA, StatusOrdemServico.ENTREGUE, StatusOrdemServico.CANCELADA)
        );
    }
}
