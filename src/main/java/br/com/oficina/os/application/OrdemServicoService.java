package br.com.oficina.os.application;

import br.com.oficina.os.application.port.in.AbrirOrdemServicoUseCase;
import br.com.oficina.os.application.port.in.AtualizarStatusOsUseCase;
import br.com.oficina.os.application.port.out.EventPublisherPort;
import br.com.oficina.os.application.port.out.OrdemServicoPersistencePort;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import br.com.oficina.os.domain.events.OsAbertaEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrdemServicoService implements AbrirOrdemServicoUseCase, AtualizarStatusOsUseCase {

    private final OrdemServicoPersistencePort persistence;
    private final EventPublisherPort events;

    public OrdemServicoService(OrdemServicoPersistencePort persistence, EventPublisherPort events) {
        this.persistence = persistence;
        this.events = events;
    }

    @Override
    @Transactional
    public OrdemServico abrir(Command cmd) {
        var servicos = cmd.servicos().stream()
            .map(s -> new OrdemServico.ItemServicoCmd(s.servicoId(), s.nome(), s.preco(), s.quantidade()))
            .toList();
        var pecas = cmd.pecas().stream()
            .map(p -> new OrdemServico.ItemPecaCmd(p.pecaId(), p.nome(), p.precoUnitario(), p.quantidade()))
            .toList();

        var os = OrdemServico.abrir(
            cmd.clienteCpfCnpj(), cmd.veiculoPlaca(), cmd.veiculoMarca(),
            cmd.veiculoModelo(), cmd.veiculoAno(), servicos, pecas
        );
        var salva = persistence.salvar(os);
        events.publish("os.aberta", OsAbertaEvent.from(salva));
        return salva;
    }

    @Override
    @Transactional
    public void atualizar(UUID osId, StatusOrdemServico novoStatus) {
        var os = persistence.buscarPorId(osId)
            .orElseThrow(() -> new NoSuchElementException("OS nao encontrada: " + osId));
        os.atualizarStatus(novoStatus);
        persistence.salvar(os);
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarAtivas() {
        return persistence.listarAtivas();
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarPorTrackingCode(String trackingCode) {
        return persistence.buscarPorTrackingCode(trackingCode)
            .orElseThrow(() -> new NoSuchElementException("OS nao encontrada: " + trackingCode));
    }
}
