package br.com.fatecads.fatecads.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveExigirFotoAoCriarProduto() {
        Produto produto = new Produto();

        assertThrows(IllegalArgumentException.class,
                () -> produtoService.save(produto, new MockMultipartFile("foto", new byte[0])));
    }

    @Test
    void devePreservarFotoAoEditarSemNovoUpload() {
        Produto existente = new Produto();
        existente.setIdProduto(8);
        existente.setFotoProduto(new byte[] { 1, 2, 3 });
        existente.setTipoFotoProduto("image/png");

        Produto alterado = new Produto();
        alterado.setIdProduto(8);
        when(produtoRepository.findById(8)).thenReturn(Optional.of(existente));
        when(produtoRepository.save(alterado)).thenReturn(alterado);

        produtoService.save(alterado, null);

        assertArrayEquals(new byte[] { 1, 2, 3 }, alterado.getFotoProduto());
        verify(produtoRepository).save(alterado);
    }

    @Test
    void deveRejeitarArquivoQueNaoEImagem() {
        Produto produto = new Produto();
        MockMultipartFile arquivo = new MockMultipartFile(
                "foto", "arquivo.txt", "text/plain", "texto".getBytes());

        assertThrows(IllegalArgumentException.class, () -> produtoService.save(produto, arquivo));
    }
}
