package org.example.services;

import org.example.dbModels.Satellite;
import org.example.dto.SatelliteDTO;
import org.example.exceptions.GlobalException;
import org.example.repositories.SatelliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class SatelliteService
{
    @Autowired
    private SatelliteRepository satelliteRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Function<String, Satellite>> satelliteFindMap = new HashMap<>();
    {
        satelliteFindMap.put("name", name -> satelliteRepository.getByName(name));
    }

    public Satellite getSatellite(String jsonBody)
    {
        JsonNode topJsonNode = mapBody(jsonBody);
        JsonNode satelliteBlock = getSatelliteBlock(topJsonNode);

        Map.Entry<String, String> searchParam = getSearchParam(satelliteBlock);

        Satellite satellite = satelliteFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(satellite)) {
            throw new GlobalException("Не существует спутника с переданными параметрам");
        }

        return satellite;
    }

    private Map.Entry<String, String> getSearchParam(JsonNode satelliteBlock)
    {
        Field[] declaredFields = Satellite.class.getDeclaredFields();

        for(Field currentField : declaredFields)
        {
            if(currentField.getName().equalsIgnoreCase("id")) {
                continue;
            }

            if(!satelliteBlock.has(currentField.getName().toLowerCase()))
            {
                continue;
            }

            return new AbstractMap.SimpleEntry<>(
                    currentField.getName().toLowerCase(),
                    satelliteBlock.get(currentField.getName().toLowerCase()).asText());
        }

        throw new GlobalException("Нет корректного поля для поиска спутника");
    }

    /**
     * Возвращает блок информации о спутнике
     * @param topJsonNode Объект в котором выполняется поиск
     * @return JsonNode блок информации о спутнике
     * @throws GlobalException при отсутствии блока с информацией
     */
    private JsonNode getSatelliteBlock(JsonNode topJsonNode)
    {
        if(!topJsonNode.has("satellite")) {
            throw new GlobalException("Не указаны параметры для поиска спутника");
        }

        return topJsonNode.get("satellite");
    }

    /**
     * Переводит строку в JsonNode
     * @param jsonBody входная строка
     * @return JsonNode
     * @throws GlobalException при ошибке парсинг-а
     */
    private JsonNode mapBody(String jsonBody)
    {
        try {
            return mapper.readTree(jsonBody);
        } catch (JacksonException e) {
            throw new GlobalException("Ошибка при чтении тела запроса в " + this.getClass().getName());
        }
    }

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
}
