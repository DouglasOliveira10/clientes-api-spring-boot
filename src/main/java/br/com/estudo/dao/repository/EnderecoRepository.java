package br.com.estudo.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudo.dao.entity.EnderecoEntity;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {

}
