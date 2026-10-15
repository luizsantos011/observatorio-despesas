package com.example.observatorio_despesas.dto;

import com.example.observatorio_despesas.enums.Categoria;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequestDTO(
    @NotBlank(message = "O código do órgão é obrigatório")
    @Size(min =1, max =30, message = "O código do órgão deve ter no máximo 30 caracteres")
    String codigoOrgao,

    @NotBlank(message = "O nome do órgão é obrigatório")
    @Size(min =1, max =150, message = "O nome do órgão deve ter no máximo 150 caracteres")
    String nomeOrgao,

    @NotNull(message = "A categoria é obrigatória")
    Categoria categoria,

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 1, max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    String descricao,

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    @Digits(integer = 15, fraction = 2, message = "O valor deve ter no máximo 15 dígitos inteiros e 2 dígitos decimais")
    BigDecimal valor,

    @NotNull(message = "A data de referência é obrigatória")
    LocalDate dataReferencia
) {
}
