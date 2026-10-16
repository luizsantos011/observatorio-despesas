package com.example.observatorio_despesas.model;

import com.example.observatorio_despesas.enums.Categoria;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_despesa")
public class Despesa {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    private String codigoOrgao;
    private String nomeOrgao;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataReferencia;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    protected Despesa() {}

    public Despesa(String codigoOrgao, String nomeOrgao, Categoria categoria, String descricao,
                   BigDecimal valor, LocalDate dataReferencia) {
        this.codigoOrgao = codigoOrgao;
        this.nomeOrgao = nomeOrgao;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.dataReferencia = dataReferencia;
    }

    @PrePersist
    protected void antesDeSalvar() {
        if(id == null) {
            id = UUID.randomUUID();
        }
        this.criadoEm = OffsetDateTime.now();
        this.atualizadoEm = OffsetDateTime.now();
    }

    @PreUpdate
    protected void antesDeAtualizar() {
        this.atualizadoEm = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getNomeOrgao() {
        return nomeOrgao;
    }

    public void setNomeOrgao(String nomeOrgao) {
        this.nomeOrgao = nomeOrgao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public String toString() {
        return codigoOrgao+dataReferencia+descricao+valor;
    }
}
