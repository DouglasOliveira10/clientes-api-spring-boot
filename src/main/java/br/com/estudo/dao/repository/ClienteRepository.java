package br.com.estudo.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudo.dao.entity.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

	List<ClienteEntity> findByIdEndereco(Long idEndereco);
}
