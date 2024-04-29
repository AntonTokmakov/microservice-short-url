package com.project.shorturlservice.controller;

import com.project.shorturlservice.controller.dto.ExceptionResponse;
import com.project.shorturlservice.controller.dto.GeneratedUrlRequest;
import com.project.shorturlservice.controller.dto.GeneratedUrlResponse;
import com.project.shorturlservice.service.GeneratorService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8090/api/v1", description = "API Gateway")
        }
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/generate")
public class GeneratedUrlController {

    private final GeneratorService generatorService;


    @Operation(summary = "Создание сокращенной ссылки",
            description = "Endpoint для создания сокращенной ссылки",
            tags = {"Генерация коротких ссылок"})

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(type = "object",
                    properties = {
                            @StringToClassMapItem(key = "longUrl", value = String.class),
                    }),
            examples = {
                    @ExampleObject(name = "ExampleRequest",
                            value = "{\"longUrl\": \"https://translated.turbopages.org/proxy_u/en-ru" +
                                    ".ru.8e1ae170-6624c6a1-e759bf37-74722d776562/https/stackoverflow" +
                                    ".com/?__ya_mt_enable_static_translations=1\"}",
                            description = "Запрос с корректными данными"),
                    @ExampleObject(name = "ErrorResponse", value = "{\"longUrl\": \"простоТекст\"}",
                            description = "Запрос с некорректными данными"),
            }))
    @ApiResponse(responseCode = "201", description = "Создана сокращенная ссылка",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse", value = """
                        {
                            "shortUrl": "http://localhost:8090/api/v1/eQwveHXA"
                        }""",
                            description = "В результате успешного запроса " +
                                    "создана короткая ссылка")
                    }))
    @ApiResponse(responseCode = "400", description = "Ошибка в запросе",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse",
                            value = """
                                {
                                    "uri": "/api/v1/generate",
                                    "message": "Error, field longUrl is not valid"
                                }""",
                            description = "Переданная строка не является ссылкой")
                    }))

    @PostMapping
    public ResponseEntity<GeneratedUrlResponse> generateShortUrl(@RequestBody @Valid GeneratedUrlRequest longUrl) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GeneratedUrlResponse(generatorService.generateShortUrl(longUrl.longUrl())));
    }
}