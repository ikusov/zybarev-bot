package ru.ikusov.training.telegrambot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("ru.ikusov.training.telegrambot")
//@PropertySource("classpath:bot.properties")
@EnableAspectJAutoProxy
public class MyConfig {
}
