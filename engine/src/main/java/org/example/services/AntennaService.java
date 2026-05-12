package org.example.services;

import org.example.dbModels.Antenna;
import org.example.dbModels.Locality;
import org.example.dto.AntennaDTO;
import org.example.exceptions.GlobalException;
import org.example.repositories.AntennaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.function.Function;

@Controller
public class AntennaService
{
    @Autowired
    private AntennaRepository antennaRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, Function<String, Antenna>> antennaFindMap = new HashMap<>();
    {
        antennaFindMap.put("name", name -> antennaRepository.getByName(name));
    }

    public Antenna getAntennaByName(String name)
    {
        try {
            return antennaFindMap.get("name").apply(name);
        } catch (Exception e) {
            throw new GlobalException("Ошибка при поиске антенны");
        }
    }

    /**
     * Создаёт антенну по
     */
    public void createAntenna(AntennaDTO antennaDTO)
    {
        Antenna antenna = new Antenna();
        antenna.createFromDTO(antennaDTO);

        antennaRepository.save(antenna);
    }
}
