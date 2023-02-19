package com.example.calcio.controller;

import com.example.calcio.dto.PlayerInfoDto;
import com.example.calcio.dto.PlayerQueryDto;
import com.example.calcio.dto.PlayerSaveDto;
import com.example.calcio.dto.RestResponse;
import com.example.calcio.model.Club;
import com.example.calcio.service.CalcioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalcioController {

    @Autowired
    private final CalcioService calcioService;

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createPlayer(@Valid @RequestBody PlayerSaveDto dto) {
        int id = calcioService.createPlayer(dto);
        return new RestResponse(String.valueOf(id));
    }

    @GetMapping("/players")
    public List<PlayerInfoDto> findAllPlayers() {
        return calcioService.findAllPlayers();
    }

    @GetMapping("/players/{id}")
    public PlayerInfoDto getPlayer(@PathVariable int id) {
        return calcioService.getPlayer(id);
    }

    @PutMapping("/players/{id}")
    public RestResponse updatePlayer(@PathVariable int id, @Valid @RequestBody PlayerSaveDto dto) {
        calcioService.updatePlayer(id, dto);
        return new RestResponse("OK");
    }

    @DeleteMapping("/players/{id}")
    public RestResponse deletePlayer(@PathVariable int id) {
        calcioService.deletePlayer(id);
        return new RestResponse("OK");
    }

    @PostMapping("/players/_search")
    public Page<PlayerInfoDto> searchPlayers(@Valid @RequestBody PlayerQueryDto dto) {
        return calcioService.findPlayersByPositionAndClub(dto);
    }

    @GetMapping("/clubs")
    public List<Club> findAllClubs() {
        return calcioService.findAllClubs();
    }

}
