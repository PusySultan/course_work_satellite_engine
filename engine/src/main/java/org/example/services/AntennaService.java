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

    public Antenna getAntennaByAntennaBlock(String topJson)
    {
        throw new GlobalException("Не реализован поиск антенны по блоку");
    }

    /**
     * Создаёт антенну по ее DTO
     */
    public void createAntenna(AntennaDTO antennaDTO)
    {
        if (antennaRepository.existsByName(antennaDTO.getName())) {
            throw new GlobalException("Данная антенна уже существует, для обновления воспользуйтесь методом PUT");
        }

        Antenna antenna = new Antenna();
        antenna.createFromDTO(antennaDTO);

        antennaRepository.save(antenna);
    }
}
