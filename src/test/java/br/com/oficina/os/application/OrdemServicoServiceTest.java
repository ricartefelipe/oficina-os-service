package br.com.oficina.os.application;

import br.com.oficina.os.application.port.in.AbrirOrdemServicoUseCase.Command;
import br.com.oficina.os.application.port.out.EventPublisherPort;
import br.com.oficina.os.application.port.out.OrdemServicoPersistencePort;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock OrdemServicoPersistencePort persistence;
    @Mock EventPublisherPort events;
    @InjectMocks OrdemServicoService service;

    private Command umComando() {
        return new Command(
            "123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new Command.ItemServicoCmd("srv-1", "Troca de oleo", new BigDecimal("120.00"), 1)),
            List.of()
        );
    }

    @Test
    void deveAbrirOsESalvarEPublicar() {
        var cmd = umComando();
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new OrdemServico.ItemServicoCmd("srv-1", "Troca de oleo", new BigDecimal("120.00"), 1)),
            List.of()
        );
        when(persistence.salvar(any())).thenReturn(os);

        var resultado = service.abrir(cmd);

        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
        verify(persistence).salvar(any());
        verify(events).publish(eq("os.aberta"), any());
    }

    @Test
    void deveAtualizarStatusExistente() {
        var id = UUID.randomUUID();
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new OrdemServico.ItemServicoCmd("srv-1", "Servico", new BigDecimal("100.00"), 1)),
            List.of()
        );
        when(persistence.buscarPorId(id)).thenReturn(Optional.of(os));
        when(persistence.salvar(any())).thenReturn(os);

        service.atualizar(id, StatusOrdemServico.EM_DIAGNOSTICO);

        assertThat(os.status()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(persistence).salvar(os);
    }

    @Test
    void deveLancarExcecaoSeOsNaoEncontrada() {
        when(persistence.buscarPorId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(
            () -> service.atualizar(UUID.randomUUID(), StatusOrdemServico.EM_DIAGNOSTICO)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void devePublicarEventoComOsId() {
        var cmd = umComando();
        var os = OrdemServico.abrir("123.456.789-09", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new OrdemServico.ItemServicoCmd("srv-1", "Servico", new BigDecimal("100.00"), 1)),
            List.of()
        );
        when(persistence.salvar(any())).thenReturn(os);

        service.abrir(cmd);

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(events).publish(eq("os.aberta"), captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }
}
