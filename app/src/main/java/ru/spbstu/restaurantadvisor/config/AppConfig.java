package ru.spbstu.restaurantadvisor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.spbstu.restaurantadvisor")
@PropertySource("classpath:application.properties")
public class AppConfig {
    
}