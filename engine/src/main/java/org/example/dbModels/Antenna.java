package org.example.dbModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.exceptions.GlobalException;

import static java.lang.Math.*;
import static java.lang.Math.pow;

@Entity             // - Отображение в БД
@NoArgsConstructor  // - Автоматическое создание конструктора по умолчанию
@AllArgsConstructor // - Конструктор со всеми параметрами
@Data               // - Автоматическая генерация геттеров, сеттеров, equals и hashCode
public class Antenna
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "diameter")
    private double antennaDiameter;

    @Column(name = "efficiency")
    private double antennaEfficiency;

    /**
     * Рассчитывает шумовую температуру антенны, обусловленную фоновым шумом
     * @param seatAngle угол места в градусах
     * @return шумовую температуру в Кельвинах
     */
    public double getAntennaNoiseTemperature(double seatAngle)
    {
        try {
            return 15 + (30 / antennaDiameter) + (180 / seatAngle);
        } catch (Exception e) {
            throw new GlobalException("Ошибка при расчете шумовой температуры антенны");
        }
    }

    /**
     * Рассчитывает усиление антенны в Дб
     * @param waveLength длина волны на частоте несущей
     * @return коэффициент усиления антенны
     */
    private double getAntennaGain(double waveLength)
    {
        try {
            return 10 * log10(
                    (pow(PI * this.antennaDiameter, 2) *  this.antennaEfficiency) / (100 * pow(waveLength, 2))
            );
        } catch (Exception e) {
            throw new GlobalException("Ошибка при расчете усиления антенны");
        }
    }
}
