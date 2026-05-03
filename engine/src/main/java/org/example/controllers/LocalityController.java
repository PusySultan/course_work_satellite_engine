package org.example.controllers;

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

    @GetMapping("/get/name")
    public ResponseEntity<?> getLocality(@RequestParam String name)
    {
        String jsonString = """
                {
                   "locality" :
                   {
                         "name" : "%s"
                   }
                }
                """.formatted(name);;

        Locality locality = localityService.getLocality(jsonString);
        return new ResponseEntity<>(locality, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLocality()
    {
        return null;
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
