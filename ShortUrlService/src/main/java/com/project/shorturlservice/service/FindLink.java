package com.project.shorturlservice.service;

public interface FindLink {

    String getShortUrl(String longUrl);

    String getLongUrl(String shortUrl);


}
