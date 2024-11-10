package com.project.kinofinder.service; //Классс принадлежит указанному пакету

import com.project.kinofinder.model.Movie;
import com.project.kinofinder.model.User;
import com.project.kinofinder.repository.MovieRepository;
import com.project.kinofinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service        //Аннотация Service, указывающая, что класс являетя сервисом, кот. будет управляться Spring
public class MovieService {


    //Объявляем поля класса
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    //Конструктор(инъекция зависимостей, позволяющая Spring автоматически передавать нужные объекты при создании сервиса)
    public MovieService(MovieRepository movieRepository, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }


    //ПОлучение фильма по заголовку
    public Movie getFilmByTitle(String title) {
        return movieRepository.getMovieByTitle(title);
    }

    //добавление нового фильма
    public void addMovie(Movie newMovie) {
        movieRepository.save(newMovie);
    }

    //удаление фильма по заголовку
    public boolean deleteMovieByTitile(String title) {
        if (getFilmByTitle(title) == null) {    //проверяем, существует ли фильм с заданным заголовком
            return false;
        }
        Movie movie = movieRepository.getMovieByTitle(title);
        movieRepository.delete(movie);
        return true;
    }

    //получение рекомендаций для пользователя
    public Map<String, Set<Movie>> getRecommendations(User user) {

        List<Movie> movies = movieRepository.findAll();

        List<Movie> moviesRecommendations = new ArrayList<>();

        //убираем просмотренные и не подходящие по жанру
        for (Movie movie : movies) {
            if (!user.getWatchedMovies().contains(movie) && (user.getGenres().contains(movie.getGenre()))) {
                moviesRecommendations.add(movie);
            }
        }

        //добавляем отложенные в список просмотра и в избранное
        Set<Movie> userFav = user.getFavorites();
        Set<Movie> userWatch = user.getWatchlist();

        Map<String, Set<Movie>> map = new HashMap<>();
        map.put("Избранное", userFav);
        map.put("Список просмотра", userWatch);
        map.put("Рекомендации", new HashSet<>(moviesRecommendations));

        return map;
    }

     public List<Movie> findAll() {

        return movieRepository.findAll();
     }

     //установка рейтинга фильма
    public Movie setRating(User user, Movie movie, double rating) {
        List<Movie> movies = movieRepository.findAll();

        for (Movie movie1 : movies) {
            if (movie1.getTitle().equalsIgnoreCase(movie.getTitle())) {

                movie1.setRateNumber(movie1.getRateNumber() + 1);   //увеличиваем количество оценок на единицу

                int rateNumber = movie1.getRateNumber();
                if (rateNumber == 1) {
                    movie1.setRating(rating);   //устанавливаем рейтинг, если это первая оценка фильма
                } else {
                    movie1.setRating(  (  (movie.getRating() * (rateNumber - 1)) + rating) / rateNumber);   //рассчитываем средний рейтинг
                }

                user.getWatchlist().remove(movie1);
                user.getWatchedMovies().add(movie1);

                movieRepository.save(movie1);
                return movie1;
            }
        }
        return null;
    }
}
