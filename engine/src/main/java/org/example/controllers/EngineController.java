package org.example.controllers;

import org.example.dbModels.Locality;
import org.example.dbModels.Satellite;
import org.example.exceptions.GlobalException;
import org.example.services.EngineService;
import org.example.services.LocalityService;
import org.example.services.OverrideService;
import org.example.services.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("api/engine")
public class EngineController
{
    @Autowired
    private OverrideService overrideService;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private SatelliteService satelliteService;

    @Autowired
    private EngineService engineService;

    @PostMapping
    public ResponseEntity<?> process(@RequestBody String body)
    {
        try
        {
            Locality locality = localityService.getLocality(body);
            Satellite satellite = satelliteService.getSatellite(body);
            JsonNode overrideBlock = overrideService.getOverrideBlock(body);

            return new ResponseEntity<>(engineService.engine(locality, satellite, overrideBlock), HttpStatus.OK);
        }
        catch (GlobalException e)
        {
            return new ResponseEntity<>("err - " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
