package br.com.fatecads.fatecads.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void deveRecalcularValoresEAssociarItensAntesDeSalvar() {
        Aluno aluno = new Aluno();
        aluno.setIdAluno(7);

        Produto produto = new Produto();
        produto.setIdProduto(3);
        produto.setValorProduto(12.50);

        ItemDoPedido item = new ItemDoPedido();
        item.setProduto(produtoComId(3));
        item.setQuantidade(4);

        Pedido pedido = new Pedido();
        pedido.setAluno(aluno);
        pedido.setItens(List.of(item));

        when(produtoRepository.findById(3)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.salvarPedido(pedido);

        assertSame(pedido, resultado);
        assertSame(pedido, item.getPedido());
        assertSame(produto, item.getProduto());
        assertEquals(12.50, item.getPreco());
        assertEquals(50.00, item.getSubtotal());
        assertEquals(50.00, pedido.getTotalPedido());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveSalvarPedidoMesmoSemAlunoVinculado() {
        Produto produto = new Produto();
        produto.setIdProduto(3);
        produto.setValorProduto(10.00);

        ItemDoPedido item = new ItemDoPedido();
        item.setProduto(produtoComId(3));
        item.setQuantidade(2);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(item));

        when(produtoRepository.findById(3)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.salvarPedido(pedido);

        assertSame(pedido, resultado);
        assertEquals(20.00, pedido.getTotalPedido());
        assertSame(produto, item.getProduto());
        verify(pedidoRepository).save(pedido);
    }

    private Produto produtoComId(Integer id) {
        Produto produto = new Produto();
        produto.setIdProduto(id);
        produto.setValorProduto(999.99);
        return produto;
    }
}