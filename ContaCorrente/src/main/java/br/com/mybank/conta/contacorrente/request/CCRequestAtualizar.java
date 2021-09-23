package br.com.mybank.conta.contacorrente.request;

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


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CCRequestAtualizar {

    private boolean ativo;
    private BigDecimal saldoCorrente;
    private BigDecimal saldoEspecial;
    private BigDecimal limiteEspecial;
}
