package org.example.services;

import org.example.dbModels.Satellite;
import org.example.dto.SatelliteDTO;
import org.example.exceptions.GlobalException;
import org.example.repositories.SatelliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class SatelliteServiceClass extends BaseModelServiceClass
{
    @Autowired
    private SatelliteRepository satelliteRepository;

    private final Map<String, Function<String, Satellite>> satelliteFindMap = new HashMap<>();
    {
        satelliteFindMap.put("name", name -> satelliteRepository.getByName(name));
    }

    public Satellite getSatelliteBySatellitelock(String topJsonStr)
    {
        /// Конвертируем String в JsonNode
        JsonNode topJsonNode = super.mapBody(topJsonStr);

        /// Получаем блок о спутнике
        JsonNode satelliteBlock = super.getRequiredBlockByName(topJsonNode, "satellite");

        /// Получаем параметры поиска
        Map.Entry<String, String> searchParam = super.getSearchParam(satelliteBlock, Satellite.class);

        /// Находим спутник
        Satellite satellite = satelliteFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(satellite)) {
            throw new GlobalException("Не существует спутника с переданными параметрам");
        }

        return satellite;
    }

    /*
    private Map.Entry<String, String> getSearchParam(JsonNode searchBlock)
    {
        Field[] declaredFields = Satellite.class.getDeclaredFields();

        for(Field currentField : declaredFields)
        {
            if(currentField.getName().equalsIgnoreCase("id")) {
                continue;
            }

            if(!searchBlock.has(currentField.getName().toLowerCase()))
            {
                continue;
            }

            return new AbstractMap.SimpleEntry<>(
                    currentField.getName().toLowerCase(),
                    searchBlock.get(currentField.getName().toLowerCase()).asText());
        }

        throw new GlobalException("Нет корректного поля для поиска спутника");
    }
     */

    /**
     * Создает запись о спутнике в БД из переданной DTO
     * @param dto информация о спутнике
     * @throws GlobalException если спутник с переданным именем уже существует
     */
    public void createSatellite(SatelliteDTO dto)
    {
        if(satelliteRepository.existsByName(dto.getName())){
            throw new GlobalException("Данный спутник уже существует, для обновления воспользуйтесь методом PUT");
        }

        Satellite satellite = new Satellite();
        satellite.createFromDTO(dto);
        satelliteRepository.save(satellite);
    }

    public Satellite getSatelliteByName(String name)
    {
        try {
            return satelliteFindMap.get("name").apply(name);
        } catch (Exception e) {
            throw new GlobalException("Ошибка поиска спутника");
        }
    }
}
