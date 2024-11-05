package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

public class ScoreboardComparator implements Comparator<Entry<Long, Double>>{
    private List<Round> rounds;
    private Round currentRound;
    private UserRepository userRepository;
    private MatchRepository matchRepository;

    public ScoreboardComparator(List<Round> rounds, Round currentRound, UserRepository userRepository, MatchRepository matchRepository){
        this.rounds = rounds;
        this.currentRound = currentRound;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public int compare(Entry<Long, Double> e1, Entry<Long, Double> e2){
        if(e1.getValue() > e2.getValue()){
            return 1;
        }
        else if(e1.getValue() < e2.getValue()){
            return -1;
        }
        else{
            User u1 = this.userRepository.findById(e1.getKey()).get();
            User u2 = this.userRepository.findById(e2.getKey()).get();
            
            try {
                int solkoffTiebreakResult = solkoffTiebreak(u1, u2, this.rounds, this.currentRound);
                if (solkoffTiebreakResult != 0){
                    return solkoffTiebreakResult;
                }

                int ratingTiebreakResult = ratingTiebreak(u1, u2, this.rounds, this.currentRound);
                if (ratingTiebreakResult != 0){
                    return ratingTiebreakResult;
                }
            } catch (Exception e){
                throw new RuntimeException("Error during tiebreak calculation", e);
            }
            return 0;
        }
    }

    public int solkoffTiebreak(User u1, User u2, List<Round> rounds, Round currentRound) throws Exception{
        long u1id = u1.getId();
        long u2id = u2.getId();
        double u1median = 0;
        double u2median = 0;

        for(Round round : rounds){
            Match match = this.matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, u1id, round, u1id);
            if (match == null){
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == u1id){
                    u1median += currentRound.getScoreboard().get(match.getPlayer2());
                } else{
                    u1median += currentRound.getScoreboard().get(match.getPlayer1());
                }
            }
        }

        for(Round round : rounds){
            Match match = this.matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, u2id, round, u2id);
            if (match == null){
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == u2id){
                    u2median += currentRound.getScoreboard().get(match.getPlayer2());
                } else{
                    u2median += currentRound.getScoreboard().get(match.getPlayer1());
                }
            }
        }

        if(u1median > u2median){
            return 1;
        }
        else if(u1median < u2median){
            return -1;
        }
        else{
            return 0;
        }
    }

    public int ratingTiebreak(User u1, User u2, List<Round> rounds, Round currentRound) throws Exception{
        long u1id = u1.getId();
        long u2id = u2.getId();
        Double u1rating = u1.getElo();
        Double u2rating = u2.getElo();

        for(Round round : rounds){
            Match match = this.matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, u1id, round, u1id);
            if (match == null){
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == u1id){
                    User opponent = this.userRepository.findById(match.getPlayer2()).get();
                    Double opponentElo = opponent.getElo();
                    u1rating += opponentElo;
                } else{
                    User opponent = this.userRepository.findById(match.getPlayer1()).get();
                    Double opponentElo = opponent.getElo();
                    u1rating += opponentElo;
                }
            }
        }

        for(Round round : rounds){
            Match match = this.matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, u2id, round, u2id);
            if (match == null){
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == u2id){
                    User opponent = this.userRepository.findById(match.getPlayer2()).get();
                    Double opponentElo = opponent.getElo();
                    u2rating += opponentElo;
                } else{
                    User opponent = this.userRepository.findById(match.getPlayer1()).get();
                    Double opponentElo = opponent.getElo();
                    u2rating += opponentElo;
                }
            }
        }

        if(u1rating > u2rating){
            return 1;
        }
        else if(u1rating < u2rating){
            return -1;
        }
        else{
            return 0;
        }
    }    
}