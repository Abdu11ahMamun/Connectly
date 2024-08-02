package com.java.Connectly.repository;

import com.java.Connectly.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.email = :emailForQueryPassing")
    public User getUserByEmail(@Param("emailForQueryPassing") String email);

    @Query("select u from User u where u.role = :roleForQueryPassing")
    public List<User> findByRole(@Param("roleForQueryPassing") String role);
}
