package com.project.kinofinder; //объявление пакета, к кот. принадлежит класс

import ch.qos.logback.classic.Logger;
import com.project.kinofinder.service.MovieService;
import com.project.kinofinder.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication  //аннотация, объединяющая несколько других аннотаций
//класс реализует интерфейс CommandLineRunner, который позволяет выполнять код после завершения запуска приложения.
public class KinofinderApplication implements CommandLineRunner {

    //объявляем поля класса
    private final MovieService movieService;
    private final UserService userService;
    private final Menu menu;

    //конструктор
    public KinofinderApplication(MovieService movieService, Menu menu,  UserService userService) {
        this.menu = menu;
        this.movieService = movieService;
        this.userService = userService;
    }


    //объявляем метод main
    public static void main(String[] args) {
        SpringApplication.run(KinofinderApplication.class, args);   //запуск приложения SpringBoot
    }


    @Override   //переопределяем метод из интерфейса CommandLineRunner
    public void run(String... args) throws Exception {
        menu.mainMenu();

    }
}
