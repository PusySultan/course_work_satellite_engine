package org.example.controllers;

import org.example.dbModels.Satellite;
import org.example.dto.SatelliteDTO;
import org.example.exceptions.GlobalException;
import org.example.services.SatelliteServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/satellite")
public class SatelliteController
{
    @Autowired
    private SatelliteServiceClass satelliteService;

    @GetMapping("get/name")
    public ResponseEntity<?> getSatellite(@RequestParam String name)
    {
        Satellite satellite;

        try {
            satellite = satelliteService.getSatelliteByName(name);
            return new ResponseEntity<>(satellite, HttpStatus.OK);
        } catch (GlobalException e) {
            return new ResponseEntity<>("err - " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSatellite(@RequestBody SatelliteDTO dto)
    {
       try {
           satelliteService.createSatellite(dto);
           return ResponseEntity.ok("Спутник с именем " + dto.getName() + " успешно создан");
       } catch (GlobalException e) {
           return new ResponseEntity<>("err - " + e.getMessage(), HttpStatus.BAD_REQUEST);
       }
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
