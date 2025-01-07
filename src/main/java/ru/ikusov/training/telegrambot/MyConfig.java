package ru.ikusov.training.telegrambot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.Clock;

@Configuration
@ComponentScan("ru.ikusov.training.telegrambot")
@EnableAspectJAutoProxy
public class MyConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
