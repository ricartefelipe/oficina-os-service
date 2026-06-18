package br.com.oficina.os.adapters.out.persistence;

import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface OrdemServicoJpaRepository extends JpaRepository<OrdemServico, UUID> {
    Optional<OrdemServico> findByTrackingCode(String trackingCode);
    List<OrdemServico> findByStatusNotIn(List<StatusOrdemServico> excluidos);
}
