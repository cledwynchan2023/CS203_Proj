package com.codewithcled.fullstack_backend_proj1.repository;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("SELECT t FROM Tournament t JOIN t.participants u WHERE u.id = :userId")
    List<Tournament> findTournamentsByUserId(@Param("userId") Long userId);

}
