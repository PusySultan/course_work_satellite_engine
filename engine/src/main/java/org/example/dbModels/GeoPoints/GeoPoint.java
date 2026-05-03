package org.example.dbModels.GeoPoints;

public interface GeoPoint
{
    /**
     * @return Количество градусов в угле
     */
    double getDegrees();

    /**
     * @return Количество минут в угле
     */
    double getMinutes();

    /**
     * @return Количество секунд в угле
     */
    double getSeconds();
}
