package com.example.calcio.service;

import com.example.calcio.model.Club;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalcioServiceImpl implements CalcioService {

    @Override
    public List<Club> findAllClubs() {
        return List.of(new Club(1, "Atalanta"),
                        new Club(2, "Bologna"),
                        new Club(3, "Cremonese"));
    }
}
