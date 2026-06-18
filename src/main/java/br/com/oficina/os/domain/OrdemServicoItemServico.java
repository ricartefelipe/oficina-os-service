package br.com.oficina.os.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "os_item_servico")
public class OrdemServicoItemServico {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id", nullable = false)
    private OrdemServico ordemServico;

    @Column(name = "servico_id", nullable = false)
    private String servicoId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private int quantidade;

    protected OrdemServicoItemServico() {}

    public static OrdemServicoItemServico criar(OrdemServico os, String servicoId, String nome, BigDecimal preco, int quantidade) {
        var item = new OrdemServicoItemServico();
        item.id = UUID.randomUUID();
        item.ordemServico = os;
        item.servicoId = servicoId;
        item.nome = nome;
        item.preco = preco;
        item.quantidade = quantidade;
        return item;
    }

    public UUID id() { return id; }
    public String servicoId() { return servicoId; }
    public String nome() { return nome; }
    public BigDecimal preco() { return preco; }
    public int quantidade() { return quantidade; }
}
