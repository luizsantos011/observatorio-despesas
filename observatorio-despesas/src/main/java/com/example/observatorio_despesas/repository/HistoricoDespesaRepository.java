package com.example.observatorio_despesas.repository;

import com.example.observatorio_despesas.model.HistoricoDespesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoDespesaRepository extends JpaRepository<HistoricoDespesa, UUID> {
   List<HistoricoDespesa> findByDespesaIdOrderByOcorridoEmAsc(UUID despesaId);
}
