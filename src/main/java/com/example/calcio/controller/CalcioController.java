package com.example.calcio.controller;

import com.example.calcio.model.Club;
import com.example.calcio.service.CalcioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalcioController {

    @Autowired
    private final CalcioService calcioService;

    @GetMapping("/clubs")
    public List<Club> findAllClubs() {
        return calcioService.findAllClubs();
    }

}
