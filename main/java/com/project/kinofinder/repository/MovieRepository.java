package com.project.kinofinder.repository;

import com.project.kinofinder.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m")
    Set<Movie> getAllMovies();

    @Query("SELECT m FROM Movie m WHERE m.title = ?1")
    Movie getMovieByTitle(String title);

    @Query("DELETE FROM Movie m WHERE m.title = ?1")
    void deleteMovieByTitle(String title);

}
