package com.codewithcled.fullstack_backend_proj1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.codewithcled.fullstack_backend_proj1.model.User;

/**
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Check if a user exists by username
     * 
     * @param username username of the user
     * @return boolean true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user exists by email
     * 
     * @param email email of the user
     * @return boolean true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email
     * 
     * @param email email of the user
     * @return User the user with the email
     */
    User findByEmail(String email);

    /**
     * Find a user by username
     * 
     * @param username username of the user
     * @return User the user with the username
     */
    User findByUsername(String username);

    /**
     * Find all users by role
     * 
     * @param role role of the user
     * @return List<User> list of users with the role
     */
    List<User> findByRole(String role);
}
