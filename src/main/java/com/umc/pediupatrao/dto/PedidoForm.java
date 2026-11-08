package com.umc.pediupatrao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import com.umc.pediupatrao.entity.FormaPagamento;

public class PedidoForm {

    @NotBlank(message = "Selecione um cliente.")
    private String clienteId;

    private FormaPagamento formaPagamento = FormaPagamento.NAO_INFORMADO;

    @Valid
    @NotEmpty(message = "Adicione pelo menos um item.")
    private List<PedidoItemForm> itens = new ArrayList<>(List.of(new PedidoItemForm()));

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public List<PedidoItemForm> getItens() { return itens; }
    public void setItens(List<PedidoItemForm> itens) { this.itens = itens; }

    public List<ItemPedidoInput> toInputs() {
        return itens.stream()
                .map(item -> new ItemPedidoInput(item.getProdutoId(), item.getQtde()))
                .toList();
    }
}
