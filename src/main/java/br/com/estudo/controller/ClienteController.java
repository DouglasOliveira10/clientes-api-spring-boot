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

import br.com.estudo.controller.responses.ResponseAPI;
import br.com.estudo.controller.responses.ResponseItems;
import br.com.estudo.dao.entity.ClienteEntity;
import br.com.estudo.dao.repository.ClienteRepository;
import br.com.estudo.exception.ClienteException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/clientes")
@CrossOrigin
public class ClienteController {

	private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

	@Autowired
	private ClienteRepository clienteRepository;

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Busca de cliente por id", response = ClienteEntity.class)
	public ResponseAPI findById(@PathVariable(value = "id") Long id) throws ClienteException {
		logger.info("buscando cliente por id: {}", id);
		Optional<ClienteEntity> cliente = clienteRepository.findById(id);

		if (!cliente.isPresent())
			throw new ClienteException("Cliente não encontrado!");
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(cliente.get())
				.build();
	}

	@GetMapping("/batch")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Lista clientes por ids", responseContainer = "List", response = ClienteEntity.class)
	public ResponseAPI findByIdIn(@RequestParam(required = true, value = "ids") List<Long> ids) {
		logger.info("buscando cliente por ids: {}", ids);
		List<ClienteEntity> clientes = clienteRepository.findAllById(ids);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(clientes)
				.build();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Lista clientes", responseContainer = "List", response = ClienteEntity.class)
	public ResponseAPI findAll(
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false, defaultValue = "id") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
		
		logger.info("buscando clientes");
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
		Page<ClienteEntity> pageClientes = clienteRepository.findAll(pageRequest);

		ResponseItems items = ResponseItems.builder()
				.items(pageClientes.getContent())
				.pageNumber(pageClientes.getNumber())
				.pageSize(pageClientes.getTotalPages())
				.totalSize(pageClientes.getTotalElements())
				.build();

		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(items)
				.build();
	}

	@GetMapping("/endereco/{idEndereco}")
	@ApiOperation(value = "Lista clientes por id do endereco", responseContainer = "List", response = ClienteEntity.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseAPI getClienteByIdEndereco(@PathVariable(value = "idEndereco") Long idEndereco) {
		logger.info("buscando clientes por id endereco: {}", idEndereco);
		List<ClienteEntity> clientes = clienteRepository.findByIdEndereco(idEndereco);

		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(clientes)
				.build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Insere um novo cliente", response = ClienteEntity.class)
	public ResponseAPI save(@Valid @RequestBody ClienteEntity clienteEntity) {
		logger.info("criando novo cliente");
		ClienteEntity entity = clienteRepository.save(clienteEntity);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.CREATED.value())
				.data(entity)
				.build();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Atualiza um cliente", response = ClienteEntity.class)
	public ResponseAPI update(@PathVariable(value = "id") Long id, @RequestBody ClienteEntity clienteInput) throws ClienteException {
		logger.info("atualizando cliente com id {}", id);
		
		Optional<ClienteEntity> opCliente = clienteRepository.findById(id);
		if (!opCliente.isPresent())
			throw new ClienteException("Cliente não encontrado!");

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
		
		ClienteEntity entity = clienteRepository.save(cliente);
		
		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.data(entity)
				.build();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Remove um cliente por id")
	public ResponseAPI delete(@PathVariable("id") Long id) {
		logger.info("removendo cliente com id {}", id);
		clienteRepository.deleteById(id);

		return ResponseAPI.builder()
				.httpStatusCode(HttpStatus.OK.value())
				.build();
	}

}
