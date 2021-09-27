package br.com.mybank.emprestimo.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TransacoesEmCC {

    private String operacoes;
    private LocalDate data;
    private String cpfOrigem;
    private String cnpjOrigem;
    private String pixOrigem;
    private String contaOrigem;
    private String cpfDestino;
    private String cnpjDestino;
    private String pixDestino;
    private String contaDestino;
    private BigDecimal valor;
    private Integer idConta;

}
