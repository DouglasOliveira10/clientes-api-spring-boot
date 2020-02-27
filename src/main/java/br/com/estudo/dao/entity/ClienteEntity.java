package br.com.estudo.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "cliente")
public class ClienteEntity {

	@Id
	@SequenceGenerator(name="cliente_seq", initialValue=100, allocationSize=100)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cliente_seq")
	private Long id;
	private String nome;
	private Integer idade;
	private Long idEndereco;
	
}
