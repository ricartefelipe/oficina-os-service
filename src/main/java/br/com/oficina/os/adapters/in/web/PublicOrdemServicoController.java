package br.com.oficina.os.adapters.in.web;

import br.com.oficina.os.application.OrdemServicoService;
import br.com.oficina.os.adapters.in.web.AdminOrdemServicoController.OrdemServicoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/ordens-servico")
@Tag(name = "Ordens de Serviço (Público)")
public class PublicOrdemServicoController {

    private final OrdemServicoService service;

    public PublicOrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping("/{trackingCode}")
    @Operation(summary = "Consulta uma OS pelo tracking code (acesso público)")
    public OrdemServicoResponse consultar(@PathVariable String trackingCode) {
        return OrdemServicoResponse.from(service.buscarPorTrackingCode(trackingCode));
    }
}
