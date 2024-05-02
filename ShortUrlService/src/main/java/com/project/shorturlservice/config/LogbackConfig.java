package com.project.shorturlservice.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;
import com.github.loki4j.logback.JavaHttpSender;
import com.github.loki4j.logback.Loki4jAppender;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
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
        log.info("Loki url: " + lokiUrl);
        boolean isLokiDefined = lokiUrl != null && isLokiAvailable(lokiUrl);
        log.info("Loki is defined: " + isLokiDefined);
        if (isLokiDefined) {
            Loki4jAppender lokiAppender = new Loki4jAppender();
            lokiAppender.setContext(context);
            lokiAppender.setName("LOKI");

            JavaHttpSender sender = new JavaHttpSender();
            sender.setUrl(lokiUrl + "/loki/api/v1/push");
            sender.setConnectionTimeoutMs(3000); // Установка таймаута подключения
            sender.setRequestTimeoutMs(3000);    // Установка таймаута запроса

            log.info("Loki url: " + sender);
            lokiAppender.setHttp(sender);

            lokiAppender.start();
            log.info("Loki appender started");

            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(lokiAppender);
        } else {
            log.warn("Loki is not defined or not available" + lokiUrl);
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(fileAppender);
        }

        StatusPrinter.print(context);
    }

    private static boolean isLokiAvailable(String lokiUrl) {
        for (int i = 0; i < 6; i++) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(lokiUrl + "/ready");
                Thread.sleep(3000);
                HttpResponse response = httpClient.execute(request);
                log.info("Response: " + response.getStatusLine().toString() + " path: " + request.getURI().getPath());
                int statusCode = response.getStatusLine().getStatusCode();
                log.error("Status code: " + statusCode);
                if (statusCode >= 200 && statusCode < 300) {
                    return true;
                }
            } catch (InterruptedException e) {
                log.error("Thread was interrupted during sleep", e);
                return false; // Возвращаем false при прерывании потока
            } catch (Exception e) {
                log.warn("Unable to connect Loki: " + e.getMessage());
            }
        }
        return false;
    }
}
