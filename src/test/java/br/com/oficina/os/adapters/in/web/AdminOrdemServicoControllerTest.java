package br.com.oficina.os.adapters.in.web;

import br.com.oficina.os.application.OrdemServicoService;
import br.com.oficina.os.domain.OrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminOrdemServicoControllerTest {

    private MockMvc mockMvc;
    private OrdemServicoService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = Mockito.mock(OrdemServicoService.class);
        var controller = new AdminOrdemServicoController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private OrdemServico osFixture() {
        return OrdemServico.abrir(
            "12345678901", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new OrdemServico.ItemServicoCmd("s1", "Revisão", new BigDecimal("150.00"), 1)),
            List.of()
        );
    }

    @Test
    void abrir_comDadosValidos_deveRetornarCreated() throws Exception {
        when(service.abrir(any())).thenReturn(osFixture());

        var body = """
            {
              "clienteCpfCnpj": "12345678901",
              "veiculoPlaca": "ABC1234",
              "veiculoMarca": "Toyota",
              "veiculoModelo": "Corolla",
              "veiculoAno": 2020,
              "servicos": [
                {"servicoId": "s1", "nome": "Revisão", "preco": 150.00, "quantidade": 1}
              ]
            }
            """;

        mockMvc.perform(post("/admin/ordens-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    void listar_deveRetornarListaDeOs() throws Exception {
        when(service.listarAtivas()).thenReturn(List.of(osFixture()));

        mockMvc.perform(get("/admin/ordens-servico"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].status").value("RECEBIDA"));
    }

    @Test
    void atualizarStatus_deveRetornarNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).atualizar(any(), any());

        mockMvc.perform(patch("/admin/ordens-servico/{id}/status", id)
                .param("novoStatus", "EM_DIAGNOSTICO"))
            .andExpect(status().isNoContent());
    }

    @Test
    void abrir_semServicos_deveRetornarBadRequest() throws Exception {
        var body = """
            {
              "clienteCpfCnpj": "12345678901",
              "veiculoPlaca": "ABC1234",
              "veiculoAno": 2020,
              "servicos": []
            }
            """;

        mockMvc.perform(post("/admin/ordens-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
