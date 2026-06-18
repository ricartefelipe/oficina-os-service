package br.com.oficina.os.application.port.out;

import br.com.oficina.os.domain.OrdemServico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoPersistencePort {
    OrdemServico salvar(OrdemServico os);
    Optional<OrdemServico> buscarPorId(UUID id);
    Optional<OrdemServico> buscarPorTrackingCode(String trackingCode);
    List<OrdemServico> listarAtivas();
}
