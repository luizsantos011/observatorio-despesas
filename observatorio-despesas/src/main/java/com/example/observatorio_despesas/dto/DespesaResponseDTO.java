package com.example.observatorio_despesas.dto;

import com.example.observatorio_despesas.enums.Categoria;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DespesaResponseDTO(UUID id, String codigoOrgao, String nomeOrgao, Categoria categoria, String descricao,
                                 BigDecimal valor, LocalDate dataReferencia, OffsetDateTime criado, OffsetDateTime atualizadoEm) {
}
