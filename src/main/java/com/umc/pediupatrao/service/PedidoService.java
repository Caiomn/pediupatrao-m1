package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.entity.Auditoria.CampoModificado;
import com.umc.pediupatrao.dto.ItemPedidoInput;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import com.umc.pediupatrao.repository.ClienteRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    public Pedido criarPedido(String clienteId, List<ItemPedidoInput> itensInput) {
        if (clienteId == null || clienteId.isBlank() || itensInput == null || itensInput.isEmpty()) {
            throw new IllegalArgumentException("O cliente e pelo menos um item são obrigatórios.");
        }
        if (!clienteRepository.existsById(java.util.Objects.requireNonNull(clienteId))) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clienteId);
        }
        List<ItemPedido> itens = itensInput.stream().map(input -> {
            if (input == null) {
                throw new IllegalArgumentException("O item do pedido é obrigatório.");
            }
            String produtoId = input.produtoId();
            if (produtoId == null || produtoId.isBlank()) {
                throw new IllegalArgumentException("Produto do item é obrigatório.");
            }
            var produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
            if (Boolean.FALSE.equals(produto.getAtivo())) {
                throw new IllegalArgumentException("O produto está inativo: " + produto.getNome());
            }
            if (input.qtde() < 1 || produto.getPreco() == null || produto.getPreco() < 0) {
                throw new IllegalArgumentException("Quantidade ou preço do produto inválido.");
            }
            return new ItemPedido(produto.getNome(), input.qtde(),
                    BigDecimal.valueOf(produto.getPreco()).setScale(2, java.math.RoundingMode.HALF_UP));
        }).toList();
        Pedido pedido = new Pedido(clienteId, itens, LocalDateTime.now(), auditoriaService.usuarioAtual());
        pedido = pedidoRepository.save(pedido);
        auditoriaService.registrar("CRIACAO", "Pedido", pedido.getId(),
                java.util.Map.of(
                        "status", new CampoModificado(null, pedido.getStatus()),
                        "valorTotal", new CampoModificado(null, pedido.getValorTotal())),
                null);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public Pedido atualizarStatus(String id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(java.util.Objects.requireNonNull(id)).orElseThrow();
        StatusPedido anterior = pedido.getStatus();
        pedido.transicionarPara(status, auditoriaService.usuarioAtual());
        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar("ALTERACAO_STATUS", "Pedido", id,
                java.util.Map.of("status", new CampoModificado(anterior, status)), null);
        return salvo;
    }

    @PreAuthorize("hasRole('GERENTE')")
    public Pedido cancelarPedido(String id, String justificativa) {
        Pedido pedido = pedidoRepository.findById(java.util.Objects.requireNonNull(id)).orElseThrow();
        StatusPedido statusAnterior = pedido.getStatus();
        pedido.cancelar(justificativa);
        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar("CANCELAMENTO", "Pedido", id,
                java.util.Map.of("status", new CampoModificado(statusAnterior, pedido.getStatus())),
                pedido.getJustificativaCancelamento());
        return salvo;
    }

    @PreAuthorize("hasRole('GERENTE')")
    public Pedido aplicarDesconto(String id, BigDecimal percentual) {
        Pedido pedido = pedidoRepository.findById(java.util.Objects.requireNonNull(id)).orElseThrow();
        BigDecimal descontoAnterior = pedido.getPercentualDesconto();
        BigDecimal valorAnterior = pedido.getValorTotal();
        pedido.aplicarDesconto(percentual);
        Pedido salvo = pedidoRepository.save(pedido);
        LinkedHashMap<String, CampoModificado> campos = new LinkedHashMap<>();
        campos.put("percentualDesconto", new CampoModificado(descontoAnterior, pedido.getPercentualDesconto()));
        campos.put("valorTotal", new CampoModificado(valorAnterior, pedido.getValorTotal()));
        auditoriaService.registrar("APLICACAO_DESCONTO", "Pedido", id, campos, null);
        return salvo;
    }

}
