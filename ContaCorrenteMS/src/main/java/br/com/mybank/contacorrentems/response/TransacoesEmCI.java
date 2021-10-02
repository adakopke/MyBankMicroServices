package br.com.mybank.contacorrentems.response;
import br.com.mybank.contacorrentems.domain.Operacoes;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TransacoesEmCI {

    private Operacoes operacoes;
    private LocalDate data;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;

}
