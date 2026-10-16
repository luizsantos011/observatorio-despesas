package com.example.observatorio_despesas.repository;

import com.example.observatorio_despesas.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface DespesaRepository extends JpaRepository<Despesa, UUID> {
    boolean existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValor(
            String codigoOrgao,
            LocalDate dataReferencia,
            String descricao,
            BigDecimal valor);

    boolean existsByCodigoOrgaoAndDataReferenciaAndDescricaoAndValorAndIdNot(
            String codigoOrgao,
            LocalDate dataReferencia,
            String descricao,
            BigDecimal valor,
            UUID id);
}
