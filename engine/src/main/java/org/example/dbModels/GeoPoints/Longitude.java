package org.example.dbModels.GeoPoints;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Longitude
{
    @Column(name = "lon_degrees")
    private Integer degrees;

    @Column(name = "lon_minutes")
    private Integer minutes;

    @Column(name = "lon_seconds")
    private Integer seconds;
}
