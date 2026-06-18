package br.com.oficina.os.application.port.in;

import br.com.oficina.os.domain.StatusOrdemServico;

import java.util.UUID;

public interface AtualizarStatusOsUseCase {
    void atualizar(UUID osId, StatusOrdemServico novoStatus);
}
