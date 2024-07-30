package ru.sparural.rest.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @Api: украсить весь класс и описать роль контроллера
 * @ApiOperation: описать метод класса или интерфейс
 * @ApiParam: описание одного параметра
 * @ApiModel: использовать объекты для получения параметров
 * @ApiProperty: при получении параметров с объектом, опишите поле объекта
 * @ApiResponse: 1 описание ответа HTTP
 * @ApiResponses: общее описание ответа HTTP.
 * @ApiIgnore: используйте эту аннотацию, чтобы игнорировать этот API
 * @ApiError: информация, возвращаемая при возникновении ошибки
 * @ApiImplicitParam: параметр запроса
 * @ApiImplicitParams: несколько параметров запроса
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${rest.version}")
    private String restVersion;

    @Value("${rest.base-url}")
    private String restBaseUrl;

    /**
     * apiInfo () добавляет информацию, связанную с API
     * Верните экземпляр ApiSelectorBuilder с помощью функции select (), чтобы контролировать, какие интерфейсы отображаются в Swagger для отображения,
     * В этом примере для определения каталога, в котором будет создан API, используется указанный путь к отсканированному пакету.
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * основная информация API
     * Адрес для посещения: http://host:port/swagger-ui.html
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SPAR RESTful API")
                .description(
                        String.format("Base url path: %s/%s", restBaseUrl, restVersion))
                .version(restVersion)
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }
}