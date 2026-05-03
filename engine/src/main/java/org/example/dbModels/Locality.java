package org.example.dbModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.LocalityDTO;
import org.example.dbModels.GeoPoints.Latitude;
import org.example.dbModels.GeoPoints.Longitude;

@Entity             // - Отображение в БД
@NoArgsConstructor  // - Автоматическое создание конструктора по умолчанию
@AllArgsConstructor // - Конструктор со всеми параметрами
@Data               // - Автоматическая генерация геттеров, сеттеров, equals и hashCode
public class Locality
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    /**
    *  Широта (спутника) в градусах
    */
    @Embedded
    private Latitude latitude;

    /**
     *  Долгота (спутника) в градусах
     */
    @Embedded
    private Longitude longitude;

    public void createFromDto(LocalityDTO dto)
    {
        this.name = dto.getName();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
    }
}
