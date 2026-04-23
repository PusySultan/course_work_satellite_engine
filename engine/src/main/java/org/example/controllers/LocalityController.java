package org.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/locality")
public class LocalityController
{
    @GetMapping
    public ResponseEntity<?> getLocality()
    {
        return null;
    }

    @PostMapping
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
