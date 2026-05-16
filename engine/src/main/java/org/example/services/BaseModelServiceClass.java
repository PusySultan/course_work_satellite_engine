package org.example.services;

import org.example.dbModels.Satellite;
import org.example.exceptions.GlobalException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;

public abstract class BaseModelServiceClass
{
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Переводит строку в JsonNode
     * @param jsonBody входная строка
     * @return JsonNode
     * @throws GlobalException при ошибке парсинг-а
     */
    protected JsonNode mapBody(String jsonBody)
    {
        try {
            return mapper.readTree(jsonBody);
        } catch (JacksonException e) {
            throw new GlobalException("Ошибка при чтении тела запроса");
        }
    }

    /**
     * Возвращает блок информации из тела запроса с переданным именем
     * @param topJsonNode Объект в котором выполняется поиск
     * @param blockName Имя блока
     * @return JsonNode блок информации о спутнике
     * @throws GlobalException при отсутствии блока с информацией
     */
    protected JsonNode getRequiredBlockByName(JsonNode topJsonNode, String blockName)
    {
        if(!topJsonNode.has(blockName)) {
            throw new GlobalException("В теле запроса не указан блок - " + blockName);
        }

        return topJsonNode.get(blockName);
    }

    /**
     * Возвращает параметр поиска и имя параметра кроме id
     * @param searchBlock блок в котором ищется параметр и его имя
     * @param requiredClass класс по которому сопоставляются параметры
     * @return  Map. Entry<Имя поля, значение поля>
     */
    protected Map.Entry<String, String> getSearchParam(JsonNode searchBlock, Class<?> requiredClass)
    {
        Field[] declaredFields = requiredClass.getDeclaredFields();

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

        throw new GlobalException("Нет корректного поля для поиска " + requiredClass.getName());
    }
}
