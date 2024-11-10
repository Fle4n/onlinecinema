package com.project.kinofinder.model;


import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column
    private String genre;
    @Column
    private Integer year;
    @Column
    private String director;
    @Column
    private Double rating;
    @Column
    private Double earnings;
    @Column
    private Integer rateNumber;

    @ManyToMany
    @JoinTable(name = "user_favorites",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersFavorited;

    @ManyToMany
    @JoinTable(name = "user_watchlist",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> usersWatched;

    @ManyToMany
    @JoinTable(name = "user_watched",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> userWatchList;


    public Movie(String title, String genre, int year, String director, double rating, double earnings) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.earnings = earnings;
        this.rateNumber = 0;
    }

    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public Double getRating() {
        return rating;
    }

    public Double getEarnings() {
        return earnings;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Genre: " + genre + ", Year: " + year +
                ", Director: " + director + ", Rating: " + rating + ", Earnings: $" + earnings;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Integer getRateNumber() {
        return rateNumber;
    }

    public void setRateNumber(int rateNumber) {
        this.rateNumber = rateNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return Objects.equals(year, movie.year) &&
                Objects.equals(title, movie.title) &&
                Objects.equals(genre, movie.genre) &&
                Objects.equals(director, movie.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, year, director);
    }
}
