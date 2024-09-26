package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.exceptions.InvalidScheduleException;
import com.udacity.jdnd.course3.critter.exceptions.ScheduleNotFoundException;
import com.udacity.jdnd.course3.critter.repositories.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule saveSchedule(Schedule schedule) {
        if (schedule.getDate() == null) {
            throw new InvalidScheduleException("Cannot create schedule without date");
        }
        if (schedule.getEmployees().isEmpty()) {
            throw new InvalidScheduleException("Cannot create schedule without employees");
        }
        if (schedule.getPets().isEmpty()) {
            throw new InvalidScheduleException("Cannot create schedule without pets");
        }
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getScheduleByPetId(long petId) {
        return scheduleRepository.findScheduleByPetId(petId)
                .orElseThrow(() -> new ScheduleNotFoundException("Pet with id " + petId + " does not have any schedule"));

    }

    public List<Schedule> getScheduleByEmployeeId(long employeeId) {
        return scheduleRepository.findScheduleByEmployeeId(employeeId)
                .orElseThrow(() -> new ScheduleNotFoundException("Employee with id " + employeeId + " does not have any schedule"));

    }

    public List<Schedule> getScheduleByCustomerId(long customerId) {
        return scheduleRepository.findScheduleByCustomerId(customerId)
                .orElseThrow(()-> new ScheduleNotFoundException("Customer with id " + customerId + " does not have any schedule"));
    }
}
