package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

@Service
public class RoundServiceImplementation implements RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    public RoundServiceImplementation(RoundRepository roundRepository){
        this.roundRepository = roundRepository;
    }

    @Override
    public Round createFirstRound(List<User> participants) throws Exception {
        //for now, we only support even number of participants
        //ie throw exception for odd number of participants
        if(participants.size() % 2 != 0){
            throw new Exception("Number of participants must be even");
        }
        Round firstRound = new Round();
        firstRound.setRoundNum(1);
        List<User> copy = new ArrayList<>(participants);
        Collections.sort(copy, new Comparator<User>(){
            @Override
            public int compare(User u1, User u2){
                if(u1.getElo() > u2.getElo()){
                    return 1;
                }
                else if(u1.getElo() < u2.getElo()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
        for(int i = 0; i < copy.size() / 2; i++){
            Match match = MatchService.createMatch(copy.get(i), copy.get(copy.size() - i - 1));
            match.setRound(firstRound);
            firstRound.addMatch(match);
        }

        return roundRepository.save(firstRound);
    }
}
