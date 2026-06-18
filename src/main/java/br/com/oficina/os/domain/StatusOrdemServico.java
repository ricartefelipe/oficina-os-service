package br.com.oficina.os.domain;

import java.util.EnumSet;
import java.util.Set;

public enum StatusOrdemServico {
    RECEBIDA,
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    PAGAMENTO_PENDENTE,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE,
    CANCELADA;

    public Set<StatusOrdemServico> proximosPermitidos() {
        return switch (this) {
            case RECEBIDA -> EnumSet.of(EM_DIAGNOSTICO);
            case EM_DIAGNOSTICO -> EnumSet.of(AGUARDANDO_APROVACAO);
            case AGUARDANDO_APROVACAO -> EnumSet.of(PAGAMENTO_PENDENTE, CANCELADA);
            case PAGAMENTO_PENDENTE -> EnumSet.of(EM_EXECUCAO, CANCELADA);
            case EM_EXECUCAO -> EnumSet.of(FINALIZADA);
            case FINALIZADA -> EnumSet.of(ENTREGUE);
            case ENTREGUE, CANCELADA -> EnumSet.noneOf(StatusOrdemServico.class);
        };
    }

    public void validarTransicaoPara(StatusOrdemServico novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("novoStatus nao pode ser null");
        }
        if (this == novoStatus) {
            throw new IllegalStateException("Transicao invalida: status ja esta em " + this);
        }
        if (!proximosPermitidos().contains(novoStatus)) {
            throw new IllegalStateException("Transicao invalida: " + this + " -> " + novoStatus);
        }
    }
}
