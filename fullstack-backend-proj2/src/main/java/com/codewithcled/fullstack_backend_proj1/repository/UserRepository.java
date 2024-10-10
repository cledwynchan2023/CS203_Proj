package com.codewithcled.fullstack_backend_proj1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import  com.codewithcled.fullstack_backend_proj1.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByUsername(String username);

    List<User> findByRole(String role);

    // @Query("SELECT u FROM User u WHERE u.lastModified > :timestamp")
    // List<User> findChangesSince(@Param("timestamp") LocalDateTime timestamp);
}
