package br.com.estudo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudo.controller.responses.ResponseAPI;
import br.com.estudo.exception.ClienteException;

@RestController
@RequestMapping("/metadata")
public class MetadataController {
	
	@GetMapping("/health")
	@ResponseStatus(HttpStatus.OK)
	public ResponseAPI health() throws ClienteException {
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data("up")
				.build();
	}
	
	@GetMapping("/version")
	@ResponseStatus(HttpStatus.OK)
	public ResponseAPI isAlive() throws ClienteException {
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data("1.0")
				.build();
	}

}
