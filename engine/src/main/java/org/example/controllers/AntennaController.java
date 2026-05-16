package org.example.controllers;

import org.example.dbModels.Antenna;
import org.example.dto.AntennaDTO;
import org.example.exceptions.GlobalException;
import org.example.services.AntennaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/antenna")
public class AntennaController
{
    @Autowired
    private AntennaService antennaService;

    @GetMapping("get/name")
    public ResponseEntity<?> getAntenna(@RequestParam String name)
    {
        Antenna antenna;
        try {
            antenna = antennaService.getAntennaByName(name);
            return new ResponseEntity<>(antenna, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAntenna(@RequestBody AntennaDTO antennaDTO)
    {
        try {
            antennaService.createAntenna(antennaDTO);
            return ResponseEntity.ok("Антенна с названием " + antennaDTO.getName() + " успешно создана");
        } catch (GlobalException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
