package com.example.simuladorpixms.service;
import com.example.simuladorpixms.domain.ChavesPix;
import com.example.simuladorpixms.repository.ChavePixRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChavePixService {

    private ChavePixRepository chavePixRepository;


    public ResponseEntity<?> cadastrarChavePix(ChavesPix chavepix) {

        return ResponseEntity.status(HttpStatus.OK).body(chavePixRepository.save(chavepix));

    }

    public ResponseEntity<?> confirmarChave(String chave) {

        if (chavePixRepository.findAllByChavePix(chave).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("{ \"resp\" : \"false\"}");
        }

        return ResponseEntity.status(HttpStatus.OK).body("{ \"resp\" : \"true\"}");

    }
}
