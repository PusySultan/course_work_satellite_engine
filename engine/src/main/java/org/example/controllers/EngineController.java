package org.example.controllers;

import org.example.dbModels.Antenna;
import org.example.dbModels.Locality;
import org.example.dbModels.Satellite;
import org.example.exceptions.GlobalException;
import org.example.services.*;
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
    private AntennaService antennaService;

    @Autowired
    private EngineService engineService;

    /**
     * Метод POST необходим, так как через
     * командную строку в теле запроса GET
     * проблемно передавать тело запроса
     * @param body
     * @return
     */
    @PostMapping
    public ResponseEntity<?> process(@RequestBody String body)
    {
        try
        {
            /// Получаем населенный пункт
            Locality locality = localityService.getLocalityByLocalityBlock(body);

            ///  Получаем спутник
            Satellite satellite = satelliteService.getSatelliteBySatelliteBlock(body);

            /// Получаем антенну
            Antenna antenna = antennaService.getAntennaByAntennaBlock(body);

            /// Получаем список переопределений
            JsonNode overrideBlock = overrideService.getOverrideBlock(body);

            return new ResponseEntity<>(engineService.engine(locality, satellite, overrideBlock), HttpStatus.OK);
        }
        catch (GlobalException e)
        {
            return new ResponseEntity<>("err - " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
