package com.project.shorturlservice.controller;

import com.project.shorturlservice.controller.dto.ExceptionResponse;
import com.project.shorturlservice.controller.dto.FindLongUrlResponse;
import com.project.shorturlservice.service.FindLink;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @ApiResponse(responseCode = "200", description = "Успех",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FindLongUrlResponse.class),
                    examples = {
                            @ExampleObject(name = "exampleResponse", value = """
                                    {
                                        "longUrl": "https://www.gismeteo.ru/weather-novokuznetsk-4721/month/"
                                    }""")
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

    @GetMapping("/long/{shortUrl}")
    public ResponseEntity<FindLongUrlResponse> getLongLink(@PathVariable("shortUrl") String shortUrl) {
        return ResponseEntity.ok(new FindLongUrlResponse(findLink.getLongUrl(shortUrl)));
    }
}
