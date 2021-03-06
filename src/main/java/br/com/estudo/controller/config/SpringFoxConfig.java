package br.com.estudo.controller.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.LoginEndpointBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Profile("homolog")
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
				.securitySchemes(securityScheme())
		        .securityContexts(securityContext())		
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
	
	private List<SecurityContext> securityContext() {
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(SecurityReference.builder().reference("oauth2").scopes(scopes().toArray(new AuthorizationScope[]{})).build());
        SecurityContext context = SecurityContext.builder().forPaths(Predicates.alwaysTrue()).securityReferences(securityReferences).build();
        return Arrays.asList(context);
    }

    private List<? extends SecurityScheme> securityScheme() {
        LoginEndpoint login = new LoginEndpointBuilder().url("http://localhost:8080/auth/realms/ClientesAPI/protocol/openid-connect/auth").build();

        List<GrantType> grantTypes = new ArrayList<>();
        grantTypes.add(new ImplicitGrant(login, "acces_token"));

        return Arrays.asList(new OAuth("oauth2", scopes(), grantTypes));
    }

    private List<AuthorizationScope> scopes() {
        List<AuthorizationScope> scopes = new ArrayList<>();
        return scopes;
    }
	
	private List<ResponseMessage> getGlobalResponseMessage() {
		List<ResponseMessage> responses = new ArrayList<ResponseMessage>();
		responses.add(new ResponseMessageBuilder().code(400).message("Erro do cliente!").build());
		responses.add(new ResponseMessageBuilder().code(401).message("Não autorizado!").build());
		responses.add(new ResponseMessageBuilder().code(500).message("Erro interno!").responseModel(new ModelRef("string")).build());
		return responses;
	}
	
	@Bean
	public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
            .realm("ClientesAPI")
            .appName("swagger-ui")
            .clientId("clientes-api-spring-boot")
            .clientSecret("cb08b0e8-13ee-442d-bdf5-4b72da1ed58d")
            .scopeSeparator(" ")
            .build();
	}
}
