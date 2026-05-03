package org.example.dbModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dbModels.GeoPoints.Latitude;
import org.example.dbModels.GeoPoints.Longitude;
import org.example.dto.SatelliteDTO;

@Entity             // - Отображение в БД
@NoArgsConstructor  // - Автоматическое создание конструктора по умолчанию
@AllArgsConstructor // - Конструктор со всеми параметрами
@Data               // - Автоматическая генерация геттеров, сеттеров, equals и hashCode
public class Satellite
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String satelliteName;
    private Integer EIRP;                /// ЭИИМ
    private Integer carrierFrequency;    /// Несущая в ГГц

    private Latitude latitude;   /// Широта (спутника)
    private Longitude longitude;  /// Долгота (спутника)

    public void createFromDTO(SatelliteDTO dto)
    {
        this.satelliteName = dto.getSatelliteName();
        this.EIRP = dto.getEIRP();
        this.carrierFrequency = dto.getCarrierFrequency();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
    }
}
