package org.example.services;

import org.example.exceptions.GlobalException;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class OverrideService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String[] requiredFields = new String[]{
            "antennaDiameter", "antennaEfficiency", "gainConverter"
    };

    public JsonNode getOverrideBlock(String jsonString)
    {
        JsonNode topJson = getTopJson(jsonString);
        JsonNode overrideBlock = getOverrideBlock(topJson);

        validateOverrideBlock(overrideBlock);

        return overrideBlock;
    }

    private void validateOverrideBlock(JsonNode topJson)
    {
        for (String field : requiredFields)
        {
            if(!topJson.has(field)) {
                throw new GlobalException("В блоке констант и переопределений, отсутствует поле " + field);
            }
        }
    }

    private JsonNode getOverrideBlock(JsonNode topJson)
    {
        if (!topJson.has("override")) {
            throw new GlobalException("Отсутствует блок переопределений и констант");
        }

        return topJson.get("override");
    }

    private JsonNode getTopJson(String jsonString)
    {
        try {
            return mapper.readTree(jsonString);
        } catch (JacksonException e) {
            throw new GlobalException("Ошибка преобразования блока верхнего уровня");
        }
    }
}
