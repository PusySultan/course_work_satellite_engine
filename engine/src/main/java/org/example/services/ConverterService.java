package org.example.services;

import org.example.dbModels.ConverterTV;
import org.example.dto.ConverterTV_DTO;
import org.example.exceptions.GlobalException;
import org.example.repositories.ConverterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class ConverterService  extends BaseModelServiceClass
{
    @Autowired
    private ConverterRepository converterRepository;

    private final HashMap<String, Function<String, ConverterTV>> converterFindMap = new HashMap<>();
    {
        converterFindMap.put("name", name -> converterRepository.getByName(name));
    }

    public ConverterTV getConverterByName(String name)
    {
        try {
            return converterFindMap.get("name").apply(name);
        } catch (Exception e) {
            throw new GlobalException("Ошибка при поиске конвертера");
        }
    }

    public void createConverter(ConverterTV_DTO converterDTO)
    {
        if (converterRepository.existsByName(converterDTO.getName())) {
            throw new GlobalException("Данный конвертер уже существует, для обновления воспользуйтесь методом PUT");
        }

        ConverterTV converterTV = new ConverterTV();
        converterTV.createFromDTO(converterDTO);

        converterRepository.save(converterTV);
    }

    public ConverterTV getConverter(String topJsonStr)
    {
        /// Конвертируем String в JsonNode
        JsonNode topJsonNode = super.mapBody(topJsonStr);

        /// Получаем блок информации о конверторе
        JsonNode converterBlock = super.getRequiredBlockByName(topJsonNode, "converter");

        /// Получаем поисковые параметры
        Map.Entry<String, String> searchParam = super.getSearchParam(converterBlock, ConverterTV.class);

        /// Находим антенну
        ConverterTV converterTV = converterFindMap
                .get(searchParam.getKey()).apply(searchParam.getValue());

        if(Objects.isNull(converterTV)) {
            throw new GlobalException("Не существует конвертера с переданными переданным параметрам");
        }

        return converterTV;
    }
}
