package com.codewithcled.fullstack_backend_proj1.service;

import java.util.List;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;

public interface RoundService {
    public Round createFirstRound(Long tournamentId) throws Exception;

    public void checkComplete(Long roundId) throws Exception;

    public Round createNextRound(Long tournamentId) throws Exception;

    public List<Match> getAllMatches(Long tournamentId) throws Exception;
}
