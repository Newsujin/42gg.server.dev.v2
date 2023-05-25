package com.gg.server.domain.team.dto;

import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.GameTeamUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class TeamInfo {
    private Integer teamScore;
    private Long teamId;
    private List<TeamUserInfoDto> players;

    public TeamInfo() {
        this.players = new ArrayList<>();
    }

    public void setTeam(Long teamId, Integer teamScore) {
        this.teamId = teamId;
        this.teamScore = teamScore;
    }

    public void addPlayer(GameTeamUserInfo info) {
        this.players.add(new TeamUserInfoDto(info.getIntraId(), info.getImage(), info.getExp()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof TeamInfo)) {
            return false;
        } else {
            TeamInfo other = (TeamInfo) obj;
            return this.teamScore.equals(other.getTeamScore())
                    && this.teamId.equals(other.getTeamId())
                    && this.players.equals(other.getPlayers());
        }
    }
}