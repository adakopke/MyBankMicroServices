package com.example.simuladorpixapi.controller;

import com.example.simuladorpixapi.domain.ChavesPix;
import com.example.simuladorpixapi.service.ChavePixService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pix")
@AllArgsConstructor
public class ChavePixController {

    private final ChavePixService chavePixService;

    @PostMapping("cadastrar")
        public ResponseEntity<?> cadastrarChavePix (@RequestBody ChavesPix chavepix) {
        return chavePixService.cadastrarChavePix(chavepix);
    }

    @GetMapping("consultar/{cpf}")
    public ResponseEntity<?> listarChaves (@PathVariable String cpf) {
        return chavePixService.confirmarChave(cpf);
    }

}
