package br.com.estudo.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import br.com.estudo.controller.responses.ResponseAPI;
import br.com.estudo.controller.responses.ResponseItems;
import br.com.estudo.dao.entity.EnderecoEntity;
import br.com.estudo.dao.repository.EnderecoRepository;
import br.com.estudo.exception.EnderecoException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/enderecos")
@CrossOrigin
public class EnderecoController {

	private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);

	@Autowired
	private EnderecoRepository enderecoRepository;

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Busca de endereco por id", response = EnderecoEntity.class)
	public ResponseAPI findById(@PathVariable(value = "id") Long id) throws EnderecoException {
		logger.info("buscando endereco por id: {}", id);
		Optional<EnderecoEntity> endereco = enderecoRepository.findById(id);

		if (!endereco.isPresent())
			throw new EnderecoException("Endereço não encontrado!");
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(endereco.get())
				.build();
	}

	@GetMapping("/batch")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Lista enderecos por ids", responseContainer = "List", response = EnderecoEntity.class)
	public ResponseAPI findByIdIn(@RequestParam(required = true, value = "ids") List<Long> ids) {
		logger.info("buscando endereco por ids: {}", ids);
		List<EnderecoEntity> enderecos = enderecoRepository.findAllById(ids);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(enderecos)
				.build();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Lista enderecos", responseContainer = "List", response = EnderecoEntity.class)
	public ResponseAPI findAll(
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "id") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
		
		logger.info("buscando enderecos");
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
		Page<EnderecoEntity> pageEnderecos = enderecoRepository.findAll(pageRequest);
		
		ResponseItems items = ResponseItems.builder()
				.items(pageEnderecos.getContent())
				.pageNumber(pageEnderecos.getNumber())
				.pageSize(pageEnderecos.getTotalPages())
				.totalSize(pageEnderecos.getTotalElements())
				.build();

		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(items)
				.build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Insere um novo endereco", response = EnderecoEntity.class)
	public ResponseAPI save(@Valid @RequestBody EnderecoEntity enderecoEntity) throws EnderecoException {
		logger.info("criando novo endereco");

		Optional<EnderecoEntity> opEndereco = getByCep(enderecoEntity.getCep());
		if (!opEndereco.isPresent())
			throw new EnderecoException("Falha ao encontrar endereco pelo cep informado!");

		EnderecoEntity enderecoEntityByCep = opEndereco.get();
		enderecoEntityByCep.setNumero(enderecoEntity.getNumero());
		enderecoEntityByCep.setComplemento(enderecoEntity.getComplemento());

		EnderecoEntity entity = enderecoRepository.save(enderecoEntityByCep);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.CREATED.value())
				.data(entity)
				.build();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Atualiza um endereco", response = EnderecoEntity.class)
	public ResponseAPI update(@PathVariable(value = "id") Long id, @RequestBody EnderecoEntity enderecoInput) throws EnderecoException {
		
		Optional<EnderecoEntity> opEndereco = enderecoRepository.findById(id);
		if (!opEndereco.isPresent())
			throw new EnderecoException("Endereço não encontrado!");

		EnderecoEntity endereco = opEndereco.get();

		if (enderecoInput.getNumero() != null) {
			endereco.setNumero(enderecoInput.getNumero());
			endereco.setComplemento(enderecoInput.getComplemento());
		}

		EnderecoEntity entity = enderecoRepository.save(endereco);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(entity)
				.build();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Remove um endereco por id")
	public ResponseAPI delete(@PathVariable("id") Long id) {
		enderecoRepository.deleteById(id);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.build();
	}
	
	@GetMapping("/cep/{cep}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Retorna os dados do endereço pelo cep")
	public ResponseAPI findDataByCep(@PathVariable("cep") String cep) throws EnderecoException {
		logger.info("buscando dados do endereco pelo cep: {}", cep);
		Optional<EnderecoEntity> endereco = getByCep(cep);

		if (!endereco.isPresent())
			throw new EnderecoException("Endereço não encontrado!");
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(endereco.get())
				.build();
	}
	
	private Optional<EnderecoEntity> getByCep(String cep) throws EnderecoException {		
		try {
			String urlViaCep = "http://viacep.com.br/ws/" + cep + "/json/";
			ResponseEntity<String> response = new RestTemplate().getForEntity(urlViaCep, String.class);
			
			EnderecoEntity enderecoEntity = new Gson().fromJson(response.getBody(), EnderecoEntity.class);
			return Optional.ofNullable(enderecoEntity);
		} catch (Exception e) {
			throw new EnderecoException("Falha ao encontrar endereço no VIACEP!");
		}
	}

}
