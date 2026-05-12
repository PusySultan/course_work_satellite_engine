package org.example.services;

import org.example.dbModels.GeoPoints.GeoPoint;
import org.example.dbModels.Locality;
import org.example.dbModels.Satellite;
import org.example.exceptions.GlobalException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;

import static java.lang.Math.*;

@Service
public class EngineService
{
    /**
    *  Отношение радиуса геостационарной орбиты к радиусу экватора Земли
    */
    private final double m = 6.61d;
    double antennaDiameter = 0d;
    double antennaEfficiency = 0d;
    private double A = 0d;
    private double B = 0d;
    private double NF = 0.3d;

    private JsonNode overrideBlock = null;

    public HashMap<String, Double> engine(Locality locality, Satellite satellite, JsonNode overrideBlock)
    {
        this.overrideBlock = overrideBlock;

        double seatAngle = getSeatAngle(locality, satellite);
        double trueAzimuth = getTrueAzimuth();
        double distanceToSatellite = getDistanceToSatellite();

        double waveLength = getWaveLength(satellite);
        double Lfs = getSignalTransmissionLossesInFreeSpace(distanceToSatellite, waveLength);
        double Flnb = getNoiseFactorConverterUnit();
        double Tlnb = getEquivalentNoiseTemperature(Flnb);
        double Tant = getAntennaNoiseTemperature(seatAngle);
        double Ttot = Tlnb + Tant;

        double Ga = getAntennaGain(waveLength);
        double qualityFactor = getQualityFactor(Ga, Ttot);

        return calculateTrack(satellite.getEIRP(), Lfs, Ga);
    }

    private HashMap<String, Double> calculateTrack(double eirp, double lfs, double Ga)
    {
       try {
           HashMap<String, Double> track = new HashMap<>();

           double antennaInput = eirp - lfs;
           double antennaOutput = antennaInput + Ga;
           double localeTrack = antennaOutput - 3;
           double converterOutput = localeTrack + overrideBlock.get("gainConverter").asDouble();

           track.put("1", eirp);
           track.put("2", antennaInput);
           track.put("3", antennaOutput);
           track.put("4", localeTrack);
           track.put("5", converterOutput);

           return track;

       } catch (Exception e) {
           throw new GlobalException ("Ошибка в расчете тракта");
       }
    }

    /**
     * Рассчитывает коэффициент добротности
     * @param Ga Коэффициент усиления антенны (Дб)
     * @param Ttot Общая температура приемной системы (Кельвин)
     * @return коэффициент добротности
     */
    private double getQualityFactor(double Ga, double Ttot)
    {
        return 10 * log10((pow(10, (Ga/10)))/Ttot);
    }

    /**
     * Рассчитывает усиление антенны в Дб
     * @param waveLength длина волны на частоте несущей
     * @return коэффициент усиления антенны
     */
    private double getAntennaGain(double waveLength)
    {
        double antennaEfficiency = 0d;

        try {
            antennaEfficiency = overrideBlock.get("antennaEfficiency").asDouble();
            return 10 * log10(((pow(PI * this.antennaDiameter, 2)) *  antennaEfficiency) / (100 * pow(waveLength, 2)));
        } catch (Exception e) {
            throw new GlobalException("Ошибка при расчете усиления антенны");
        }
    }


    /**
     * Рассчитывает шумовую температуру антенны, обусловленную фоновым шумом
     * @param seatAngle угол места в радианах
     * @return шумовую температуру в Кельвинах
     */
    private double getAntennaNoiseTemperature(double seatAngle)
    {
        try {
            this.antennaDiameter = overrideBlock.get("antennaDiameter").asDouble();
            return 15 + (30 / antennaDiameter) + (180 / radianToDegree(seatAngle));
        } catch (Exception e) {
            throw new GlobalException("Ошибка при расчете шумовой температуры антенны");
        }
    }

    /**
     * Возвращает эквивалентную шумовую температуру выраженную в Кельвинах
     * @param Flnb фактор шума блока конвертора
     * @return эквивалентную шумовую температуру
     */
    private double getEquivalentNoiseTemperature(double Flnb)
    {
        return 290 * (Flnb - 1);
    }

    /**
     * Возвращает фактор шума блока конвертера
     * @return фактор шума
     */
    private double getNoiseFactorConverterUnit()
    {
        return pow(10, (NF/10));
    }

    /**
     * Вычисляет потери при прохождении сигнала в свободном пространстве
     * выраженные в децибелах
     * @param distance Расстояние до спутника
     * @param waveLength несущая длина волны
     * @return потери
     */
    private double getSignalTransmissionLossesInFreeSpace(double distance, double waveLength)
    {
        return 20 * log10((4000 * PI * distance) / (waveLength));
    }

    /**
     * Рассчитывает длину волны в метрах
     * @param satellite Информация о спутнике
     * @return длину волны в метрах
     */
    private double getWaveLength(Satellite satellite)
    {
        return (3 * Math.pow(10, 8)) / (satellite.getFrequency() * Math.pow(10, 9));
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
     * @return истинный азимут в радианах
     */
    private double getTrueAzimuth()
    {
        return PI + atan((tan(B))/(sin(A)));
    }

    /**
     * Возвращает угол места для заданных спутника и точки на земле
     * @param locality Точка на земле
     * @param satellite Спутник
     * @return угол места в радианах
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

    /**
     * Перевод из радиана в градусы
     * @param angle радиан
     * @return градусы
     */
    private double radianToDegree(double angle)
    {
        return (angle * 180d) / PI;
    }
}
