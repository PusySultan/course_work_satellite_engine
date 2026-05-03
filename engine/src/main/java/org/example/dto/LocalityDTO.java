package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dbModels.GeoPoints.Latitude;
import org.example.dbModels.GeoPoints.Longitude;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalityDTO
{
    private String name;
    private Latitude latitude;
    private Longitude longitude;
}
