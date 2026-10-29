package br.com.fatecads.fatecads.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    //método para criar um pedido
    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido é obrigatório");
        }

        List<ItemDoPedido> itens = pedido.getItens();
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve possuir ao menos um item");
        }

        pedido.setDataPedido(LocalDate.now());
        for (ItemDoPedido item : itens) {
            if (item == null || item.getProduto() == null || item.getProduto().getIdProduto() == null
                    || item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Produto e quantidade válidos são obrigatórios");
            }

            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            item.setProduto(produto);
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            item.setPedido(pedido);
    }

        pedido.atualizarTotal();
    return pedidoRepository.save(pedido);
    }

}