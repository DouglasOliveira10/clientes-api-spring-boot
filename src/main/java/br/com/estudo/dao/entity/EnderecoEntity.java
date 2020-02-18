package br.com.estudo.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "endereco")
public class EnderecoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	
	@NotNull @NotBlank
	private String cep;
	private String logradouro;
	private Integer numero;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;

}
