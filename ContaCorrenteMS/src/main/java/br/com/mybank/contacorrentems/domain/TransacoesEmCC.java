package br.com.mybank.contacorrentems.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity

public class TransacoesEmCC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Operacoes operacoes;
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
