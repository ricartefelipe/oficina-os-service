package br.com.oficina.os.adapters.out.persistence;

import br.com.oficina.os.application.port.out.OrdemServicoPersistencePort;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class OrdemServicoPersistenceAdapter implements OrdemServicoPersistencePort {

    private final OrdemServicoJpaRepository repository;

    OrdemServicoPersistenceAdapter(OrdemServicoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico salvar(OrdemServico os) {
        return repository.save(os);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<OrdemServico> buscarPorTrackingCode(String trackingCode) {
        return repository.findByTrackingCode(trackingCode);
    }

    @Override
    public List<OrdemServico> listarAtivas() {
        return repository.findByStatusNotIn(
            List.of(StatusOrdemServico.FINALIZADA, StatusOrdemServico.ENTREGUE, StatusOrdemServico.CANCELADA)
        );
    }
}
