package org.example.services;

import org.example.LocalityDTO;
import org.example.dbModels.Locality;
import org.example.exceptions.GlobalException;
import org.example.repositories.LocalityRepository;
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
public class LocalityService
{
    @Autowired
    private LocalityRepository localityRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HashMap<String, Function<String, Locality>> localityFindMap = new HashMap<>();
    {
        localityFindMap.put("name", name -> localityRepository.getByName(name));
    }

    public Locality getLocality(String jsonBody)
    {
        JsonNode topJsonNode = mapBody(jsonBody);
        JsonNode localityBlock = getLocalityBlock(topJsonNode);

        Map.Entry<String, String> searchParam = getSearchParam(localityBlock);

        Locality locality = localityFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(locality)) {
            throw new GlobalException("Не существует населенного пункта по переданным параметрам");
        }

        return locality;
    }

    /**
     * Возвращает блок информации о населенном пункте
     * @param topJsonNode Объект в котором выполняется поиск
     * @return JsonNode блок информации о спутнике
     * @throws GlobalException при отсутствии блока с информацией
     */
    private JsonNode getLocalityBlock(JsonNode topJsonNode)
    {
        if(!topJsonNode.has("locality")) {
            throw new GlobalException("Не указаны параметры для поиска населенного пункта");
        }

        return topJsonNode.get("locality");
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

    private Map.Entry<String, String> getSearchParam(JsonNode topJsonNode)
    {
        Field[] declaredFields = Locality.class.getDeclaredFields();

        for(Field currentField : declaredFields)
        {
            if(currentField.getName().equalsIgnoreCase("id")) {
                continue;
            }

            if(!topJsonNode.has(currentField.getName().toLowerCase()))
            {
                continue;
            }

            return new AbstractMap.SimpleEntry<>(
                    currentField.getName().toLowerCase(),
                    topJsonNode.get(currentField.getName().toLowerCase()).asText());
        }

        throw new GlobalException("Нет корректного поля для поиска населенного пункта");
    }

    public void createLocality(LocalityDTO dto)
    {
        if(localityRepository.existsByName(dto.getName())) {
            throw new GlobalException("Данная местность уже существует, для обновления воспользуйтесь методом PUT");
        }

        Locality locality = new Locality();
        locality.createFromDto(dto);
        localityRepository.save(locality);
    }
}
