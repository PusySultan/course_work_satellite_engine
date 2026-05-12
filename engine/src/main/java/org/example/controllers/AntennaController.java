package org.example.controllers;

import org.example.dto.AntennaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/antenna")
public class AntennaController
{
    @GetMapping("get/name")
    public ResponseEntity<?> getAntenna(@RequestParam String name)
    {
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLocality(@RequestBody AntennaDTO antenna)
    {
        // localityService.createLocality(locality);
        return ResponseEntity.ok("Антенна с названием " + antenna.getName() + " успешно создана");
    }

    @PutMapping
    public ResponseEntity<?> updateAntenna()
    {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAntenna()
    {
        return null;
    }
}
