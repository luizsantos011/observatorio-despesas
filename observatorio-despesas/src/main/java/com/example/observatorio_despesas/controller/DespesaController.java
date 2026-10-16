package com.example.observatorio_despesas.controller;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.model.Despesa;
import com.example.observatorio_despesas.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/despesas")
public class DespesaController {

    private final DespesaService despesaService;

    public DespesaController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    @PostMapping
    public ResponseEntity<DespesaResponseDTO> criarDespesa(@Valid @RequestBody DespesaRequestDTO dto) {
        DespesaResponseDTO novaDespesa = despesaService.criarDespesa(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaDespesa.id())
                .toUri();
        return ResponseEntity.created(location).body(novaDespesa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaResponseDTO> buscarDespesaPorId(@PathVariable UUID id) {
        DespesaResponseDTO despesa = despesaService.buscarDespesaPorId(id);
        return ResponseEntity.ok(despesa);
    }
}
