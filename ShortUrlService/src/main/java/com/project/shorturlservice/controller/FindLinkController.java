package com.project.shorturlservice.controller;

import com.project.shorturlservice.controller.dto.*;
import com.project.shorturlservice.service.FindLink;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FindLinkController {

    public final FindLink findLink;


    @Operation(summary = "Найти длинную ссылку",
            description = "Endpoint для поиска длинной ссылки",
            tags = {"Поиск длинной ссылки"})
    @RequestBody( content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(type = "object",
                    properties = {
                            @StringToClassMapItem(key = "", value = FindShortUrlRequest.class),
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
                                      "uri": "/api/v1/shortUrl,string",
                                      "message": "Short URL shortUrl,string not found"
                                  }""")
                    }))

    @GetMapping("short/{shortUrl}")
    public ResponseEntity<FindLongUrlResponse> getLongLink(@Valid @PathVariable @Parameter(hidden = true)
                                                               FindShortUrlRequest shortUrl) {
        return ResponseEntity.ok(new FindLongUrlResponse(findLink.getLongUrl(shortUrl.shortUrl())));
    }

    @Operation(summary = "Найти короткую ссылку",
            description = "Endpoint для поиска короткой ссылки",
            tags = {"Поиск короткой ссылки"})
    @RequestBody( content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(type = "object",
                    properties = {
                            @StringToClassMapItem(key = "", value = FindShortUrlRequest.class),
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
                    examples = {@ExampleObject(name = "ErrorResponse", value = """
                                  {
                                      "uri": "/api/v1/shortUrl,string",
                                      "message": "Short URL shortUrl,string not found"
                                  }""")
                    }))

    @GetMapping("long/{longUrl}")
    public ResponseEntity<FindShortUrlResponse> getShortLink(@Valid @PathVariable @Parameter(hidden = true)
                                                                 FindLongUrlRequest longUrl) {
        return ResponseEntity.ok(new FindShortUrlResponse(findLink.getShortUrl(longUrl.longUrl())));
    }
}
