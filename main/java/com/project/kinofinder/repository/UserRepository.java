package com.project.kinofinder.repository;

import com.project.kinofinder.model.Movie;
import com.project.kinofinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u")
    Set<User> getAllUsers();

    @Query("SELECT u FROM User u WHERE u.name = ?1")
    User getUserByName(String name);
}
