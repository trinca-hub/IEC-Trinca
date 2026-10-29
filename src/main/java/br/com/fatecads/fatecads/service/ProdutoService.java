package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import java.util.List;


@Service
public class ProdutoService {

    private static final long MAX_FOTO_BYTES = 5 * 1024 * 1024;
    
    @Autowired
    private ProdutoRepository produtoRepository;

    //método para salvar um produto
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto save(Produto produto, MultipartFile foto) {
        if (produto.getIdProduto() == null && (foto == null || foto.isEmpty())) {
            throw new IllegalArgumentException("A foto do produto é obrigatória");
        }

        if (foto != null && !foto.isEmpty()) {
            validarFoto(foto);
            try {
                byte[] conteudo = foto.getBytes();
                if (ImageIO.read(new ByteArrayInputStream(conteudo)) == null) {
                    throw new IllegalArgumentException("O arquivo enviado não é uma imagem válida");
                }
                produto.setFotoProduto(conteudo);
                produto.setTipoFotoProduto(foto.getContentType());
            } catch (IOException ex) {
                throw new IllegalArgumentException("Não foi possível ler a foto enviada", ex);
            }
        } else if (produto.getIdProduto() != null) {
            Produto existente = findById(produto.getIdProduto());
            if (existente != null) {
                produto.setFotoProduto(existente.getFotoProduto());
                produto.setTipoFotoProduto(existente.getTipoFotoProduto());
            }
        }

        return produtoRepository.save(produto);
    }

    private void validarFoto(MultipartFile foto) {
        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("A foto deve ser uma imagem");
        }
        if (foto.getSize() > MAX_FOTO_BYTES) {
            throw new IllegalArgumentException("A foto deve ter no máximo 5 MB");
        }
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
