package com.project.shorturlservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "urls", indexes = @Index(name = "idx_short_url", columnList = "short_url"))
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @URL
    @Column(nullable = false, unique = true)
    private String longUrl;
    @NotNull
    @Column(nullable = false, unique = true)
    private String shortUrl;
    @Column(nullable = false)
    private ZonedDateTime dateTime;

    public Url(String longUrl, String shortUrl, ZonedDateTime dateTime) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.dateTime = dateTime;
    }
}
