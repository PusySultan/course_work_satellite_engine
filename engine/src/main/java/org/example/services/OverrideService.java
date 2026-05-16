package org.example.services;

import org.example.exceptions.GlobalException;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class OverrideService extends BaseModelServiceClass
{

    public JsonNode getOverrideBlock(String topJsonStr)
    {
        /// Конвертируем String в JsonNode
        JsonNode topJsonNode = super.mapBody(topJsonStr);

        /// Получаем блок информации о блоке переопределений
        return super.getRequiredBlockByName(topJsonNode, "override");
    }
}
