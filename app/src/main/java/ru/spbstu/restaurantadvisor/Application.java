package ru.spbstu.restaurantadvisor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.spbstu.restaurantadvisor.config.AppConfig;

public class Application {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("----- App started -----");

        var prefService = ctx.getBean(ru.spbstu.restaurantadvisor.service.preference.PreferenceService.class);
        prefService.addPreference(111L, "vegan");
        System.out.println(prefService.getPreferences(111L));

        ctx.close();
    }
}