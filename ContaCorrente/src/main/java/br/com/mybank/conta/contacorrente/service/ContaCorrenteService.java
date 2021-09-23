package br.com.mybank.conta.contacorrente.service;

import br.com.mybank.conta.contacorrente.domain.ContaCorrente;
import br.com.mybank.conta.contacorrente.repository.ContaCorrenteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContaCorrenteService {

    private final ContaCorrenteRepository contaCorrenteRepository;

    public ContaCorrente adicionarContaCorrente(ContaCorrente contaCorrente) {

        return contaCorrenteRepository.save(contaCorrente);

    }

    public List<ContaCorrente> listarTodas() {
        return contaCorrenteRepository.findAll();
    }


    public Optional<ContaCorrente> listarPorId(Integer id) {

        return contaCorrenteRepository.findById(id);

    }

    public ContaCorrente atualizarContacorrente(Integer id, ContaCorrente contaCorrente) {

        if (listarPorId(id).isEmpty() || !listarPorId(id).get().getId().equals(id)) {
            return null;
        }

        contaCorrente.setId(id);
        return contaCorrenteRepository.save(contaCorrente);

    }
}
