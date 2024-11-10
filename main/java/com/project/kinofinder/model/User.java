package com.project.kinofinder.model;

import jakarta.persistence.*;

import javax.swing.*;
import java.util.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column
    private int age;

    @Column
    private String password;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_watchlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> watchlist;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> favorites;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_watched",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> watchedMovies;


    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> genres;


    public User(String name, int age, String password, Set<String> genres) {
        this.name = name;
        this.age = age;
        this.password = password;
        this.genres = genres;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Set<Movie> getWatchlist() {
        return watchlist;
    }

    public Set<Movie> getFavorites() {
        return favorites;
    }

    public Set<Movie> getWatchedMovies() {
        return watchedMovies;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void addMovieToWatchlist(Movie movie) {
        watchlist.add(movie);
    }


    public void addMovieToWatched(Movie movie) {
        watchedMovies.add(movie);
    }

    public void addMovieToFavorites(Movie movie) {
        favorites.add(movie);
    }

    public Set<Movie> getWatchedList() {
        return watchlist;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setWatchlist(Set<Movie> watchlist) {
        this.watchlist = watchlist;
    }
}
