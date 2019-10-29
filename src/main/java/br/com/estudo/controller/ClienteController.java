package br.com.estudo.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudo.dao.entity.ClienteEntity;
import br.com.estudo.dao.repository.ClienteRepository;

@RestController
@RequestMapping("/cliente")
@CrossOrigin
public class ClienteController {

	private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

	@Autowired
	private ClienteRepository clienteRepository;

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable(value = "id") Long id) {
		try {
			logger.info("buscando cliente por id: {}", id);
			Optional<ClienteEntity> cliente = clienteRepository.findById(id);

			if (cliente.isPresent()) {
				return new ResponseEntity<>(cliente.get(), HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			String message = "Falha na busca de cliente por id";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/batch")
	public ResponseEntity<?> findByIdIn(@RequestParam(required = true, value = "ids") List<Long> ids) {
		try {
			logger.info("buscando cliente por ids: {}", ids);
			List<ClienteEntity> clientes = clienteRepository.findAllById(ids);
			return new ResponseEntity<>(clientes, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha na busca de clientes por ids";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<?> findAll(@PageableDefault(page = 0, size = 5, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
		try {
			logger.info("buscando clientes");
			Page<ClienteEntity> clientes = clienteRepository.findAll(pageable);

			return new ResponseEntity<>(clientes.getContent(), HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha na busca de clientes";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/endereco/{idEndereco}")
	public ResponseEntity<?> getClienteByIdEndereco(@PathVariable(value = "idEndereco") Long idEndereco) {
		try {
			logger.info("buscando clientes por id endereco: {}", idEndereco);
			List<ClienteEntity> clientes = clienteRepository.findByIdEndereco(idEndereco);

			return new ResponseEntity<>(clientes, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha na busca de clientes por id endereco";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	public ResponseEntity<?> save(@Valid @RequestBody ClienteEntity clienteEntity) {
		try {
			logger.info("criando novo cliente");
			clienteRepository.save(clienteEntity);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			String message = "Falha ao criar novo cliente";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @RequestBody ClienteEntity clienteInput) {
		try {
			Optional<ClienteEntity> opCliente = clienteRepository.findById(id);
			if (!opCliente.isPresent())
				return ResponseEntity.notFound().build();

			ClienteEntity cliente = opCliente.get();
			
			if(clienteInput.getNome() != null && !clienteInput.getNome().isEmpty()) {
				cliente.setNome(clienteInput.getNome());
			}
			
			if(clienteInput.getIdade() != null) {
				cliente.setIdade(clienteInput.getIdade());
			}
			
			if(clienteInput.getIdEndereco() != null) {
				cliente.setIdEndereco(clienteInput.getIdEndereco());
			}
			
			clienteRepository.save(cliente);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha ao atualizar cliente";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		try {
			clienteRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha ao remover cliente";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
