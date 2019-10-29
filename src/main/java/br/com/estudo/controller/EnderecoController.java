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
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import br.com.estudo.dao.entity.EnderecoEntity;
import br.com.estudo.dao.repository.EnderecoRepository;

@RestController
@RequestMapping("/endereco")
@CrossOrigin
public class EnderecoController {

	private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);

	@Autowired
	private EnderecoRepository enderecoRepository;

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable(value = "id") Long id) {
		try {
			logger.info("buscando endereco por id: {}", id);
			Optional<EnderecoEntity> endereco = enderecoRepository.findById(id);

			if (endereco.isPresent()) {
				return new ResponseEntity<>(endereco.get(), HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			String message = "Falha na busca de endereco por id";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/batch")
	public ResponseEntity<?> findByIdIn(@RequestParam(required = true, value = "ids") List<Long> ids) {
		try {
			logger.info("buscando endereco por ids: {}", ids);
			List<EnderecoEntity> enderecos = enderecoRepository.findAllById(ids);
			return new ResponseEntity<>(enderecos, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha na busca de endereco por ids";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<?> findAll(@PageableDefault(page = 0, size = 5, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
		try {
			logger.info("buscando enderecos");
			Page<EnderecoEntity> enderecos = enderecoRepository.findAll(pageable);

			return new ResponseEntity<>(enderecos.getContent(), HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha na busca de enderecos";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	public ResponseEntity<?> save(@Valid @RequestBody EnderecoEntity enderecoEntity) {
		try {
			logger.info("criando novo endereco");
			
			Optional<EnderecoEntity> opEndereco = getByCep(enderecoEntity.getCep());
			if(!opEndereco.isPresent()) return new ResponseEntity<>("Falha ao encontrar endereco pelo cep informado", HttpStatus.BAD_REQUEST);
			
			EnderecoEntity enderecoEntityByCep = opEndereco.get();
			enderecoEntityByCep.setNumero(enderecoEntity.getNumero());
			enderecoEntityByCep.setComplemento(enderecoEntity.getComplemento());
			
			enderecoRepository.save(enderecoEntityByCep);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			String message = "Falha ao criar novo endereco";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @RequestBody EnderecoEntity enderecoInput) {
		try {
			Optional<EnderecoEntity> opEndereco = enderecoRepository.findById(id);
			if (!opEndereco.isPresent())
				return ResponseEntity.notFound().build();

			EnderecoEntity endereco = opEndereco.get();
			
			if(enderecoInput.getNumero() != null) {
				endereco.setNumero(enderecoInput.getNumero());
				endereco.setComplemento(enderecoInput.getComplemento());
			}
			
			enderecoRepository.save(endereco);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha ao atualizar endereco";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		try {
			enderecoRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			String message = "Falha ao remover endereco";
			logger.error(message, e);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Optional<EnderecoEntity> getByCep(String cep) {
		try {
			String urlViaCep = "http://viacep.com.br/ws/" + cep + "/json/";
			ResponseEntity<String> response = new RestTemplate().getForEntity(urlViaCep, String.class);
	
			EnderecoEntity enderecoEntity = new Gson().fromJson(response.getBody(), EnderecoEntity.class);
			return Optional.ofNullable(enderecoEntity);
		} catch (Exception e) {
			String message = "Falha ao buscar endereco no servico vaicep";
			logger.error(message, e);
			return Optional.empty();
		}
	}

}
