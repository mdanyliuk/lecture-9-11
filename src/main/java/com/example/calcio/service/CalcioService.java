package com.example.calcio.service;

import com.example.calcio.dto.PlayerInfoDto;
import com.example.calcio.dto.PlayerSaveDto;
import com.example.calcio.model.Club;

import java.util.List;
import java.util.Optional;

public interface CalcioService {

    List<Club> findAllClubs();

    Integer createPlayer(PlayerSaveDto dto);

    List<PlayerInfoDto> findAllPlayers();

    PlayerInfoDto getPlayer(Integer id);

    void updatePlayer(Integer id, PlayerSaveDto dto);

    void deletePlayer(Integer id);

}
