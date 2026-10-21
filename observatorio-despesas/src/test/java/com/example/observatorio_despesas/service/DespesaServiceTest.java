package com.example.observatorio_despesas.service;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.enums.Categoria;
import com.example.observatorio_despesas.exception.business.DespesaDuplicadaException;
import com.example.observatorio_despesas.exception.resource.RecursoNaoEncontradoException;
import com.example.observatorio_despesas.model.Despesa;
import com.example.observatorio_despesas.repository.DespesaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DespesaServiceTest {

    @Mock
    private DespesaRepository despesaRepository;

    @InjectMocks
    private DespesaService despesaService;

    @Test
    void deveCriarDespesaComSucesso() {
        DespesaRequestDTO requestDTO = criarRequestValido();

        when(despesaRepository.existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValor(
                requestDTO.codigoOrgao(),
                requestDTO.dataReferencia(),
                requestDTO.descricao(),
                requestDTO.valor())).thenReturn(false);

        when(despesaRepository.save(any(Despesa.class)))
                .thenAnswer(i -> i.getArgument(0));

        DespesaResponseDTO resultado = despesaService.criarDespesa(requestDTO);

        assertThat(resultado).isNotNull();
    }

    @Test
    void deveFalharCriacaoPorDuplicata() {
        DespesaRequestDTO requestDTO = criarRequestValido();

        when(despesaRepository.existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValor(
                requestDTO.codigoOrgao(),
                requestDTO.dataReferencia(),
                requestDTO.descricao(),
                requestDTO.valor()
        )).thenReturn(true);

        assertThatThrownBy(() -> despesaService.criarDespesa(requestDTO))
                .isInstanceOf(DespesaDuplicadaException.class);
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        Despesa despesa = criarDespesa();
        when(despesaRepository.findById(id)).thenReturn(Optional.of(despesa));

        DespesaResponseDTO resultado = despesaService.buscarDespesaPorId(id);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(despesa.getId());
    }

    @Test
    void deveRetornarNaoEncontradoBuscaPorId() {
        UUID id = UUID.randomUUID();
        Despesa despesa = criarDespesa();
        when(despesaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despesaService.buscarDespesaPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveRetornarPaginaDespesas() {
        List<Despesa> despesas = List.of(criarDespesa(), criarDespesa());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Despesa> page = new PageImpl<>(despesas, pageable, despesas.size());

        when(despesaRepository.findAll(pageable)).thenReturn(page);

        Page<DespesaResponseDTO> resultado = despesaService.listarDespesas(pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(despesas.size());
        assertThat(resultado.getContent()).hasSize(despesas.size());
    }

    private DespesaRequestDTO criarRequestValido() {
        return new DespesaRequestDTO(
                "123456789",
                "orgaoExemplo",
                Categoria.OUTROS,
                "testando",
                BigDecimal.valueOf(100.0),
                LocalDate.now()
        );
    }

    private Despesa criarDespesa() {
        return new Despesa(
                "123456789",
                "orgaoExemplo",
                Categoria.OUTROS,
                "testando",
                BigDecimal.valueOf(100.0),
                LocalDate.now()
        );
    }
}