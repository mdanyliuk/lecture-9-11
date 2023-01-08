package com.example.calcio.dao;

import com.example.calcio.model.Club;
import com.example.calcio.model.Player;

import java.util.List;
import java.util.Optional;

public interface CalcioDao {

    List<Club> findAllClubs();
    Integer savePlayer(Player player);
    Optional<Club> getClubById(Integer clubId);
    List<Player> findAllPlayers();
    Optional<Player> getPlayerById(Integer id);
    void deletePlayer(Integer id);

}
