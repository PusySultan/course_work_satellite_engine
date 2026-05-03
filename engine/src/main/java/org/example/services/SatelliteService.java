package org.example.services;

import org.example.dbModels.Satellite;
import org.example.repositories.SatelliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class SatelliteService
{
    @Autowired
    private SatelliteRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    public Satellite getSatellite(String body)
    {
        return null;
    }
}
