package org.example.dbModels;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.ConverterTV_DTO;

@Entity             // - Отображение в БД
@NoArgsConstructor  // - Автоматическое создание конструктора по умолчанию
@AllArgsConstructor // - Конструктор со всеми параметрами
@Data               // - Автоматическая генерация геттеров, сеттеров, equals и hashCode
public class ConverterTV
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private double converterGain;

    public void createFromDTO(ConverterTV_DTO converterDTO)
    {
        this.name = converterDTO.getName();
        this.converterGain = converterDTO.getConverterGain();
    }
}
