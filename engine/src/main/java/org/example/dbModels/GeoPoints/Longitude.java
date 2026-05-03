package org.example.dbModels.GeoPoints;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Longitude implements GeoPoint
{
    @Column(name = "lon_degrees")
    private double degrees;

    @Column(name = "lon_minutes")
    private double minutes;

    @Column(name = "lon_seconds")
    private double seconds;
}
