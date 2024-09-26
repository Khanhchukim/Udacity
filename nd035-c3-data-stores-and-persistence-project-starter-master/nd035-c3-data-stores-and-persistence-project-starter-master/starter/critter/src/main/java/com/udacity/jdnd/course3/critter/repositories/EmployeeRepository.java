package com.udacity.jdnd.course3.critter.repositories;

import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.enumerations.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e " +
            "JOIN e.skills s " +
            "JOIN e.daysAvailable d " +
            "WHERE d = :dayOfWeek " +
            "AND s IN :skills " +
            "GROUP BY e " +
            "HAVING COUNT(DISTINCT s) = :requiredSkillCount")
    Optional<List<Employee>> findEmployeesBySkillsAndDayOfWeek(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("skills") Set<EmployeeSkill> skills,
            @Param("requiredSkillCount") long requiredSkillCount);
}
