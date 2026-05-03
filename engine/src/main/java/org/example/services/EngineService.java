package org.example.services;

import org.example.dbModels.GeoPoints.GeoPoint;
import org.example.dbModels.Locality;
import org.example.dbModels.Satellite;
import org.springframework.stereotype.Service;

import static java.lang.Math.*;

@Service
public class EngineService
{
    /**
    *  Отношение радиуса геостационарной орбиты к радиусу экватора Земли
    */
    private final double m = 6.61;
    private double A = 0;
    private double B = 0;

    public void engine(Locality locality, Satellite satellite)
    {
        double seatAngle = getSeatAngle(locality, satellite);
        double trueAzimuth = getTrueAzimuth();
        double distanceToSatellite = getDistanceToSatellite();
    }

    /**
     * Рассчитывает расстояние до спутника связи в километрах
     * @return расстояние в километрах
     */
    private double getDistanceToSatellite()
    {
        return 6378.16d * sqrt((m * m) + 1 - 2 * m * cos(A) * cos(B));
    }

    /**
     * Рассчитывает истинный азимут, представляющий собой угол направления,
     * указывающего на выбранный спутник, который отсчитывается от истинного севера
     * @return истинный азимут в градусах
     */
    private double getTrueAzimuth()
    {
        return 180 + atan((tan(B))/(sin(A)));
    }

    /**
     * Возвращает угол места для заданных спутника и точки на земле
     * @param locality Точка на земле
     * @param satellite Спутник
     * @return угол места в градусах
     */
    private double getSeatAngle(Locality locality, Satellite satellite)
    {
        /// Широта места нахождения земной станции
        A = geoPointToRadian(locality.getLatitude());

        /// Восточная долгота земной станции минус восточная долгота спутника
        B = geoPointToRadian(locality.getLongitude()) - geoPointToRadian(satellite.getLongitude());

        double cosA = cos(A);
        double cosB = cos(B);
        double cos2A = cosA * cosA;
        double cos2B = cosB * cosB;

        return atan((m * cosA * cosB - 1 ) / (m * sqrt(1 - cos2A * cos2B)));
    }

    /**
     * @param geoPoint Точку для которой необходим расчет
     * @return количество радиан в угле
     */
    private double geoPointToRadian(GeoPoint geoPoint)
    {
        return toRadians(
                geoPoint.getDegrees(),
                geoPoint.getMinutes(),
                geoPoint.getSeconds());
    }

    /**
     * Переводит градусы, минуты, секунды в радиан
     * @param degree количество градусов в угле
     * @param minutes количество минут в угле
     * @param sec количество секунд в угле
     * @return угол переведенный в радианы
     */
    private double toRadians(double degree,double minutes, double sec)
    {
        double allDegree = degree + (minutes / 60d) + (sec / 3600d);
        return (PI * allDegree) / 180;
    }
}
