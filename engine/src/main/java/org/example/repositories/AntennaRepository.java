package org.example.repositories;

import org.example.dbModels.Antenna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AntennaRepository extends JpaRepository<Antenna, Integer>
{
}
