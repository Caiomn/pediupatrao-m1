package com.umc.pediupatrao.dto;

import java.math.BigDecimal;

public record FormaPagamentoDashboard(String formaPagamento, String descricao, long quantidade, BigDecimal valorTotal) {}
