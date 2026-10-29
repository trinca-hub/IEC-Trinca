package br.com.fatecads.fatecads.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.security.UserDetailsImpl;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@SessionAttributes("carrinho")
public class MarketplaceController {

    private static final String CARRINHO = "carrinho";

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public MarketplaceController(ProdutoService produtoService, PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/marketplace")
    public String marketplace(Model model, @SessionAttribute(name = CARRINHO, required = false) Map<Integer, Integer> carrinho) {
        Map<Integer, Integer> itens = carrinho == null ? new LinkedHashMap<>() : carrinho;
        model.addAttribute("produtos", produtoService.findAll());
        model.addAttribute("quantidadeCarrinho", itens.values().stream().mapToInt(Integer::intValue).sum());
        return "marketplace";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionar(@RequestParam Integer produtoId, @RequestParam Integer quantidade,
            @SessionAttribute(name = CARRINHO, required = false) Map<Integer, Integer> carrinho,
            Model model) {
        if (quantidade == null || quantidade <= 0 || produtoService.findById(produtoId) == null) {
            return "redirect:/marketplace?erro=produto";
        }
        Map<Integer, Integer> itens = carrinho == null ? new LinkedHashMap<>() : carrinho;
        itens.merge(produtoId, quantidade, Integer::sum);
        model.addAttribute(CARRINHO, itens);
        return "redirect:/marketplace";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model,
            @SessionAttribute(name = CARRINHO, required = false) Map<Integer, Integer> carrinho) {
        Map<Integer, Integer> itens = carrinho == null ? new LinkedHashMap<>() : carrinho;
        List<CarrinhoItem> produtos = new ArrayList<>();
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : itens.entrySet()) {
            Produto produto = produtoService.findById(entry.getKey());
            if (produto != null) {
                CarrinhoItem item = new CarrinhoItem(produto, entry.getValue());
                produtos.add(item);
                total += item.getSubtotal();
            }
        }
        model.addAttribute("itens", produtos);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String remover(@RequestParam Integer produtoId,
            @SessionAttribute(name = CARRINHO, required = false) Map<Integer, Integer> carrinho,
            Model model) {
        if (carrinho != null) {
            carrinho.remove(produtoId);
            model.addAttribute(CARRINHO, carrinho);
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/checkout/confirmar")
    public String finalizar(@SessionAttribute(name = CARRINHO, required = false) Map<Integer, Integer> carrinho,
            @AuthenticationPrincipal UserDetailsImpl userDetails, Model model, HttpSession session) {
        Usuario usuario = userDetails.getUsuario();
        Aluno aluno = usuario.getAluno();
        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho?erro=vazio";
        }

        List<ItemDoPedido> itens = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : carrinho.entrySet()) {
            Produto produto = produtoService.findById(entry.getKey());
            if (produto == null) {
                continue;
            }
            ItemDoPedido item = new ItemDoPedido();
            item.setProduto(produto);
            item.setQuantidade(entry.getValue());
            itens.add(item);
        }

        Pedido pedido = new Pedido();
        pedido.setAluno(aluno);
        pedido.setUsuario(usuario);
        pedido.setItens(itens);
        pedidoService.salvarPedido(pedido);
        session.removeAttribute(CARRINHO);
        return "redirect:/marketplace?sucesso=pedido";
    }

    public static class CarrinhoItem {
        private final Produto produto;
        private final Integer quantidade;

        public CarrinhoItem(Produto produto, Integer quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() { return produto; }
        public Integer getQuantidade() { return quantidade; }
        public double getSubtotal() { return produto.getValorProduto() * quantidade; }
    }
}