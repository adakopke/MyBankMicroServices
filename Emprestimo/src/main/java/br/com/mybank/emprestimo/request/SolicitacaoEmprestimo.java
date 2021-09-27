package br.com.mybank.emprestimo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class SolicitacaoEmprestimo {

    private BigDecimal valorDoPrincipal;
    private Integer NumeroDeParcelas;
    private Integer idUsuario;
    private String contaDestino;

}
