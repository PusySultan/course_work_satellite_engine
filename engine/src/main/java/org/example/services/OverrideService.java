package org.example.services;

import org.example.exceptions.GlobalException;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class OverrideService
{
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode getOverrideBlock(String jsonString)
    {
        JsonNode topJson = getTopJson(jsonString);

        if(topJson.has("override")) {
            return topJson.get("override");
        }

        return null;
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
