package com.udacity.jdnd.course3.critter.mappers;

import com.udacity.jdnd.course3.critter.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.services.PetService;
import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.services.EmployeeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduleMapper {
    private final PetService petService;
    private final EmployeeService employeeService;

    public ScheduleMapper(PetService petService, EmployeeService employeeService) {
        this.petService = petService;
        this.employeeService = employeeService;
    }

    public ScheduleDTO convertScheduleEntityToDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setDate(schedule.getDate());

        scheduleDTO.setPetIds(schedule.getPets().stream()
                .map(Pet::getId)
                .collect(Collectors.toList()));

        scheduleDTO.setEmployeeIds(schedule.getEmployees().stream()
                .map(Employee::getId)
                .collect(Collectors.toList()));

        scheduleDTO.setActivities(schedule.getActivities());

        return scheduleDTO;

    }

    public Schedule convertScheduleDTOToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        schedule.setId(scheduleDTO.getId());
        schedule.setDate(scheduleDTO.getDate());

        Set<Pet> pets = scheduleDTO.getPetIds().stream()
                .map(this.petService::getPetById)
                .collect(Collectors.toSet());
        schedule.setPets(pets);

        Set<Employee> employees = scheduleDTO.getEmployeeIds().stream()
                .map(this.employeeService::getEmployeeById)
                .collect(Collectors.toSet());
        schedule.setEmployees(employees);

        schedule.setActivities(scheduleDTO.getActivities());

        return schedule;
    }
}
