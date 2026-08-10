package br.com.fatecads.fatecads.entity;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;       


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    private Integer quantidade;

    private Double preco;

    private Double subtotal;
    
    @ManyToOne
    @JoinColumn(name = "idProduto_fk")
    private Produto produto;

    @ManyToOne  
    @JoinColumn(name = "idPedido_fk")
    private Pedido pedido;

    //método para calcular o subtotal do item do pedido
    public Double calcularSubtotal() {
        return quantidade * preco;
    }
    //método para atualizar o subtotal do item do pedido
    public void atualizarSubtotal() {
        this.subtotal = calcularSubtotal();
    }


}
