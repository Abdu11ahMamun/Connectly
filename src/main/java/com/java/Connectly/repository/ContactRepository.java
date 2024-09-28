package com.java.Connectly.repository;

import com.java.Connectly.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    //pagination
    @Query("from Contact as c where c.user.id=:userId")
    //pageable = current-page + contact per page
    public Page<Contact> findCountactsByUser(@Param("userId") int userId, Pageable pageable);

}
