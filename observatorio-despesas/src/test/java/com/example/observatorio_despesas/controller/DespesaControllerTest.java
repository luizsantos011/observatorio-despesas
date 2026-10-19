package com.example.observatorio_despesas.controller;

import com.example.observatorio_despesas.dto.DespesaRequestDTO;
import com.example.observatorio_despesas.dto.DespesaResponseDTO;
import com.example.observatorio_despesas.enums.Categoria;
import com.example.observatorio_despesas.exception.business.DespesaDuplicadaException;
import com.example.observatorio_despesas.exception.handler.GlobalExceptionHandler;
import com.example.observatorio_despesas.exception.resource.RecursoNaoEncontradoException;
import com.example.observatorio_despesas.service.DespesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class DespesaControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private DespesaService despesaService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        despesaService = mock(DespesaService.class);
        mockMvc = standaloneSetup(new DespesaController(despesaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void deveCriarDespesaComSucesso() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestValido();
        DespesaResponseDTO responseDTO = criarResponseValido();

        when(despesaService.criarDespesa(any(DespesaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(responseDTO.id().toString()));
    }

    @Test
    void deveRetornar400QuandoPayloadInvalido() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestInvalido();

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].campo").exists())
                .andExpect(jsonPath("$.erros[0].mensagem").exists());

        verify(despesaService, never()).criarDespesa(any());
    }

    @Test
    void deveRetornar409QuandoDespesaDuplicada() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestValido();

        when(despesaService.criarDespesa(any(DespesaRequestDTO.class)))
                .thenThrow(new DespesaDuplicadaException("Despesa já cadastrada"));

        mockMvc.perform(post("/api/v1/despesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar200QuandoBuscarPorId() throws Exception {
        DespesaResponseDTO responseDTO = criarResponseValido();

        when(despesaService.buscarDespesaPorId(any(UUID.class))).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/despesas/{id}", responseDTO.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.id().toString()))
                .andExpect(jsonPath("$.codigoOrgao").value(responseDTO.codigoOrgao()));

    }

    @Test
    void deveRetornar404QuandoBuscaIdNaoEncontrado() throws Exception {
        when(despesaService.buscarDespesaPorId(any(UUID.class))).thenThrow(new RecursoNaoEncontradoException("Recurso não encontado"));

        mockMvc.perform(get("/api/v1/despesas/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar200QuandoListar() throws Exception{
        List<DespesaResponseDTO> conteudo = List.of(criarResponseValido(), criarResponseValido());
        Pageable pageable = PageRequest.of(0, 10);
        Page<DespesaResponseDTO> page = new PageImpl<>(conteudo, pageable, conteudo.size());

        when(despesaService.listarDespesas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/despesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(conteudo.size()))
                .andExpect(jsonPath("$.totalElements").value(conteudo.size()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void deveRetornar200QuandoAtualizarValido() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestValido();
        DespesaResponseDTO responseDTO = criarResponseValido();

        when(despesaService.atualizarDespesa(any(UUID.class), any(DespesaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/despesas/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar400QuandoPayloadAtualizarInvalido() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestInvalido();

        mockMvc.perform(put("/api/v1/despesas/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(despesaService, never()).atualizarDespesa(any(UUID.class), any(DespesaRequestDTO.class));
    }

    @Test
    void deveRetornar404QuandoAtualizarNaoEncontrado() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestValido();

        when(despesaService.atualizarDespesa(any(UUID.class), any(DespesaRequestDTO.class))).thenThrow(new RecursoNaoEncontradoException("Recurso não encontrado"));

        mockMvc.perform(put("/api/v1/despesas/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409QuandoAtualizarConflita() throws Exception {
        DespesaRequestDTO requestDTO = criarRequestValido();

        when(despesaService.atualizarDespesa(any(UUID.class), any(DespesaRequestDTO.class))).thenThrow(new DespesaDuplicadaException("Os parâmetros informados geram duplicata"));

        mockMvc.perform(put("/api/v1/despesas/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar204QuandoDeletarSucesso() throws Exception {
        mockMvc.perform(delete("/api/v1/despesas/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404QuandoDeletarNaoEncontrado() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Recurso não encontrado")).when(despesaService).deletarDespesa(any(UUID.class));

        mockMvc.perform(delete("/api/v1/despesas/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private DespesaRequestDTO criarRequestValido() {
        return new DespesaRequestDTO(
                "123456789", "orgaoExemplo", Categoria.OUTROS, "testando", BigDecimal.valueOf(100.0), LocalDate.now()
        );
    }

    private DespesaResponseDTO criarResponseValido() {
        UUID uuid = UUID.randomUUID();
        return new DespesaResponseDTO(
                uuid, "123456789", "orgaoExemplo", Categoria.OUTROS, "testando", BigDecimal.valueOf(100.0),
                LocalDate.now(), OffsetDateTime.now(), OffsetDateTime.now()
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