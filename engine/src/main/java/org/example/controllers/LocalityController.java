package org.example.controllers;

import org.example.dto.LocalityDTO;
import org.example.dbModels.Locality;
import org.example.services.LocalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/locality")
public class LocalityController
{
    @Autowired
    private LocalityService localityService;

    /**
     * /// Реализовать метод поиска мо имени, без предварительного форматирования
     * @param name имя спутника
     * @return Спутник
     */
    @GetMapping("/get/name")
    public ResponseEntity<?> getLocality(@RequestParam String name)
    {
        Locality locality;
        try {
            locality = localityService.getLocalityByName(name);
            return new ResponseEntity<>(locality, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLocality(@RequestBody LocalityDTO locality)
    {
        localityService.createLocality(locality);
        return ResponseEntity.ok("Местность с именем " + locality.getName() + " успешно создана");
    }

    @PutMapping
    public ResponseEntity<?> updateLocality()
    {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteLocality()
    {
        return null;
    }
}
