package br.com.mybank.conta.contacorrente.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContaCorrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String numeroConta;
    private Integer idPessoa;
    private Integer idUsuario;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private boolean ativo;
    private BigDecimal saldoCorrente;
    private BigDecimal saldoEspecial;
    private BigDecimal limiteEspecial;
    private LocalDateTime dataInicioUsoEspecial;
}
