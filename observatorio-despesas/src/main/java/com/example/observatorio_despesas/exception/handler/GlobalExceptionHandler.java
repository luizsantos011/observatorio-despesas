package com.example.observatorio_despesas.exception.handler;

import com.example.observatorio_despesas.exception.business.DespesaDuplicadaException;
import com.example.observatorio_despesas.exception.dto.ErroValidacao;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DespesaDuplicadaException.class)
    public ProblemDetail handleDespesaDuplicadaException(DespesaDuplicadaException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(409),
                e.getMessage()
        );
        problemDetail.setTitle("Despesa Duplicada");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleDTOValidation(MethodArgumentNotValidException e) {
        List<ErroValidacao> erros = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroValidacao(erro.getField(), erro.getDefaultMessage()))
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(400),
                "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente."
        );
        problemDetail.setTitle("Erro de validação");
        problemDetail.setProperty("erros", erros);
        return problemDetail;
    }
}
