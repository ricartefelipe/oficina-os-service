package br.com.oficina.os.bdd;

import br.com.oficina.os.application.port.in.AbrirOrdemServicoUseCase;
import br.com.oficina.os.application.port.in.AbrirOrdemServicoUseCase.Command;
import br.com.oficina.os.domain.OrdemServico;
import br.com.oficina.os.domain.StatusOrdemServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class AbrirOsSteps {

    @Autowired
    AbrirOrdemServicoUseCase useCase;

    private String cpf;
    private String placa;
    private OrdemServico osResultado;
    private Exception excecao;

    @Dado("um cliente com CPF {string} e veículo com placa {string}")
    public void clienteComCpfEPlaca(String cpf, String placa) {
        this.cpf = cpf;
        this.placa = placa;
        this.osResultado = null;
        this.excecao = null;
    }

    @Quando("o atendente abre uma OS com o serviço {string} por {double}")
    public void abreOsComServico(String nomeServico, double preco) {
        var servico = new Command.ItemServicoCmd("srv-bdd-1", nomeServico, BigDecimal.valueOf(preco), 1);
        var cmd = new Command(cpf, placa, "Toyota", "Corolla", 2020, List.of(servico), List.of());
        osResultado = useCase.abrir(cmd);
    }

    @Quando("o atendente tenta abrir uma OS sem nenhum serviço")
    public void abreOsSemServico() {
        try {
            var cmd = new Command(cpf, placa, "Toyota", "Corolla", 2020, List.of(), List.of());
            osResultado = useCase.abrir(cmd);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a OS é criada com status {string}")
    public void osComStatus(String status) {
        assertThat(osResultado).isNotNull();
        assertThat(osResultado.status()).isEqualTo(StatusOrdemServico.valueOf(status));
    }

    @Entao("a OS possui um trackingCode gerado")
    public void osTemTrackingCode() {
        assertThat(osResultado.trackingCode()).isNotBlank();
    }

    @Entao("o valor estimado da OS é {double}")
    public void valorEstimado(double valor) {
        assertThat(osResultado.valorEstimado()).isEqualByComparingTo(BigDecimal.valueOf(valor));
    }

    @Entao("a operação falha com mensagem {string}")
    public void operacaoFalha(String mensagem) {
        assertThat(excecao).isNotNull();
        assertThat(excecao.getMessage()).contains(mensagem);
    }
}
