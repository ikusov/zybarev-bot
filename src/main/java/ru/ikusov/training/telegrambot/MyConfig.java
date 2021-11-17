package ru.ikusov.training.telegrambot;

import com.google.j2objc.annotations.Property;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.ikusov.training.telegrambot")
//@PropertySource("classpath:bot.properties")
@EnableAspectJAutoProxy
public class MyConfig {
}
