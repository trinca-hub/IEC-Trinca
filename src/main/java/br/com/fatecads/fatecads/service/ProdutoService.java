package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import java.util.List;


@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;

    //método para salvar um produto
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    //método para listar todos os produtos
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    //método para buscar todos os produtos
    public Produto findById(Integer id) {
        return produtoRepository.findById(id).orElse(null);
    }
    
    //método para deletar um produto pelo id
    public void deleteById(Integer id) {
        produtoRepository.deleteById(id);
    }
}
