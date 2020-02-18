package br.com.estudo.controller.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan("br.com.estudo.controller")
public class SpringFoxConfig {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.apis(RequestHandlerSelectors.basePackage("br.com.estudo.controller"))
					.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, getGlobalResponseMessage())
				.globalResponseMessage(RequestMethod.POST, getGlobalResponseMessage())
				.globalResponseMessage(RequestMethod.PUT, getGlobalResponseMessage())
				.globalResponseMessage(RequestMethod.DELETE, getGlobalResponseMessage())
				.globalResponseMessage(RequestMethod.PATCH, getGlobalResponseMessage());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Clientes API")
				.description("API responsavel pela administração de clientes e seus endereços")
				.version("1.0.0")
				.contact(new Contact("Douglas Oliveira", "https://github.com/DouglasOliveira10/clientes-api-spring-boot", ""))
				.build();
	}
	
	private List<ResponseMessage> getGlobalResponseMessage() {
		List<ResponseMessage> responses = new ArrayList<ResponseMessage>();
		responses.add(new ResponseMessageBuilder().code(400).message("Erro do cliente!").build());
		responses.add(new ResponseMessageBuilder().code(401).message("Não autorizado!").build());
		responses.add(new ResponseMessageBuilder().code(500).message("Erro interno!").responseModel(new ModelRef("string")).build());
		return responses;
	}
}
