package com.codewithcled.fullstack_backend_proj1.repository;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {


}
