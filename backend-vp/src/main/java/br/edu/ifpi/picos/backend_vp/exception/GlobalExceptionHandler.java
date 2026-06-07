package br.edu.ifpi.picos.backend_vp.exception;

import br.edu.ifpi.picos.backend_vp.dto.ErroRespostaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroRespostaDTO> handleRegrasDeNegocio(RuntimeException ex) {
       
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> handleErrosDeDigitacao(org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String mensagemLimpa = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        
        ErroRespostaDTO erro = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), mensagemLimpa);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}