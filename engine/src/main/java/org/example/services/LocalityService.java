package org.example.services;

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
    private HashMap<String, Function<String, Locality>> localityFindMap = new HashMap<>();
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

    private JsonNode getLocalityBlock(JsonNode topJsonNode)
    {
        if(!topJsonNode.has("locality")) {
            throw new GlobalException("Не указаны параметры для поиска населенного пункта");
        }

        return topJsonNode.get("locality");
    }

    private JsonNode mapBody(String jsonBody)
    {
        try {
            return mapper.readTree(jsonBody);
        } catch (JacksonException e) {
            throw new GlobalException("Ошибка при чтении тела запроса");
        }
    }

    private Map.Entry<String, String> getSearchParam(JsonNode body)
    {
        Field[] declaredFieldsField = Locality.class.getDeclaredFields();

        for(Field currentField : declaredFieldsField)
        {
            if(currentField.getName().equalsIgnoreCase("id")) {
                continue;
            }

            if(!body.has(currentField.getName().toLowerCase()))
            {
                continue;
            }

            return new AbstractMap.SimpleEntry<>(
                    currentField.getName().toLowerCase(),
                    body.get(currentField.getName().toLowerCase()).asText());
        }

        throw new GlobalException("Нет корректного поля для поиска населенного пункта");
    }
}
