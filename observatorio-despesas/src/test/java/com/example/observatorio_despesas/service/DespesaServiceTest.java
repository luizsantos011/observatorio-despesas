package com.example.observatorio_despesas.service;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.enums.Categoria;
import com.example.observatorio_despesas.exception.business.DespesaDuplicadaException;
import com.example.observatorio_despesas.model.Despesa;
import com.example.observatorio_despesas.repository.DespesaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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


    void deveFalharCriacaoPorDuplicata() {
        DespesaRequestDTO requestDTO = criarRequestInvalido();
        when(despesaRepository.existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValor(
                requestDTO.codigoOrgao(),
                requestDTO.dataReferencia(),
                requestDTO.descricao(),
                requestDTO.valor()
        )).thenReturn(true);

        DespesaResponseDTO resultado = despesaService.criarDespesa(requestDTO);

        assertThatThrownBy(() -> despesaService.criarDespesa(requestDTO))
                .isInstanceOf(DespesaDuplicadaException.class);
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

    private DespesaRequestDTO criarRequestInvalido() {
        return new DespesaRequestDTO(
                "",
                "orgaoExemplo",
                Categoria.OUTROS,
                "testando",
                BigDecimal.valueOf(-10.0),
                LocalDate.now()
        );
    }
}