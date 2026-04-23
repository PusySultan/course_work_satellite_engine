package org.example.dbModels.GeoPoints;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Latitude
{
    @Column(name = "lat_degrees")
    private Integer degrees;

    @Column(name = "lat_minutes")
    private Integer minutes;

    @Column(name = "lat_seconds")
    private Integer seconds;
}
