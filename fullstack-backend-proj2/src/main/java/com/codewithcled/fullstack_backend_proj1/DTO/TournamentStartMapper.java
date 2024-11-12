package com.codewithcled.fullstack_backend_proj1.DTO;


import java.util.List;
import java.util.stream.Collectors;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;

public class TournamentStartMapper {
    public static TournamentStartDTO toDTO(Tournament tournament) {
        TournamentStartDTO dto = new TournamentStartDTO();
        dto.setId(tournament.getId());
        dto.setTournamentName(tournament.getTournament_name());
        dto.setDate(tournament.getDate());
        dto.setStatus(tournament.getStatus());
        dto.setSize(tournament.getSize());
        dto.setCurrentSize(tournament.getCurrentSize());
        dto.setNoOfRounds(tournament.getNoOfRounds());
        dto.setParticipants(tournament.getParticipants().stream()
                .map(TournamentMapper::toUserDTO)
                .collect(Collectors.toList()));
        dto.setRounds(tournament.getRounds().stream()
                .map(RoundMapper::toDTO)
                .collect(Collectors.toList()));
        dto.setCurrentRound(tournament.getCurrentRound());
        return dto;
    }

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setElo(user.getElo());
        return dto;
    }

    public static List<TournamentStartDTO> toDTOList(List<Tournament> tournaments) {
        return tournaments.stream()
                .map(TournamentStartMapper::toDTO)
                .collect(Collectors.toList());
    }
}