package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public Produto novoProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Produto alternarAtivo(String id) {
        Produto produto = produtoRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        produto.setAtivo(!Boolean.TRUE.equals(produto.getAtivo()));
        return produtoRepository.save(produto);
    }

    // Método para excluir cliente
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(String id) {
        produtoRepository.deleteById(id);
    }


    // Método para salvar um novo cliente ou atualizar um cliente existente
    @PreAuthorize("hasRole('ADMIN')")
    public Produto salvar(Produto produto) {
        // Se o cliente não tem ID (novo cliente), salva como novo
        if (produto.getId() == null) {
            return produtoRepository.save(produto);  // Cria um novo cliente
        } // Se já tem ID (cliente existente), atualiza
        else {
            // Verifica se o cliente existe antes de atualizar
            if (produtoRepository.existsById(produto.getId())) {
                return produtoRepository.save(produto);  // Atualiza o cliente existente
            } else {
                throw new IllegalArgumentException("Cliente não encontrado para atualização.");
            }
        }
    }
}
