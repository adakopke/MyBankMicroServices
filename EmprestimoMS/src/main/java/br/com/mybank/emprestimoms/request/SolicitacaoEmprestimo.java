package br.com.mybank.emprestimoms.request;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class SolicitacaoEmprestimo {

    private BigDecimal valorDoPrincipal;
    private Integer NumeroDeParcelas;
    private Integer idUsuario;
    private String contaDestino;

}
