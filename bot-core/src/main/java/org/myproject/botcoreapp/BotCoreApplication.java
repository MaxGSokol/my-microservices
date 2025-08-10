package org.myproject.botcoreapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BotCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotCoreApplication.class, args);
    }

}
