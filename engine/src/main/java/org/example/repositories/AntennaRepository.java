package org.example.repositories;

import org.example.dbModels.Antenna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntennaRepository extends JpaRepository<Antenna, Integer>
{
}
