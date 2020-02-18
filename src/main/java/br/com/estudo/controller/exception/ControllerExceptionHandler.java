package br.com.estudo.controller.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.com.estudo.controller.responses.ResponseAPI;
import br.com.estudo.exception.ClienteException;
import br.com.estudo.exception.EnderecoException;

/**
 * Um resolver para exceções que serão retornadas para o client
 * nos controlllers rest. 
 * 
 * @author douglas.oliveira
 * @since 1.0.0
 * @version 1.0.0
 */
@RestControllerAdvice
public class ControllerExceptionHandler {	
	
	/**
	 * Tratamento para exceções do tipo cliente
	 * 
	 * @param ClienteException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(ClienteException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI clienteExceptionHandler(ClienteException ex) {
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage()) 
				.build();
	}
	
	/**
	 * Tratamento para exceções do tipo endereço
	 * 
	 * @param EnderecoException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(EnderecoException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI enderecoExceptionHandler(EnderecoException ex) {
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage()) 
				.build();
	}
	
	/**
	 * Tratamento para parametros não encontrados
	 * 
	 * @param MissingServletRequestParameterException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
		String message = ex.getParameterName() + " parametro não encontrado";
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message(message) 
				.build();
	}
		
	/**
	 * Tratamento para parametros com tipo errado
	 * 
	 * @param MethodArgumentTypeMismatchException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
		String message = ex.getName() + " deve ser do tipo " + ex.getRequiredType().getName();
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message(message) 
				.build();
	}
	
	/**
	 * Tratamento para parametros tratados com javax validation
	 * 
	 * @param MethodArgumentNotValidException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
		List<String> errors = new ArrayList<String>();
		
	    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
	        errors.add(error.getField() + ": " + error.getDefaultMessage());
	    }
	    
	    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
	        errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
	    }
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message("Campo invalido!") 
				.errors(errors)
				.build();
	}
	
	/**
	 * Tratamento para violações de restrições
	 * 
	 * @param ConstraintViolationException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseAPI constraintViolationExceptionHandler(ConstraintViolationException ex) {
		List<String> errors = new ArrayList<String>();
		
	    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
	        errors.add(violation.getRootBeanClass().getName() + " " + 
	          violation.getPropertyPath() + ": " + violation.getMessage());
	    }
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message("Restrição violada")
				.errors(errors)
				.build();
	}

	/**
	 * Tratamento para verbos acionado pelo cliente que não foi implantado
	 * 
	 * @param HttpRequestMethodNotSupportedException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseAPI httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
	    return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message("O verbo " +  ex.getMethod() + " não é suportado")
				.build();
	}
	
	/**
	 * Tratamento de HttpMessageNotReadableException
	 * 
	 * @param HttpMessageNotReadableException ex
	 * @return ResponseAPI
	 * @throws IOException 
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseAPI httpMessageNotReadableException(HttpMessageNotReadableException ex) throws IOException {
						
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage())
				.build();
	}
	
	/**
	 * Tratamento de violação de constraint
	 * 
	 * @param DataIntegrityViolationException ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseAPI dataIntegrityViolationException(DataIntegrityViolationException ex){
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.BAD_REQUEST.value())
				.message("Não foi possível executar a querie. Violação de constraint.")
				.build();
	}
	
	/**
	 * Tratamento para qualquer exceção não mapeada
	 * 
	 * @param Exception ex
	 * @return ResponseAPI
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseAPI exceptionHandler(Exception ex) {
		ex.printStackTrace();
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message(ex.getMessage())
				.build();
	}
}
