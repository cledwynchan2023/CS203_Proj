package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setElo(user.getElo());
        dto.setCurrentTournaments(user.getCurrentTournaments().stream()
                .map(UserMapper::toTournamentDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public static List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static TournamentDTO toTournamentDTO(Tournament tournament) {
        TournamentDTO dto = new TournamentDTO();
        dto.setId(tournament.getId());
        dto.setTournamentName(tournament.getTournament_name());
        dto.setDate(tournament.getDate());
        dto.setStatus(tournament.getStatus());
        dto.setSize(tournament.getSize());
        dto.setCurrentSize(tournament.getCurrentSize());
        dto.setNoOfRounds(tournament.getNoOfRounds());
       
        return dto;
    }
}