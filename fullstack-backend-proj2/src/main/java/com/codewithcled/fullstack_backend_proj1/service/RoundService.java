package com.codewithcled.fullstack_backend_proj1.service;

import java.util.List;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;

public interface RoundService {
    public Round createFirstRound(List<User> participants) throws Exception;
}
