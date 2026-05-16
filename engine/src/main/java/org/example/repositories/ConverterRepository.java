package org.example.repositories;

import org.example.dbModels.ConverterTV;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConverterRepository extends JpaRepository<ConverterTV, Integer>
{
    ConverterTV getByName(String name);

    boolean existsByName(String name);
}
