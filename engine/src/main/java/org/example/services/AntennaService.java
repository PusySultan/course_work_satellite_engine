package org.example.services;

import org.example.dbModels.Antenna;
import org.example.dto.AntennaDTO;
import org.example.exceptions.GlobalException;
import org.example.repositories.AntennaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Controller
public class AntennaService extends BaseModelServiceClass
{
    @Autowired
    private AntennaRepository antennaRepository;

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
     * Находит антенну, по переданным параметрам в блоке
     * @param topJsonStr блок запроса на расчет
     * @return антенну
     */
    public Antenna getAntenna(String topJsonStr)
    {
        /// Конвертируем String в JsonNode
        JsonNode topJsonNode = super.mapBody(topJsonStr);

        /// Получаем блок об антенне
        JsonNode antennaBlock = super.getRequiredBlockByName(topJsonNode, "antenna");

        /// Получаем поисковые параметры
        Map.Entry<String, String> searchParam = super.getSearchParam(antennaBlock, Antenna.class);

        /// Находим антенну
        Antenna antenna = antennaFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(antenna)) {
            throw new GlobalException("Не существует антенны с переданными переданным параметрам");
        }

        return antenna;
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
