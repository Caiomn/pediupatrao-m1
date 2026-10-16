package com.umc.pediupatrao.entity;

import java.math.BigDecimal;

public record ItemPedido(String produto, int qtde, BigDecimal precoUnitario) {
    public ItemPedido {
        if (produto == null || produto.isBlank()) {
            throw new IllegalArgumentException("O produto do item é obrigatório.");
        }
        if (qtde < 1) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }
        if (precoUnitario == null || precoUnitario.signum() < 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser negativo.");
        }
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(qtde));
    }
}
