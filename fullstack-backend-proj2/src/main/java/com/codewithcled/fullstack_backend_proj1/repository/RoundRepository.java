package com.codewithcled.fullstack_backend_proj1.repository;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
    
}