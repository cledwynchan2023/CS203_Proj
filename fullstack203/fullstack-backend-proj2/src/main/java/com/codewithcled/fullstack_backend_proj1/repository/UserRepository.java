package com.codewithcled.fullstack_backend_proj1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import  com.codewithcled.fullstack_backend_proj1.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    User findByEmail(String email);



}
