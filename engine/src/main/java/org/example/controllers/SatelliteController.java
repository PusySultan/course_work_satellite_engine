package org.example.controllers;

import org.example.dto.SatelliteDTO;
import org.example.services.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/satellite")
public class SatelliteController
{
    @Autowired
    private SatelliteService satelliteService;

    @GetMapping("get/name")
    public ResponseEntity<?> getSatellite()
    {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSatellite(@RequestBody SatelliteDTO dto)
    {
        satelliteService.createSatellite(dto);
        return ResponseEntity.ok("Спутник с именем " + dto.getSatelliteName() + " успешно создан");
    }

    @PutMapping
    public ResponseEntity<?> updateSatellite()
    {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSatellite()
    {
        return null;
    }
}
