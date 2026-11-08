package com.umc.pediupatrao.dto;

import java.math.BigDecimal;

public record TaxaCancelamentoDashboard(BigDecimal taxaCancelamento, long quantidadeCancelados, long totalPedidos) {}
