package br.com.estudo.controller.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@Autowired
	private MessageSource mensagemSource;

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String handle(MethodArgumentNotValidException ex) {

//		List<RetornoErroParametroEntradaVO> erroEntrada = new ArrayList<RetornoErroParametroEntradaVO>();
//		List<FieldError> campoErro = ex.getBindingResult().getFieldErrors();
//
//		campoErro.forEach(e -> {
//			String mensagem = mensagemSource.getMessage(e, LocaleContextHolder.getLocale());
//			RetornoErroParametroEntradaVO retornoErro = new RetornoErroParametroEntradaVO(e.getField(), mensagem);
//			erroEntrada.add(retornoErro);
//		});
//
//		return erroEntrada;
		
		return "";
	}
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public String handler(MethodArgumentTypeMismatchException ex) {
		return "Paramentro Invalido";
	}
}
