package com.project.kinofinder; //Код находится в пакете com.project.kinofinder.

import com.project.kinofinder.model.Movie;
import com.project.kinofinder.model.User;
import com.project.kinofinder.service.MovieService;
import com.project.kinofinder.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; //Аннотация @Component указывает Spring, что этот класс является компонентом, который можно использовать в других частях приложения.


import java.util.*;


@Component
public class Menu {

    private static String stopGenres = "Хоррор,Ужасы,Триллер"; //Жанры фильмов, запрещенные детям

    private static String currentUser; //Имя текущего пользователя

    //Сервисы для работы с фильмами и пользователями
    private final MovieService movieService;
    private final UserService userService;

    public static Scanner scanner = new Scanner(System.in);

    //Конструктор Menu
    public Menu(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    //Метод, запускаюий главное меню программы
    public void mainMenu() {
        currentUser = null;

        while (true) {
            if (currentUser == null) { //Если пользователь не вошел с систему, отображаем меню с 3 вариантами
                System.out.println("1. Зайти как пользователь\n2. Зайти как админ\n3. Выйти");

                int choice = inputChoice(scanner);

                switch (choice) {
                    case 1:
                        userLogin();
                        break;
                    case 2:
                        adminLogin();
                        break;
                    case 3:
                        System.out.println("Выход...");
                        return;
                    default:
                        System.out.println("Неверный выбор. Попробуйте еще раз.");
                }
            } else {
                userMenu();
            }
        }
    }

    //Запрашиваем логин у пользователя, проверяем на уже сущ. пользователя, если нет то создаем и сохряняем через userService
    private void userLogin() {
        System.out.println("Введите логин:");
        String name = inputStr(scanner);

        Set<User> users = userService.getAllUsers();

        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                currentUser = user.getName();
                System.out.println("Вход под логином " + name);
                return;
            }
        }

        System.out.println("Пользователь не найден. Создание нового пользователя");
        System.out.println("Введите возраст:");

        int age = inputChoice(scanner);
        System.out.println();
        String password = "test";

        Set<String> genres = inputGenres(scanner, age);

        User newUser = new User(name, age, password, genres);
        userService.save(newUser);
        currentUser = newUser.getName();
        System.out.println("Создан пользователь и выполнен вход: " + name);
    }

    //Метод, реализующий логику аутентификации администратора
    private void adminLogin() {
        System.out.println("Введите пароль администратора:");
        String password = inputStr(scanner);
        if ("admin".equals(password)) { //Проверка пароля
            Set<User> users = userService.getAllUsers();

            for (User user : users) {
                if (user.getName().equals("admin")) { //Проверка существования администратора
                    currentUser = user.getName();
                    System.out.println("Вход под администратором");
                    adminMenu();
                    return;
                }
            }
            User user = new User("admin", 30, "admin", Set.of("Комедия", "Драма", "Триллер")); //Создаем нового админа если вдруг админа не существует
            userService.save(user);
            currentUser = user.getName();
            adminMenu();
        } else {
            System.out.println("Неверный пароль.");
        }
    }


    private void userMenu() {
        if ("admin".equals(currentUser)) {  //Проверяем, юзера на админа, если да, вызываем админ. меню
            adminMenu();
            return;
        }
        //Если пользователь не админ:
        System.out.println("1. Показать рекомендации\n2. Добавить фильм в список просмотра\n3. Добавить фильм в избранное\n4. Удалить аккаунт\n5. Посмотреть и оценить фильм\n6. Выйти");
        int choice = inputChoice(scanner);

        switch (choice) {       //Используем оператор switch для обработки выбора пользователя
            case 1:
                viewRecommendations();
                break;
            case 2:
                addMovieToWatchlist();
                break;
            case 3:
                addMovieToFavorites();
                break;
            case 4:
                deleteUser();
                break;
            case 5:
                rateMovie();
                break;
            case 6:
                currentUser = null;  //Выход из системы
                break;
            default:
                System.out.println("Неправильный выбор. Попробуйте еще раз");
        }
    }

    //Меню админа
    private void adminMenu() {
        System.out.println("1. Удалить пользователя\n2. Добавить фильм\n3. Удалить фильм\n4. Показать рекомендации для всех пользователей\n5. Показать рейтинги всех фильмов\n6. Показать список пользователей\n7. Показать список фильмов\n8. Выход");
        int choice = inputChoice(scanner);

        switch (choice) {
            case 1:
                deleteUserByAdmin();
                break;
            case 2:
                addMovie();
                break;
            case 3:
                deleteMovie();
                break;
            case 4:
                viewRecommendationsForAllUsers();
                break;
            case 5:
                viewAllMovieRatings();
                break;
            case 6:
                listUsers();
                break;
            case 7:
                listMovies();
                break;
            case 8:
                currentUser = null;
                return;
            default:
                System.out.println("Неправильный выбор попробуйте еще раз");
        }
    }

    private void viewRecommendations() {        //Отображение рекомендаций фильмов для текущего юзера
        System.out.println("Рекомендации для " + currentUser + ":");
        User user = userService.getUserByName(currentUser); //Получаем объект пользователя
        Map<String, Set<Movie>> movies = movieService.getRecommendations(user); //Получаем рекомендации для этого юзера

        for (Map.Entry<String, Set<Movie>> entry : movies.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (Movie movie : entry.getValue()) {
                //фильтр по возрасту
                if (user.getAge() <= 16 && getStopGenresSet().contains(movie.getGenre()))  {
                    continue;
                }
                System.out.println("\tTitle: " + movie.getTitle() + ", Genre: " + movie.getGenre() + ", Year: " + movie.getYear() +
                        ", Director: " + movie.getDirector() + ", Rating: " + movie.getRating() + ", Earnings: $" + movie.getEarnings());
            }
        }

        System.out.println();

        if (movies.isEmpty()) {
            System.out.println("Вы уже все посмотрели, заходите попозже");
        }

    }

    private void addMovieToWatchlist() {        //Добавление фильмов в список просмотра
        System.out.println("Введите название фильма для добавления в список просмотра:");
        String title = inputStr(scanner);

        List<Movie> movies = movieService.findAll();
        User user = userService.getUserByName(currentUser);

        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title)) {
                userService.addMovieToWatchlist(user, movie);
                System.out.println("Фильм добавлен в список просмотра.");
                return;
            }
        }
        System.out.println("Фильм не найден.");
    }

    private void addMovieToFavorites() {        //Добавление фильма в избранное
        System.out.println("Введите название фильма для добавление в избранное:");
        String title = inputStr(scanner);


        User user = userService.getUserByName(currentUser);

        List<Movie> movies = movieService.findAll();

        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title)) {
                userService.addMovieToFavorites(user, movie);
                System.out.println("Фильм добавлен в избранное.");
                return;
            }
        }
        System.out.println("Фильм не найден.");
    }

    private void deleteUser() {

        User user = userService.getUserByName(currentUser);
        userService.deleteUser(user);
        System.out.println("Пользователь " + currentUser + " удален.");
        currentUser = null;
    }

    private void rateMovie() {
        System.out.println("Введите фильм для просмотра и оценки:");
        String title = inputStr(scanner);
        List<Movie> movies = movieService.findAll();


        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title)) {

                User user = userService.getUserByName(currentUser);
                userService.addMovieToWatched(user, movie);
                System.out.println("....смотрим фильм....");
                System.out.println("Введите рейтинг от 1 до 10");
                double rating = inputDouble(scanner);
                Movie ratedMovie = movieService.setRating(user, movie, rating);
                if (ratedMovie != null) {
                    System.out.println("Фильм оценен.");
                    System.out.println("Текущий рейтинг фильма: " + ratedMovie.getRating());
                    return;
                }
            }
        }


        System.out.println("Фильм не найден.");
    }

    private void deleteUserByAdmin() {
        System.out.println("Введите пользователя для удаления:");
        String name = inputStr(scanner);

        User userForDelete = userService.getUserByName(name);
        if (userService.deleteUser(userForDelete)) {
            System.out.println("Пользователь " + name + " удален.");
            return;
        }
        System.out.println("Пользователь не найден.");
    }

    private void addMovie() {
        System.out.println("Введите название фильма:");
        String title = inputStr(scanner);
        System.out.println("Введите жанр:");
        String genre = inputStr(scanner);
        System.out.println("Введите год:");
        int year = inputChoice(scanner);
        System.out.println("Введите режисера:");
        String director = inputStr(scanner);
        System.out.println("Введите рейтинг:");
        double rating = inputDouble(scanner);
        System.out.println("Введите предполагаемые сборы:");
        double earnings = inputDouble(scanner);

        Movie newMovie = new Movie(title, genre, year, director, rating, earnings);
        System.out.println(newMovie.toString());
        movieService.addMovie(newMovie);
        System.out.println("Фильм добавлен.");
    }

    private void deleteMovie() {
        System.out.println("Введите фильм для удаления:");
        String title = inputStr(scanner);

        boolean isDeleted = movieService.deleteMovieByTitile(title);

        if (isDeleted) {
            System.out.println("Фильм удален.");
        } else {
            System.out.println("Фильм не найден.");
        }

    }

    private void viewRecommendationsForAllUsers() {
        Set<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println("Рекомендации для " + user.getName() + ":");

            Map<String, Set<Movie>> movies = movieService.getRecommendations(user);
            for (Map.Entry<String, Set<Movie>> entry : movies.entrySet()) {
                System.out.println(entry.getKey() + ":");
                for (Movie movie : entry.getValue()) {
                    //фильтр по возрасту
                    if (user.getAge() <= 16 && getStopGenresSet().contains(movie.getGenre()))  {
                        continue;
                    }
                    System.out.println("\tTitle: " + movie.getTitle() + ", Genre: " + movie.getGenre() + ", Year: " + movie.getYear() +
                            ", Director: " + movie.getDirector() + ", Rating: " + movie.getRating() + ", Earnings: $" + movie.getEarnings());
                }
            }
        }
    }

    private void viewAllMovieRatings() {
        List<Movie> movies = movieService.findAll();
        for (Movie movie : movies) {
            System.out.println(movie.getTitle() + " - Рейтинг: " + movie.getRating());
        }
    }

    private void listUsers() {
        System.out.println("Список пользователей:");

        Set<User> users = userService.getAllUsers();
        for (User user : users) {
            System.out.println(user.getName() + ", Возраст: " + user.getAge());
        }
    }

    private void listMovies() {
        System.out.println("Список фильмов:");

        List<Movie> movies = movieService.findAll();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    //Далее идут методы для ввода данных пользователем, обеспечивающие защиту от некорретного ввода
    private int inputChoice(Scanner scanner) {
        int choice = 0;
        while (choice <= 0) {
            try {
                System.out.println("Введите число: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // очищаем буфер
                if (choice <= 0) {
                    System.out.println("Выбор должен быть положительным числом. Пожалуйста, попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Некорректный формат ввода. Пожалуйста, введите положительное число.");
                scanner.nextLine(); // очищаем буфер
            }
        }
        return choice;
    }

    private String inputStr(Scanner scanner) {
        String str = null;
        while (str == null) {
            try {
                System.out.println("Введите строку:");
                str = scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Некорректный формат ввода. Пожалуйста, попробуйте снова.");
                scanner.nextLine(); // очищаем буфер
            }
        }
        return str;
    }

    private double inputDouble(Scanner scanner) {
        double doubleValue = 0;
        while (doubleValue <= 0) {
            try {
                System.out.println("Введите число:");
                doubleValue = scanner.nextDouble();
                scanner.nextLine();
                if (doubleValue <= 0) {
                    System.out.println("Число должно быть положительным. Пожалуйста, попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Некорректный формат ввода. Пожалуйста, попробуйте снова.");
                scanner.nextLine();
            }
        }
        return doubleValue;
    }

    public static Set<String> inputGenres(Scanner scanner, int age) {
        Set<String> genres = new HashSet<>();
        String genre = null;

        while (true) {
            try {
                System.out.println("Введите любимый жанр (или 'Enter' для завершения ввода):");
                genre = scanner.nextLine();

                if (genre.trim().isEmpty()) {
                    break;
                }

                if(age <= 16 && getStopGenresSet().contains(genre))  {
                    System.out.println("Вы не можете выбрать этот жанр.");
                    continue;
                }
                genres.add(genre);

            } catch (InputMismatchException e) {
                System.out.println("Некорректный формат ввода. Пожалуйста, попробуйте снова.");
                scanner.nextLine();
            }
        }
        if (genres.isEmpty()) {
            genres = Set.of("Фантастика");
        }
        return genres;
    }

    public static Set<String> getStopGenresSet() {
        String property = stopGenres;
        String[] values = property.split(",");
        Set<String> stopGenresSet = new HashSet<>();
        for (String value : values) {
            stopGenresSet.add(value.trim());
        }
        return stopGenresSet;
    }
}
