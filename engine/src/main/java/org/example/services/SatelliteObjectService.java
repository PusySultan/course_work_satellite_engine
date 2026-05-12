package org.example.services;

import org.example.exceptions.GlobalException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public abstract class SatelliteObjectService
{
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Переводит строку в JsonNode
     * @param jsonBody входная строка
     * @return JsonNode
     * @throws GlobalException при ошибке парсинг-а
     */
    public JsonNode mapBody(String jsonBody)
    {
        try {
            return mapper.readTree(jsonBody);
        } catch (JacksonException e) {
            throw new GlobalException("Ошибка при чтении тела запроса");
        }
    }
}
