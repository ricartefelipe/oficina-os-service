package br.com.oficina.os.application.port.in;

import br.com.oficina.os.domain.OrdemServico;

import java.math.BigDecimal;
import java.util.List;

public interface AbrirOrdemServicoUseCase {

    record Command(
        String clienteCpfCnpj,
        String veiculoPlaca,
        String veiculoMarca,
        String veiculoModelo,
        int veiculoAno,
        List<ItemServicoCmd> servicos,
        List<ItemPecaCmd> pecas
    ) {
        public record ItemServicoCmd(String servicoId, String nome, BigDecimal preco, int quantidade) {}
        public record ItemPecaCmd(String pecaId, String nome, BigDecimal precoUnitario, int quantidade) {}
    }

    OrdemServico abrir(Command cmd);
}
