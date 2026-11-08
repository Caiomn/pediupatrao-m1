package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.IndicadoresDashboard;
import com.umc.pediupatrao.dto.PontoFaturamento;
import com.umc.pediupatrao.dto.ProdutoRanking;
import com.umc.pediupatrao.dto.StatusDashboard;
import com.umc.pediupatrao.dto.FormaPagamentoDashboard;
import com.umc.pediupatrao.dto.HorarioPicoDashboard;
import com.umc.pediupatrao.dto.TaxaCancelamentoDashboard;
import com.umc.pediupatrao.entity.FormaPagamento;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {
    private static final DateTimeFormatter DIA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("yyyy-MM");
    private final PedidoRepository pedidoRepository;

    public AdminDashboardService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    private List<Pedido> pedidos(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        LocalDateTime inicioInclusivo = inicio.atStartOfDay();
        LocalDateTime fimExclusivo = fim.plusDays(1).atStartOfDay();
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getDataHoraEntrada() != null
                        && !p.getDataHoraEntrada().isBefore(inicioInclusivo)
                        && p.getDataHoraEntrada().isBefore(fimExclusivo))
                .toList();
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("dataInicio e dataFim devem formar um período válido.");
        }
        if (ChronoUnit.DAYS.between(inicio, fim) > 3660) {
            throw new IllegalArgumentException("O período máximo permitido é de 10 anos.");
        }
    }

    public IndicadoresDashboard indicadores(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidosPeriodo = pedidos(inicio, fim);
        long quantidadeCancelados = pedidosPeriodo.stream()
                .filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();
        List<Pedido> vendas = pedidosPeriodo.stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO).toList();
        BigDecimal total = vendas.stream().map(Pedido::getValorTotal)
                .filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        long quantidade = vendas.size();
        BigDecimal taxaCancelamento = pedidosPeriodo.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(quantidadeCancelados)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(pedidosPeriodo.size()), 2, RoundingMode.HALF_UP);
        BigDecimal ticket = quantidade == 0 ? BigDecimal.ZERO.setScale(2)
                : total.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP);
        return new IndicadoresDashboard(total.setScale(2, RoundingMode.HALF_UP), quantidade, ticket,
                quantidadeCancelados, taxaCancelamento);
    }

    public TaxaCancelamentoDashboard taxaCancelamento(LocalDate inicio, LocalDate fim) {
        List<Pedido> periodo = pedidos(inicio, fim);
        long cancelados = periodo.stream().filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();
        BigDecimal taxa = periodo.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(cancelados).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(periodo.size()), 2, RoundingMode.HALF_UP);
        return new TaxaCancelamentoDashboard(taxa, cancelados, periodo.size());
    }

    public List<FormaPagamentoDashboard> formasPagamento(LocalDate inicio, LocalDate fim) {
        Map<FormaPagamento, Long> quantidades = new EnumMap<>(FormaPagamento.class);
        Map<FormaPagamento, BigDecimal> valores = new EnumMap<>(FormaPagamento.class);
        pedidos(inicio, fim).stream().filter(p -> p.getStatus() != StatusPedido.CANCELADO).forEach(p -> {
            FormaPagamento forma = p.getFormaPagamento();
            quantidades.merge(forma, 1L, Long::sum);
            valores.merge(forma, p.getValorTotal() == null ? BigDecimal.ZERO : p.getValorTotal(), BigDecimal::add);
        });
        return quantidades.keySet().stream().sorted()
                .map(forma -> new FormaPagamentoDashboard(forma.name(), forma.getDescricao(),
                        quantidades.get(forma), valores.getOrDefault(forma, BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP)))
                .toList();
    }

    public List<HorarioPicoDashboard> horariosPico(LocalDate inicio, LocalDate fim) {
        long[] quantidades = new long[24];
        pedidos(inicio, fim).forEach(p -> quantidades[p.getDataHoraEntrada().getHour()]++);
        List<HorarioPicoDashboard> resultado = new ArrayList<>(24);
        for (int hora = 0; hora < quantidades.length; hora++) {
            resultado.add(new HorarioPicoDashboard(hora, "%02dh".formatted(hora), quantidades[hora]));
        }
        return resultado;
    }

    public List<PontoFaturamento> graficos(LocalDate inicio, LocalDate fim) {
        List<Pedido> vendas = pedidos(inicio, fim).stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO).toList();
        boolean porDia = ChronoUnit.DAYS.between(inicio, fim) <= 62;
        Map<String, BigDecimal> totais = new LinkedHashMap<>();
        for (Pedido p : vendas) {
            String chave = porDia ? p.getDataHoraEntrada().toLocalDate().format(DIA)
                    : p.getDataHoraEntrada().toLocalDate().format(MES);
            totais.merge(chave, p.getValorTotal() == null ? BigDecimal.ZERO : p.getValorTotal(), BigDecimal::add);
        }
        LocalDate cursor = porDia ? inicio : inicio.withDayOfMonth(1);
        LocalDate limite = porDia ? fim : fim.withDayOfMonth(1);
        List<PontoFaturamento> resultado = new ArrayList<>();
        while (!cursor.isAfter(limite)) {
            String chave = cursor.format(porDia ? DIA : MES);
            resultado.add(new PontoFaturamento(chave,
                    totais.getOrDefault(chave, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)));
            cursor = porDia ? cursor.plusDays(1) : cursor.plusMonths(1);
        }
        return resultado;
    }

    public List<StatusDashboard> status(LocalDate inicio, LocalDate fim) {
        Map<StatusPedido, Long> contagem = new EnumMap<>(StatusPedido.class);
        pedidos(inicio, fim).forEach(p -> contagem.merge(p.getStatus(), 1L, Long::sum));
        return List.of(StatusPedido.values()).stream()
                .map(s -> new StatusDashboard(s.name(), descricao(s), contagem.getOrDefault(s, 0L))).toList();
    }

    public List<ProdutoRanking> rankingProdutos(LocalDate inicio, LocalDate fim) {
        Map<String, Long> quantidades = new LinkedHashMap<>();
        pedidos(inicio, fim).stream().filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .forEach(p -> p.getItens().forEach(i -> quantidades.merge(i.produto(), (long) i.qtde(), Long::sum)));
        return quantidades.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(5).map(e -> new ProdutoRanking(e.getKey(), e.getValue())).toList();
    }

    private String descricao(StatusPedido status) {
        return switch (status) {
            case RECEBIDO -> "Pendente";
            case EM_PREPARACAO -> "Em preparo";
            case PRONTO -> "Pronto";
            case SAIU_PARA_ENTREGA -> "Saiu para entrega";
            case FINALIZADO -> "Concluído";
            case CANCELADO -> "Cancelado";
        };
    }
}
