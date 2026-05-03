package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dbModels.GeoPoints.Latitude;
import org.example.dbModels.GeoPoints.Longitude;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SatelliteDTO
{
    private String satelliteName;
    private Latitude latitude;
    private Longitude longitude;
    private double EIRP;                /// ЭИИМ
    private double carrierFrequency;    /// Несущая в ГГц
}
