package br.com.oficina.os.adapters.in.web;

import br.com.oficina.os.application.OrdemServicoService;
import br.com.oficina.os.application.port.in.AbrirOrdemServicoUseCase.Command;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/ordens-servico")
@Tag(name = "Ordens de Serviço (Admin)")
public class AdminOrdemServicoController {

    private final OrdemServicoService service;

    public AdminOrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abre uma nova Ordem de Serviço")
    public OrdemServicoResponse abrir(@Valid @RequestBody AbrirOsRequest request) {
        var cmd = new Command(
            request.clienteCpfCnpj(),
            request.veiculoPlaca(),
            request.veiculoMarca(),
            request.veiculoModelo(),
            request.veiculoAno(),
            request.servicos().stream()
                .map(s -> new Command.ItemServicoCmd(s.servicoId(), s.nome(), s.preco(), s.quantidade()))
                .toList(),
            request.pecas() == null ? List.of() : request.pecas().stream()
                .map(p -> new Command.ItemPecaCmd(p.pecaId(), p.nome(), p.precoUnitario(), p.quantidade()))
                .toList()
        );
        return OrdemServicoResponse.from(service.abrir(cmd));
    }

    @GetMapping
    @Operation(summary = "Lista ordens de serviço ativas")
    public List<OrdemServicoResponse> listar() {
        return service.listarAtivas().stream().map(OrdemServicoResponse::from).toList();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de uma OS (transição manual)")
    public ResponseEntity<Void> atualizarStatus(
        @PathVariable UUID id,
        @RequestParam StatusOrdemServico novoStatus
    ) {
        service.atualizar(id, novoStatus);
        return ResponseEntity.noContent().build();
    }

    public record AbrirOsRequest(
        @NotBlank String clienteCpfCnpj,
        @NotBlank String veiculoPlaca,
        String veiculoMarca,
        String veiculoModelo,
        int veiculoAno,
        @NotEmpty List<ItemServicoRequest> servicos,
        List<ItemPecaRequest> pecas
    ) {
        public record ItemServicoRequest(
            @NotBlank String servicoId,
            @NotBlank String nome,
            @Positive BigDecimal preco,
            @Positive int quantidade
        ) {}
        public record ItemPecaRequest(
            @NotBlank String pecaId,
            @NotBlank String nome,
            @Positive BigDecimal precoUnitario,
            @Positive int quantidade
        ) {}
    }

    public record OrdemServicoResponse(
        UUID id,
        String trackingCode,
        String clienteCpfCnpj,
        String veiculoPlaca,
        StatusOrdemServico status,
        BigDecimal valorEstimado,
        Instant criadoEm
    ) {
        public static OrdemServicoResponse from(OrdemServico os) {
            return new OrdemServicoResponse(
                os.id(), os.trackingCode(), os.clienteCpfCnpj(), os.veiculoPlaca(),
                os.status(), os.valorEstimado(), os.criadoEm()
            );
        }
    }
}
