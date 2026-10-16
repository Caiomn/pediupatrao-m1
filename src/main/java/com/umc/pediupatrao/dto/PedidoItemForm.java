package com.umc.pediupatrao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PedidoItemForm {

    @NotBlank(message = "Selecione um produto.")
    private String produtoId;

    @Min(value = 1, message = "A quantidade deve ser maior que zero.")
    private int qtde = 1;

    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public int getQtde() { return qtde; }
    public void setQtde(int qtde) { this.qtde = qtde; }
}
