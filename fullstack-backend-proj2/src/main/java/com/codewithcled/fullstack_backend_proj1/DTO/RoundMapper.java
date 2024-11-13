package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;

public class RoundMapper {
    public static RoundDTO toDTO(Round round) {
        RoundDTO dto = new RoundDTO();
        dto.setId(round.getId());
        dto.setRoundNum(round.getRoundNum());
        dto.setScoreboard(round.getScoreboard());
        dto.setIsCompleted(round.getIsCompleted());
        List<MatchDTO> matchList = new ArrayList<>();
        for (Match match : round.getMatchList()) {
            matchList.add(MatchDTOMapper.toDTO(match));
        }
        dto.setMatchList(matchList);
        return dto;
    }

    public static List<RoundDTO> toDTOList(List<Round> rounds) {
        return rounds.stream()
                .map(RoundMapper::toDTO)
                .collect(Collectors.toList());
    }
}
