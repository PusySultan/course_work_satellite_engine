package org.example.services;

import org.example.dto.LocalityDTO;
import org.example.dbModels.Locality;
import org.example.exceptions.GlobalException;
import org.example.repositories.LocalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class LocalityService extends BaseModelServiceClass
{
    @Autowired
    private LocalityRepository localityRepository;

    private final HashMap<String, Function<String, Locality>> localityFindMap = new HashMap<>();
    {
        localityFindMap.put("name", name -> localityRepository.getByName(name));
    }

    public Locality getLocalityByName(String name)
    {
        try {
            return localityFindMap.get("name").apply(name);
        } catch (Exception e) {
            throw new GlobalException("Ошибка поиска местности");
        }
    }

    public Locality getLocalityByLocalityBlock(String topJsonStr)
    {
        /// Конвертируем String в JsonNode
        JsonNode topJsonNode = super.mapBody(topJsonStr);

        /// Получаем блок о местности
        JsonNode localityBlock = super.getRequiredBlockByName(topJsonNode, "locality");

        /// Получаем поисковые параметры
        Map.Entry<String, String> searchParam = super.getSearchParam(localityBlock, Locality.class);

        /// Находим местность
        Locality locality = localityFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(locality)) {
            throw new GlobalException("Не существует населенного пункта по переданным параметрам");
        }

        return locality;
    }

    /**
     * Выполняет поиск в переданном JSON
     * поля по которому можно найти спутник
     * @param searchBlock JSON
     * @return имя поля и значение для поиска
     */
    private Map.Entry<String, String> getSearchParam(JsonNode searchBlock)
    {
        Field[] declaredFields = Locality.class.getDeclaredFields();

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

        throw new GlobalException("Нет корректного поля для поиска населенного пункта");
    }

    /**
     * Создает запись о местности в БД из переданной DTO
     * @param dto информация о местности
     * @throws GlobalException если местность с переданным именем уже существует
     */
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
