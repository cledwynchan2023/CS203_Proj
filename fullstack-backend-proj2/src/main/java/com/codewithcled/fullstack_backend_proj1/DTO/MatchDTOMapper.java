package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.codewithcled.fullstack_backend_proj1.model.Match;

public class MatchDTOMapper {
    public static MatchDTO toDTO(Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setPlayer1(match.getPlayer1());
        dto.setPlayer1StartingElo(match.getPlayer1StartingElo());
        dto.setPlayer2(match.getPlayer2());
        dto.setPlayer2StartingElo(match.getPlayer2StartingElo());
        dto.setComplete(match.getIsComplete());
        dto.setResult(match.getResult());
        dto.setEloChange1(match.getEloChange1());
        dto.setEloChange2(match.getEloChange2());
        return dto;
    }

    public static List<MatchDTO> toDTOList(List<Match> rounds) {
        return rounds.stream()
                .map(MatchDTOMapper::toDTO)
                .collect(Collectors.toList());
    }
}
