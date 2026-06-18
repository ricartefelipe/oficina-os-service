package br.com.oficina.os.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "os_item_peca")
public class OrdemServicoItemPeca {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id", nullable = false)
    private OrdemServico ordemServico;

    @Column(name = "peca_id", nullable = false)
    private String pecaId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private int quantidade;

    protected OrdemServicoItemPeca() {}

    public static OrdemServicoItemPeca criar(OrdemServico os, String pecaId, String nome, BigDecimal precoUnitario, int quantidade) {
        var item = new OrdemServicoItemPeca();
        item.id = UUID.randomUUID();
        item.ordemServico = os;
        item.pecaId = pecaId;
        item.nome = nome;
        item.precoUnitario = precoUnitario;
        item.quantidade = quantidade;
        return item;
    }

    public UUID id() { return id; }
    public String pecaId() { return pecaId; }
    public String nome() { return nome; }
    public BigDecimal precoUnitario() { return precoUnitario; }
    public int quantidade() { return quantidade; }
}
