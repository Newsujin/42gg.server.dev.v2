package com.gg.server.admin.season.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.domain.season.data.Season;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SeasonAdminService {
    private final SeasonAdminRepository seasonAdminRepository;

    public List<SeasonAdminDto> findAllSeasons() {
        List<Season> seasons =  seasonAdminRepository.findAll();
        List<SeasonAdminDto> dtoList = new ArrayList<>();
        for (Season season : seasons) {
            SeasonAdminDto dto = new SeasonAdminDto(season);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public Long createSeason(SeasonCreateRequestDto createDto) {
        Season newSeason = Season.builder()
                .seasonName(createDto.getSeasonName())
                .startTime(createDto.getStartTime())
                .startPpp(createDto.getStartPpp())
                .pppGap(createDto.getPppGap())
                .build();
        insert(newSeason);
        seasonAdminRepository.save(newSeason);
        return (newSeason.getId());
    }

    @Transactional
    public SeasonAdminDto findSeasonById(Long seasonId) {
        Season season = seasonAdminRepository.findById(seasonId).orElseThrow(()-> throw AdminException());

        return SeasonAdminDto.from(season);
    }


//    @Transactional
//    public void deleteSeason(Long seasonId) {
//        Season season = seasonAdminRepository.findById(seasonId).orElse(null);
//        //if (season == null)
////            throw new BusinessException("E0001", "존재하지 않은 시즌입니다.");
//        detach(season);
//        seasonAdminRepository.delete(season);
//    }
//    @Transactional
//    public void updateSeason(Long seasonId, SeasonUpdateRequestDto updateDto) {
//        Season season = seasonAdminRepository.findById(seasonId).orElse(null);
//        //if (season == null)
////            throw new BusinessException("E0001", "존재하지 않은 시즌입니다.");
//        if (LocalDateTime.now().isBefore(season.getEndTime())) {
//            season.setPppGap(updateDto.getPppGap());
//        }
//        if (LocalDateTime.now().isBefore(season.getStartTime())) {
//            detach(season);
//            season.setSeasonName(updateDto.getSeasonName());
//            season.setSeasonMode(updateDto.getSeasonMode());
//            season.setStartTime(updateDto.getStartTime());
//            season.setStartPpp(updateDto.getStartPpp());
//            insert(season);
//            seasonAdminRepository.save(season);
//            checkSeasonAtDB(updateDto.getSeasonMode());
//        }
//    }

    private void insert(Season season)
    {
        List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getStartTime());
        Season beforeSeason;
        if (beforeSeasons.isEmpty())
            beforeSeason = null;
        else
            beforeSeason = beforeSeasons.get(0).getId() != season.getId()? beforeSeasons.get(0) : beforeSeasons.get(1);
        List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getSeasonMode(), season.getStartTime());
        Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

        if (LocalDateTime.now().plusMinutes(1).isAfter(season.getStartTime()))
            throw new BusinessException("E0001", "현재시간 이전의 시즌을 생성 할 수 없습니다.");
        if (beforeSeason != null) {
            if (beforeSeason.getStartTime().plusDays(1).isAfter(season.getStartTime()))
                throw new BusinessException("E0001", "이전시즌이 너무 빨리 끝납니다.");
            beforeSeason.setEndTime(season.getStartTime().minusSeconds(1));
        }
        if (afterSeason != null)
            season.setEndTime(afterSeason.getStartTime().minusSeconds(1));
        else
            season.setEndTime(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
    }

    private void detach(Season season)
    {
        List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getSeasonMode(), season.getStartTime());
        Season beforeSeason = beforeSeasons.isEmpty() ? null : beforeSeasons.get(0);
        List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getSeasonMode(), season.getStartTime());
        Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

        if ((LocalDateTime.now().isAfter(season.getStartTime()) && LocalDateTime.now().isBefore(season.getEndTime()))
                || season.getEndTime().isBefore(LocalDateTime.now()))
            throw new BusinessException("E0001", "과거나 현재시즌은 수정/삭제가 불가합니다.");
        if (beforeSeason != null) {
            if (afterSeason != null)
                beforeSeason.setEndTime(afterSeason.getStartTime().minusSeconds(1));
            else
                beforeSeason.setEndTime(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        }
    }

    private boolean isOverlap(Season season1, Season season2) {
        LocalDateTime start1 = season1.getStartTime();
        LocalDateTime end1 = season1.getEndTime();
        LocalDateTime start2 = season2.getStartTime();
        LocalDateTime end2 = season2.getEndTime();

        if (start1.isEqual(end1) || start2.isEqual(end2)) {
            return false;
        }
        // 첫 번째 기간이 두 번째 기간의 이전에 끝날 때
        if (end1.isBefore(start2)) {
            return false;
        }

        // 첫 번째 기간이 두 번째 기간의 이후에 시작할 때
        if (start1.isAfter(end2)) {
            return false;
        }

        // 나머지 경우에는 두 기간이 겹칩니다.
        return true;
    }
}
