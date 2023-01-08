package com.example.calcio.dao;

import com.example.calcio.model.Club;
import com.example.calcio.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CalcioDaoImpl implements CalcioDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Club> findAllClubs() {
        List<Club> result = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("select id, name from club");
            while (rs.next()) {
                result.add(mapClub(rs));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("findAllClubs Error", e);
        }
    }

    @Override
    public Integer savePlayer(Player player) {
        if (player.getId() == null) {
            Integer id = createPlayer(player);
            player.setId(id);
        } else {
            updatePlayer(player);
        }
        return player.getId();
    }

    @Override
    public Optional<Club> getClubById(Integer clubId) {
        Club club = null;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select id, name from club where id = ?")) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                club = mapClub(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("getGroupById Error", e);
        }
        return Optional.ofNullable(club);
    }

    @Override
    public List<Player> findAllPlayers() {
        List<Player> result = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("select id, name, position, id_club from player");
            while (rs.next()) {
                result.add(mapPlayer(rs));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("findAllPlayers Error", e);
        }
    }

    private Integer createPlayer(Player player) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into player (name, position, id_club) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, player.getPosition());
            stmt.setInt(3, player.getClub().getId());
            stmt.executeUpdate();
            ResultSet gk = stmt.getGeneratedKeys();
            if (gk.next()) {
                return gk.getInt(1);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Create Player Error", e);
        }
    }

    private void updatePlayer(Player player) {
        try(Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("update player set name = ?, position = ?, id_club = ? where id = ?")) {
            stmt.setString(1, player.getName());
            stmt.setString(2, player.getPosition());
            stmt.setInt(3, player.getClub().getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Update Player Error", e);
        }
    }

    private Club mapClub(ResultSet rs) throws SQLException {
        return Club.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {
        return Player.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .position(rs.getString("position"))
                .club(getClubById(rs.getInt("id_club")).orElseThrow(RuntimeException::new))
                .build();
    }
}
