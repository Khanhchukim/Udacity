package com.udacity.jdnd.course3.critter.entities;

import com.udacity.jdnd.course3.critter.enumerations.EmployeeSkill;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "t_schedules")
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "t_employee_schedule",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id")
    )
    private Set<Employee> employees;

    @ManyToMany
    @JoinTable(
            name = "t_pet_schedule",
            joinColumns = @JoinColumn(name = "pet_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id")
    )
    private Set<Pet> pets;

    @CollectionTable(name = "t_schedule_activities", joinColumns = @JoinColumn(name = "schedule_id"))
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = EmployeeSkill.class)
    private Set<EmployeeSkill> activities;

    public Schedule() {
    }

    public Schedule(Set<Employee> employees, Set<Pet> pets, LocalDate date, Set<EmployeeSkill> activities) {
        this.employees = employees;
        this.pets = pets;
        this.date = date;
        this.activities = activities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Set<Pet> getPets() {
        return pets;
    }

    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<EmployeeSkill> getActivities() {
        return activities;
    }

    public void setActivities(Set<EmployeeSkill> activities) {
        this.activities = activities;
    }
}
