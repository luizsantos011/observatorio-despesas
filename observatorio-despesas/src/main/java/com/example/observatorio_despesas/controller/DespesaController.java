package com.example.observatorio_despesas.controller;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
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

    @GetMapping()
    public ResponseEntity<Page<DespesaResponseDTO>> listarDespesas(
            @PageableDefault(page = 0, size = 10, sort = "dataReferencia") Pageable pageable) {
        Page<DespesaResponseDTO> despesas = despesaService.listarDespesas(pageable);
        return ResponseEntity.ok(despesas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponseDTO> atualizarDespesa(@PathVariable UUID id, @Valid @RequestBody DespesaRequestDTO dto) {
        DespesaResponseDTO despesaAtualizada = despesaService.atualizarDespesa(id, dto);
        return ResponseEntity.ok(despesaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable UUID id) {
        despesaService.deletarDespesa(id);
        return ResponseEntity.noContent().build();
    }
}
