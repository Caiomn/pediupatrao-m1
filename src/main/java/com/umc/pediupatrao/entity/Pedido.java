package com.umc.pediupatrao.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;
    private String clienteId;
    private List<ItemPedido> itens = new ArrayList<>();
    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;
    private String responsavelRegistro;
    private String responsavelSaida;
    private BigDecimal percentualDesconto = BigDecimal.ZERO;
    private StatusPedido status;
    private String justificativaCancelamento;
    private BigDecimal valorTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public Pedido() {
    }

    public Pedido(String clienteId, List<ItemPedido> itens, LocalDateTime dataHoraEntrada,
            String responsavelRegistro) {
        this.clienteId = clienteId;
        this.itens = new ArrayList<>(itens);
        this.dataHoraEntrada = dataHoraEntrada;
        this.responsavelRegistro = responsavelRegistro;
        this.status = StatusPedido.RECEBIDO;
        recalcularValorTotal();
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public List<ItemPedido> getItens() { return List.copyOf(itens); }
    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public String getResponsavelRegistro() { return responsavelRegistro; }
    public String getResponsavelSaida() { return responsavelSaida; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public StatusPedido getStatus() { return status; }
    public String getJustificativaCancelamento() { return justificativaCancelamento; }
    public BigDecimal getValorTotal() { return valorTotal; }

    public void transicionarPara(StatusPedido novoStatus, String responsavel) {
        if (status == StatusPedido.CANCELADO || status == StatusPedido.FINALIZADO) {
            throw new IllegalStateException("Pedidos cancelados ou finalizados não podem ser alterados.");
        }
        boolean transicaoValida = switch (status) {
            case RECEBIDO -> novoStatus == StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO -> novoStatus == StatusPedido.PRONTO;
            case PRONTO -> novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA -> novoStatus == StatusPedido.FINALIZADO;
            default -> false;
        };
        if (!transicaoValida) {
            throw new IllegalArgumentException("Transição de status inválida: " + status + " -> " + novoStatus);
        }
        status = novoStatus;
        if (novoStatus == StatusPedido.SAIU_PARA_ENTREGA || novoStatus == StatusPedido.FINALIZADO) {
            dataHoraSaida = LocalDateTime.now();
            responsavelSaida = responsavel;
        }
    }

    public void cancelar(String justificativa) {
        if (status == StatusPedido.CANCELADO || status == StatusPedido.FINALIZADO) {
            throw new IllegalStateException("Pedidos cancelados ou finalizados não podem ser alterados.");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("A justificativa do cancelamento é obrigatória.");
        }
        status = StatusPedido.CANCELADO;
        justificativaCancelamento = justificativa.trim();
    }

    public void aplicarDesconto(BigDecimal percentual) {
        if (status == StatusPedido.CANCELADO || status == StatusPedido.FINALIZADO
                || status == StatusPedido.SAIU_PARA_ENTREGA) {
            throw new IllegalStateException("O desconto só pode ser aplicado antes da saída para entrega.");
        }
        if (percentual == null || percentual.signum() < 0
                || percentual.compareTo(BigDecimal.valueOf(20)) > 0) {
            throw new IllegalArgumentException("O desconto deve estar entre 0 e 20 por cento.");
        }
        percentualDesconto = percentual.setScale(2, RoundingMode.HALF_UP);
        recalcularValorTotal();
    }

    private void recalcularValorTotal() {
        BigDecimal bruto = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            bruto = bruto.add(item.getSubtotal());
        }
        BigDecimal fator = BigDecimal.ONE.subtract(
                percentualDesconto.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        valorTotal = bruto.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }
}
