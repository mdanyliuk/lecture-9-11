package com.example.calcio.service;

import com.example.calcio.dao.CalcioDao;
import com.example.calcio.dto.PlayerInfoDto;
import com.example.calcio.dto.PlayerSaveDto;
import com.example.calcio.model.Club;
import com.example.calcio.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalcioServiceImpl implements CalcioService {

    @Autowired
    CalcioDao calcioDao;

    @Override
    public List<Club> findAllClubs() {
        return calcioDao.findAllClubs();
    }

    @Override
    public Integer createPlayer(PlayerSaveDto dto) {
        Player player = new Player();
        updatePlayerFromDto(player, dto);
        return calcioDao.savePlayer(player);
    }

    @Override
    public List<PlayerInfoDto> findAllPlayers() {
        return calcioDao.findAllPlayers().stream()
                .map(this::toPlayerInfoDto)
                .toList();
    }

    private void updatePlayerFromDto(Player player, PlayerSaveDto dto) {
        player.setName(dto.getName());
        player.setPosition(dto.getPosition());
        player.setClub(resolveClub(dto.getClubId()));
    }

    private Club resolveClub(Integer clubId) {
        if (clubId == null) {
            return null;
        }
        return calcioDao.getClubById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club with id %d not found".formatted(clubId)));
    }

    private PlayerInfoDto toPlayerInfoDto(Player player) {
        return PlayerInfoDto.builder()
                .id(player.getId())
                .name(player.getName())
                .position(player.getPosition())
                .clubName(player.getClub() != null ? player.getClub().getName() : null)
                .build();
    }
}
