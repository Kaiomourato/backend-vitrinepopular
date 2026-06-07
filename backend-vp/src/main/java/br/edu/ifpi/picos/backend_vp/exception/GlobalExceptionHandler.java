package br.edu.ifpi.picos.backend_vp.exception;

import br.edu.ifpi.picos.backend_vp.dto.ErroRespostaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // [CORREÇÃO] Antes: toda RuntimeException virava 400, inclusive "não encontrado".
    // Agora: usamos exceções específicas para cada situação.
    // A convenção é simples — a mensagem de erro começa com um prefixo:
    //   "Erro de Segurança:" → 403 Forbidden
    //   "Erro: ... não encontrado" → 404 Not Found
    //   Qualquer outro "Erro:" → 400 Bad Request

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> handleRegrasDeNegocio(RuntimeException ex) {
        String mensagem = ex.getMessage();

        if (mensagem != null && mensagem.startsWith("Erro de Segurança:")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErroRespostaDTO(HttpStatus.FORBIDDEN.value(), mensagem));
        }

        if (mensagem != null && mensagem.toLowerCase().contains("não encontrad")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErroRespostaDTO(HttpStatus.NOT_FOUND.value(), mensagem));
        }

        // Fallback: erros de regra de negócio em geral
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), mensagem));
    }

    // Erros de validação dos campos (@NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> handleErrosDeDigitacao(MethodArgumentNotValidException ex) {
        String mensagemLimpa = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), mensagemLimpa));
    }

    // [NOVO] Captura tentativas de acesso sem permissão lançadas pelo Spring Security
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroRespostaDTO> handleAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErroRespostaDTO(HttpStatus.FORBIDDEN.value(), "Acesso negado: você não tem permissão para esta ação."));
    }
}