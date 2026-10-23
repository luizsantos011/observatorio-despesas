package com.example.observatorio_despesas.model;

import com.example.observatorio_despesas.enums.TipoAcao;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_despesa")
@Getter
public class HistoricoDespesa {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    private UUID despesaId;
    @Enumerated(EnumType.STRING)
    private TipoAcao acao;
    @JdbcTypeCode(SqlTypes.JSON)
    private String dadosAnteriores;
    @JdbcTypeCode(SqlTypes.JSON)
    private String dadosPosteriores;
    private OffsetDateTime ocorridoEm;

    protected HistoricoDespesa() {}

    public HistoricoDespesa(UUID despesaId, TipoAcao acao, String dadosAnteriores, String dadosPosteriores) {
        this.despesaId = despesaId;
        this.acao = acao;
        this.dadosAnteriores = dadosAnteriores;
        this.dadosPosteriores = dadosPosteriores;
    }

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        this.ocorridoEm = OffsetDateTime.now();
    }
}
