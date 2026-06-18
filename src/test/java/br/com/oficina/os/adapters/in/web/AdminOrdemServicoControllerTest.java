package br.com.oficina.os.adapters.in.web;

import br.com.oficina.os.application.OrdemServicoService;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOrdemServicoController.class)
@ActiveProfiles("test")
class AdminOrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrdemServicoService service;

    private OrdemServico osFixture() {
        return OrdemServico.abrir(
            "12345678901", "ABC1234", "Toyota", "Corolla", 2020,
            List.of(new OrdemServico.ItemServicoCmd("s1", "Revisão", new BigDecimal("150.00"), 1)),
            List.of()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void abrir_comDadosValidos_deveRetornarCreated() throws Exception {
        var os = osFixture();
        when(service.abrir(any())).thenReturn(os);

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
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_deveRetornarListaDeOs() throws Exception {
        when(service.listarAtivas()).thenReturn(List.of(osFixture()));

        mockMvc.perform(get("/admin/ordens-servico"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].status").value("RECEBIDA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarStatus_deveRetornarNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).atualizar(any(), any());

        mockMvc.perform(patch("/admin/ordens-servico/{id}/status", id)
                .with(csrf())
                .param("novoStatus", "EM_DIAGNOSTICO"))
            .andExpect(status().isNoContent());
    }

    @Test
    void abrir_semAutenticacao_deveRetornarUnauthorized() throws Exception {
        mockMvc.perform(post("/admin/ordens-servico")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());
    }
}
