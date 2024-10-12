package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.User;

public interface MatchService {
    Match createMatch(User player1, User player2) throws Exception;

    void updateMatch(Long matchId, int result) throws Exception;

    Double getEloChange1(Long matchId) throws Exception;

    Double getEloChange2(Long matchId) throws Exception;

    int getResult(Long matchId) throws Exception;
}
