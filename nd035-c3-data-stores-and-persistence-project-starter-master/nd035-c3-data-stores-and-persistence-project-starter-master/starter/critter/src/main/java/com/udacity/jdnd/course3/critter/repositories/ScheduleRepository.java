package com.udacity.jdnd.course3.critter.repositories;

import com.udacity.jdnd.course3.critter.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s JOIN s.pets p WHERE p.id = :petId GROUP BY s")
    Optional<List<Schedule>> findScheduleByPetId(@Param("petId") Long petId);

    @Query("SELECT s FROM Schedule s JOIN s.employees e WHERE e.id = :employeeId GROUP BY s")
    Optional<List<Schedule>> findScheduleByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT s FROM Schedule s JOIN s.pets p WHERE p.owner = (SELECT c FROM Customer c WHERE c.id = :customerId) GROUP BY s")
    Optional<List<Schedule>> findScheduleByCustomerId(@Param("customerId") Long customerId);

}
