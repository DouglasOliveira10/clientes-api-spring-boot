package br.com.estudo.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "endereco")
public class EnderecoEntity {

	@Id
	
	@SequenceGenerator(name="endereco_seq", initialValue=4, allocationSize=100)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endereco_seq")
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
