package br.com.oficina.os.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    private UUID id;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 20)
    private String trackingCode;

    @Column(name = "cliente_cpf_cnpj", nullable = false, length = 18)
    private String clienteCpfCnpj;

    @Column(name = "veiculo_placa", nullable = false, length = 8)
    private String veiculoPlaca;

    @Column(name = "veiculo_marca", length = 80)
    private String veiculoMarca;

    @Column(name = "veiculo_modelo", length = 80)
    private String veiculoModelo;

    @Column(name = "veiculo_ano")
    private Integer veiculoAno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrdemServico status;

    @Column(name = "valor_estimado", precision = 12, scale = 2)
    private BigDecimal valorEstimado;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServicoItemServico> itensServico = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServicoItemPeca> itensPeca = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    protected OrdemServico() {}

    public static OrdemServico abrir(
        String clienteCpfCnpj,
        String veiculoPlaca,
        String veiculoMarca,
        String veiculoModelo,
        int veiculoAno,
        List<ItemServicoCmd> servicos,
        List<ItemPecaCmd> pecas
    ) {
        if (servicos == null || servicos.isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um servico e obrigatorio");
        }
        var os = new OrdemServico();
        os.id = UUID.randomUUID();
        os.trackingCode = TrackingCodeGenerator.generate();
        os.clienteCpfCnpj = clienteCpfCnpj;
        os.veiculoPlaca = veiculoPlaca;
        os.veiculoMarca = veiculoMarca;
        os.veiculoModelo = veiculoModelo;
        os.veiculoAno = veiculoAno;
        os.status = StatusOrdemServico.RECEBIDA;
        os.criadoEm = Instant.now();

        BigDecimal total = BigDecimal.ZERO;
        for (ItemServicoCmd s : servicos) {
            var item = OrdemServicoItemServico.criar(os, s.servicoId(), s.nome(), s.preco(), s.quantidade());
            os.itensServico.add(item);
            total = total.add(s.preco().multiply(BigDecimal.valueOf(s.quantidade())));
        }
        for (ItemPecaCmd p : pecas) {
            var item = OrdemServicoItemPeca.criar(os, p.pecaId(), p.nome(), p.precoUnitario(), p.quantidade());
            os.itensPeca.add(item);
            total = total.add(p.precoUnitario().multiply(BigDecimal.valueOf(p.quantidade())));
        }
        os.valorEstimado = total;
        return os;
    }

    public void atualizarStatus(StatusOrdemServico novoStatus) {
        this.status.validarTransicaoPara(novoStatus);
        this.status = novoStatus;
        this.atualizadoEm = Instant.now();
    }

    public UUID id() { return id; }
    public String trackingCode() { return trackingCode; }
    public String clienteCpfCnpj() { return clienteCpfCnpj; }
    public String veiculoPlaca() { return veiculoPlaca; }
    public String veiculoMarca() { return veiculoMarca; }
    public String veiculoModelo() { return veiculoModelo; }
    public Integer veiculoAno() { return veiculoAno; }
    public StatusOrdemServico status() { return status; }
    public BigDecimal valorEstimado() { return valorEstimado; }
    public List<OrdemServicoItemServico> itensServico() { return Collections.unmodifiableList(itensServico); }
    public List<OrdemServicoItemPeca> itensPeca() { return Collections.unmodifiableList(itensPeca); }
    public Instant criadoEm() { return criadoEm; }
    public Instant atualizadoEm() { return atualizadoEm; }

    public record ItemServicoCmd(String servicoId, String nome, BigDecimal preco, int quantidade) {}
    public record ItemPecaCmd(String pecaId, String nome, BigDecimal precoUnitario, int quantidade) {}
}
