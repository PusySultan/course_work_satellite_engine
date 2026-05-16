package org.example.controllers;


import org.example.dbModels.ConverterTV;
import org.example.dto.ConverterTV_DTO;
import org.example.services.ConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/converter")
public class ConverterController
{
    @Autowired
    private ConverterService converterService;
    
    @GetMapping("get/name")
    public ResponseEntity<?> getAntenna(@RequestParam String name)
    {
        ConverterTV converterTV;
        try {
            converterTV = converterService.getConverterByName(name);
            return new ResponseEntity<>(converterTV, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAntenna(@RequestBody ConverterTV_DTO antennaDTO)
    {
        converterService.createConverter(antennaDTO);
        return ResponseEntity.ok("Конвертер с названием " + antennaDTO.getName() + " успешно создана");
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
