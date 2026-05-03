package org.example.repositories;

import org.example.dbModels.Satellite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatelliteRepository extends JpaRepository<Satellite, Integer>
{
    Satellite getBySatelliteName(String satelliteName);

    boolean existsBySatelliteName(String satelliteName);
}
