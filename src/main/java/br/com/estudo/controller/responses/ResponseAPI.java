package br.com.estudo.controller.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ResponseAPI {

	private int httpStatusCode;
	private List<String> errors;
	private String message;
	private Object data;
}
