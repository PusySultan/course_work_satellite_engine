package org.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/satellite")
public class SatelliteController
{
    @GetMapping
    public ResponseEntity<?> getSatellite()
    {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> createSatellite()
    {
        return null;
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
