package com.umc.pediupatrao.dto;

import java.math.BigDecimal;

public record IndicadoresDashboard(BigDecimal totalVendas, long quantidadePedidos, BigDecimal ticketMedio,
        long quantidadeCancelados, BigDecimal taxaCancelamento) {}
