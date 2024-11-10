package com.project.kinofinder.service; //Определяем принадлежность класса к пакету

import com.project.kinofinder.model.Movie;
import com.project.kinofinder.model.User;
import com.project.kinofinder.repository.MovieRepository;
import com.project.kinofinder.repository.UserRepository;
import org.springframework.stereotype.Service;  //Подключение аннотации Service для Spring

import java.util.Set;

@Service        //Аннотация Service, указывающая, что класс являетя сервисом, кот. будет управляться Spring
public class UserService {

    //Объявляем поля класса
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    //Конструктор(инъекция зависимостей, позволяющая Spring автоматически передавать нужные объекты при создании сервиса)
    public UserService(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    //Получение всех пользователей
    public Set<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    //получение пользователя по имени
    public User getUserByName(String name) {
        return userRepository.getUserByName(name);
    }

    //сохранение нового пользователя
    public void save(User newUser) {
        userRepository.save(newUser);
    }

    //получение списка просмотренных фильмов пользователем
    public Set<Movie> getWatchedByUser(String currentUser) {
        User user = userRepository.getUserByName(currentUser);
        return user.getWatchedList();
    }

    //добавление фильма в список просмотра
    public void addMovieToWatchlist(User user, Movie movie) {
        user.addMovieToWatchlist(movie);
        userRepository.save(user);
    }

    //добавление в избранное
    public void addMovieToFavorites(User user, Movie movie) {
        user.addMovieToFavorites(movie);
        userRepository.save(user);
    }

    //добавление фильма в просмотренные
    public void addMovieToWatched(User user, Movie movie) {
        user.addMovieToWatched(movie);
        userRepository.save(user);
    }

    //удаление пользователя
    public boolean deleteUser(User user) {
        if (user == null) {     //проверяем, сущ. ли юзер
            return false;
        }
        userRepository.delete(user);
        return true;
    }

}
