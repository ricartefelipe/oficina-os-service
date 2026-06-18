package br.com.oficina.os.domain.events;

import br.com.oficina.os.domain.OrdemServico;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OsAbertaEvent(
    UUID osId,
    String trackingCode,
    String clienteCpfCnpj,
    String veiculoPlaca,
    List<ItemDto> servicos,
    List<ItemDto> pecas,
    BigDecimal valorEstimado,
    Instant ocorridoEm
) {
    public record ItemDto(String itemId, String nome, BigDecimal preco, int quantidade) {}

    public static OsAbertaEvent from(OrdemServico os) {
        var servicos = os.itensServico().stream()
            .map(s -> new ItemDto(s.servicoId(), s.nome(), s.preco(), s.quantidade()))
            .toList();
        var pecas = os.itensPeca().stream()
            .map(p -> new ItemDto(p.pecaId(), p.nome(), p.precoUnitario(), p.quantidade()))
            .toList();
        return new OsAbertaEvent(os.id(), os.trackingCode(), os.clienteCpfCnpj(), os.veiculoPlaca(),
            servicos, pecas, os.valorEstimado(), Instant.now());
    }
}
