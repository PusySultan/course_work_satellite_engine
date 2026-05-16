package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntennaDTO
{
    private String name;

    /// Диаметр антенны
    private double diameter;

    /// КПД антенны
    private double efficiency;
}
