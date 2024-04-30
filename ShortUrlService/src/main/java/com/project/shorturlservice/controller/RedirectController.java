package com.project.shorturlservice.controller;

import com.project.shorturlservice.controller.dto.ExceptionResponse;
import com.project.shorturlservice.service.RedirectService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8090/api/v1", description = "API Gateway")
        }
)
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;

    @Operation(summary = "Переход по короткой ссылке",
            description = "Endpoint для перехода по короткой ссылке",
            tags = {"Переход по короткой ссылке"})
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
    @ApiResponse(responseCode = "302", description = "Переход на сайт по кототкой ссылке",
            content = @Content(mediaType = MediaType.TEXT_HTML_VALUE,
                    schema = @Schema(implementation = String.class),
                    examples = {@ExampleObject(name = "ExampleResponse", value = """
                            <!doctype html>\s
                            <html xmlns="http://www.w3.org/1999/xhtml" prefix="og:\s
                            https://ogp.me/ns#" xml:lang="ru" lang="ru" class="html-month">

                              <head>
                                <meta charset="UTF-8">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <meta name="viewport"
                             ......"""),

    }))
    @ApiResponse(responseCode = "400", description = "Ошибка в запросе",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse",
                                    value = """
                            {
                                "timestamp": "2024-04-26 03:21:39",
                                "path": "/api/v1/qcRl3T-o",
                                "message": "Short URL http://localhost:8080/api/v1/qcRl3T-o not found",
                                "status": "NOT_FOUND"
                            }""",
                                    description = "Переданная строка не является ссылкой")
    }))
    @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse", value = """
                            {
                                "timestamp": "2024-04-26 03:20:10",
                                "path": "/api/v1/qcRl3T-o",
                                "message": "Short URL http://localhost:8080/api/v1/qcRl3T-o not found",
                                "status": "NOT_FOUND"
                            }""")
    }))
    @ApiResponse(responseCode = "410", description = "Ссылка устарела",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {@ExampleObject(name = "ExampleResponse", value = """
                            {
                                "timestamp": "2024-04-26 02:38:54",
                                "path": "/api/v1/qcRl3T-o",
                                "message": "Short URL http://localhost:8090/api/v1/qcRl3T-o expired",
                                "status": "GONE"
                            }""",
                                    description = "Ссылка является не действительной, " +
                                            "по истечении определенного времени")
    }))

    @GetMapping("{shortUrl}")
    public void redirect(@PathVariable String shortUrl, HttpServletResponse response) {
        redirectService.redirectTo(shortUrl, response);
    }
}