package com.project.shorturlservice.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.loki4j.logback.Loki4jAppender;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbackConfig {

    @PostConstruct
    public void init() {
        configureLogback();
    }

    public static void configureLogback() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("ERROR_FILE");
        fileAppender.setFile("logs/error.log");
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(context);
        fileEncoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} - %level - %msg%n");
        fileEncoder.start();
        fileAppender.setEncoder(fileEncoder);
        fileAppender.start();

        String lokiUrl = System.getenv("LOKI");

        boolean isLokiDefined = lokiUrl != null && isLokiAvailable(lokiUrl);

        if (isLokiDefined) {
            Loki4jAppender lokiAppender = new Loki4jAppender();
            lokiAppender.setContext(context);
            lokiAppender.setName("LOKI");
            lokiAppender.start();

            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(lokiAppender);
        } else {
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(fileAppender);
        }

        StatusPrinter.print(context);
    }

    private static boolean isLokiAvailable(String lokiUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(lokiUrl + "/ready");
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode >= 200 && statusCode < 300;
        } catch (Exception e) {
            System.out.println("Unable to connect Loki: " + e.getMessage());
            return false;
        }
    }
}
