package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.CancelamentoRequest;
import com.umc.pediupatrao.dto.DescontoRequest;
import com.umc.pediupatrao.dto.ItemPedidoInput;
import com.umc.pediupatrao.dto.TransicaoStatusRequest;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public Pedido criarPedido(@RequestBody @Valid CriarPedidoRequest request) {
        return pedidoService.criarPedido(request.clienteId(), request.itens());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public Pedido atualizarStatus(@PathVariable String id, @RequestBody @Valid TransicaoStatusRequest request) {
        return pedidoService.atualizarStatus(id, request.status());
    }

    @PostMapping("/{id}/desconto")
    @PreAuthorize("hasRole('GERENTE')")
    public Pedido aplicarDesconto(@PathVariable String id, @RequestBody @Valid DescontoRequest request) {
        return pedidoService.aplicarDesconto(id, request.percentual());
    }

    @PostMapping("/{id}/cancelamento")
    @PreAuthorize("hasRole('GERENTE')")
    public Pedido cancelarPedido(@PathVariable String id, @RequestBody @Valid CancelamentoRequest request) {
        return pedidoService.cancelarPedido(id, request.justificativa());
    }

    public record CriarPedidoRequest(String clienteId, List<@Valid ItemPedidoInput> itens) {
    }
}
