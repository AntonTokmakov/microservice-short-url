package com.project.shorturlservice.controller;

import com.project.shorturlservice.controller.dto.ExceptionResponse;
import com.project.shorturlservice.controller.dto.FindLongUrlResponse;
import com.project.shorturlservice.controller.dto.FindShortUrlResponse;
import com.project.shorturlservice.service.FindLink;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8090/api/v1", description = "API Gateway")
        }
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/find")
public class FindLinkController {

    private final FindLink findLink;

    @Operation(summary = "Найти длинную ссылку",
            description = "Endpoint для поиска длинной ссылки",
            tags = {"Поиск длинной ссылки"})
    @RequestBody( content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(type = "object",
                    properties = {
                            @StringToClassMapItem(key = "shortUrl", value = String.class),
                    }),
            examples = {
                    @ExampleObject(name = "exampleRequest",
                            value = "{\"shortUrl\": \"http://localhost:8090/api/v1/Yic-yBB_\"}",
                            description = "Запрос с корректными данными"),
                    @ExampleObject(name = "ErrorRequest",
                            value = "{\"shortUrl\": \"Какой-то текст\"}",
                            description = "Запрос с некорректными данными"),
            }))
    @ApiResponse(responseCode = "200", description = "Успех",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = FindLongUrlResponse.class),
            examples = {
                    @ExampleObject(name = "exampleResponse", value = """
                                        {
                                            "longUrl": "https://www.gismeteo.ru/weather-novokuznetsk-4721/month/"
                                        }""")
            }))
    @ApiResponse(responseCode = "400", description = "Ошибка в запросе",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ErrorResponse",
                            value = """
                                  {
                                        "uri": "/api/v1/generate",
                                        "message": "Error, field longUrl is not valid"
                                    }""",
                            description = "Переданная строка не является ссылкой")
                    }))
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ErrorResponse",
                            value = """
                            {
                                "timestamp": "2024-04-26 03:20:10",
                                "path": "/api/v1/qcRl3T-o",
                                "message": "Short URL http://localhost:8090/api/v1/qcRl3T-o not found",
                                "status": "NOT_FOUND"
                            }""")
                    }))

    @GetMapping("/long")
    public ResponseEntity<FindLongUrlResponse> getLongLink(@RequestParam("shortUrl")
                                                           @URL(message = "Error, field shortUrl is not valid")
                                                           @NotBlank(message = "Error, field shortUrl cannot is empty")
                                                           String shortUrl) {
        return ResponseEntity.ok(new FindLongUrlResponse(findLink.getLongUrl(shortUrl)));
    }

    @Operation(summary = "Найти короткую ссылку",
            description = "Endpoint для поиска короткой ссылки",
            tags = {"Поиск короткой ссылки"})
    @RequestBody( content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(type = "object",
                    properties = {
                            @StringToClassMapItem(key = "longUrl", value = String.class),
                    }),
            examples = {
                    @ExampleObject(name = "exampleRequest",
                            value = "{\"shortUrl\": \"http://localhost:8090/api/v1/Yic-yBB_\"}",
                            description = "Запрос с корректными данными"),
                    @ExampleObject(name = "ErrorRequest",
                            value = "{\"shortUrl\": \"Какой-то текст\"}",
                            description = "Запрос с некорректными данными"),
            }))
    @ApiResponse(responseCode = "200", description = "Успех",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = FindShortUrlResponse.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка в запросе",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse",
                            value = """
                                    {
                                        "timestamp": "2024-04-26 03:21:39",
                                        "path": "/api/v1/find/short",
                                        "message": "Error, field longUrl is not valid",
                                        "status": "BAD_REQUEST"
                                    }""",
                            description = "Переданная строка не является ссылкой")
                    }))
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse", value = """
                            {
                                "timestamp": "2024-04-26 10:33:32",
                                "path": "/api/v1/find/short",
                                "message": "Short URL http://localhost:8090/api/v1/ораорм not found",
                                "status": "NOT_FOUND"
                            }""")
                    }))

    @GetMapping("/short")
    public ResponseEntity<FindShortUrlResponse> getShortLink(@RequestParam("longUrl")
                                                            @URL(message = "Error, field longUrl is not valid")
                                                            @NotBlank(message = "Error, field longUrl cannot is empty")
                                                            String longUrl) {
        return ResponseEntity.ok(new FindShortUrlResponse(findLink.getShortUrl(longUrl)));
    }
}
