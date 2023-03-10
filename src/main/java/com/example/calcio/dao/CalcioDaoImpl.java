package com.example.calcio.dao;

import com.example.calcio.dto.PlayerInfoDto;
import com.example.calcio.dto.PlayerQueryDto;
import com.example.calcio.model.Club;
import com.example.calcio.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
            throw new RuntimeException("getClubById Error", e);
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
        } catch (Exception e) {
            throw new RuntimeException("findAllPlayers Error", e);
        }
        return result;
    }

    @Override
    public Optional<Player> getPlayerById(Integer id) {
        Player player = null;
        try(Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("select id, name, position, id_club from player where id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                player = mapPlayer(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("getPlayerById Error", e);
        }
        return Optional.ofNullable(player);
    }

    @Override
    public void deletePlayer(Integer id) {
        try(Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("delete from player where id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("deletePlayer Error", e);
        }
    }

    @Override
    public Page<PlayerInfoDto> findPlayersByPositionAndClub(PlayerQueryDto dto) {
        List<PlayerInfoDto> result = new ArrayList<>();
        int total = 0;
        try(Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("select id, name, position, id_club " +
                        "from player where position = ? and id_club = ? " +
                        "limit " + dto.getSize() * dto.getPage() + ", " + dto.getSize());
                PreparedStatement stCount = conn.prepareStatement("select count(id) " +
                    "from player where position = ? and id_club = ? ")) {
            stmt.setString(1, dto.getPosition());
            stmt.setInt(2, dto.getClubId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapToPlayerInfoDto(rs));
            }
            stCount.setString(1, dto.getPosition());
            stCount.setInt(2, dto.getClubId());
            rs = stCount.executeQuery();
            rs.next();
            total = rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("findPlayersByPositionAndClub Error", e);
        }
        return new PageImpl<>(result, PageRequest.of(dto.getPage(), dto.getSize()), total);
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
            stmt.setInt(4, player.getId());
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

    private PlayerInfoDto mapToPlayerInfoDto(ResultSet rs) throws SQLException {
        return PlayerInfoDto.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .position(rs.getString("position"))
                .clubName(getClubById(rs.getInt("id_club")).orElseThrow(RuntimeException::new).getName())
                .build();
    }
}
