package com.umc.pediupatrao.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequest(
        @NotBlank(message = "A justificativa do cancelamento é obrigatória.")
        String justificativa) {
}
