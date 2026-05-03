package org.example.dbModels.GeoPoints;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Latitude implements GeoPoint
{
    @Column(name = "lat_degrees")
    private double degrees;

    @Column(name = "lat_minutes")
    private double minutes;

    @Column(name = "lat_seconds")
    private double seconds;
}
