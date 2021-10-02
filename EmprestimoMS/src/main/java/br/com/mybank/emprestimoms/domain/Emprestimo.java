package br.com.mybank.emprestimoms.domain;

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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal valorDoPrincipal;
    private BigDecimal valorTotalEmprestimo;
    private BigDecimal saldoDevedor;
    private LocalDate dataEmprestimo;
    private Integer NumeroDeParcelas;
    private Boolean ativo;
    private Integer idCliente;
    private Integer idUsuario;
}

