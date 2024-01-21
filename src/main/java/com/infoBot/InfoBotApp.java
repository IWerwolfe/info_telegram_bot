package com.infoBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfoBotApp {

    public static void main(String[] args) {
        SpringApplication.run(InfoBotApp.class, args);
    }
}
