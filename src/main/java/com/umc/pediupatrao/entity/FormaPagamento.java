package com.umc.pediupatrao.entity;

public enum FormaPagamento {
    PIX("Pix"),
    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito"),
    DINHEIRO("Dinheiro"),
    NAO_INFORMADO("Não informado");

    private final String descricao;

    FormaPagamento(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
