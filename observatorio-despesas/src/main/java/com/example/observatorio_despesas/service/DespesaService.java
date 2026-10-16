package com.example.observatorio_despesas.service;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.exception.business.DespesaDuplicadaException;
import com.example.observatorio_despesas.exception.resource.RecursoNaoEncontradoException;
import com.example.observatorio_despesas.model.Despesa;
import com.example.observatorio_despesas.repository.DespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DespesaService {
    private final DespesaRepository despesaRepository;

    public DespesaService(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    @Transactional
    public DespesaResponseDTO criarDespesa(DespesaRequestDTO dto) {
        if (despesaRepository.existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValor(
                dto.codigoOrgao(),
                dto.dataReferencia(),
                dto.descricao(),
                dto.valor()
        )) {
            throw new DespesaDuplicadaException("Despesa já existe para os critérios informados.");
        }
        Despesa despesa = mapToEntity(dto);
        Despesa salva = despesaRepository.save(despesa);
        return mapToDTO(salva);
    }

    public DespesaResponseDTO buscarDespesaPorId(UUID id) {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Despesa não encontrada"));
        return mapToDTO(despesa);
    }

    private DespesaResponseDTO mapToDTO(Despesa despesa) {
        return new DespesaResponseDTO(
                despesa.getId(),
                despesa.getCodigoOrgao(),
                despesa.getNomeOrgao(),
                despesa.getCategoria(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getDataReferencia(),
                despesa.getCriadoEm(),
                despesa.getAtualizadoEm()
        );
    }

    private Despesa mapToEntity(DespesaRequestDTO dto) {
        Despesa despesa = new Despesa(
                dto.codigoOrgao(),
                dto.nomeOrgao(),
                dto.categoria(),
                dto.descricao(),
                dto.valor(),
                dto.dataReferencia());
        return despesa;
    }
}
