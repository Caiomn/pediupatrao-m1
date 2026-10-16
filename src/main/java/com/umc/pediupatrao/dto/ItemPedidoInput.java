package com.umc.pediupatrao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemPedidoInput(
        @NotBlank(message = "Selecione um produto.")
        String produtoId,
        @Min(value = 1, message = "A quantidade deve ser maior que zero.")
        int qtde) {
}
