package br.com.mybank.containvestimento.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContaInvestimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String numeroConta;
    private Integer idPessoa;
    private Integer idUsuario;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private boolean ativo;
    private BigDecimal saldoContaInvestimento;
}
