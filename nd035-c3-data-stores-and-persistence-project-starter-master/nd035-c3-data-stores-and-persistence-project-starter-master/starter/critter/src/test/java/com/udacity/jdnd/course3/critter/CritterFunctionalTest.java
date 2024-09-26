package com.udacity.jdnd.course3.critter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.udacity.jdnd.course3.critter.controllers.PetController;
import com.udacity.jdnd.course3.critter.controllers.UserController;
import com.udacity.jdnd.course3.critter.dto.*;
import com.udacity.jdnd.course3.critter.enumerations.EmployeeSkill;
import com.udacity.jdnd.course3.critter.enumerations.PetType;
import com.udacity.jdnd.course3.critter.controllers.ScheduleController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is a set of functional tests to validate the basic capabilities desired for this application.
 * Students will need to configure the application to run these tests by adding application.properties file
 * to the test/resources directory that specifies the datasource. It can run using an in-memory H2 instance
 * and should not try to re-use the same datasource used by the rest of the app.
 *
 * These tests should all pass once the project is complete.
 */
@Transactional
@SpringBootTest(classes = CritterApplication.class)
public class CritterFunctionalTest {

    @Autowired
    private UserController userController;

    @Autowired
    private PetController petController;

    @Autowired
    private ScheduleController scheduleController;

    @Test
    public void testCreateCustomer(){
        CustomerDTO customerDTO = createCustomerDTO();

// Lưu khách hàng và kiểm tra khách hàng mới tạo
        CustomerDTO savedCustomer = userController.saveCustomer(customerDTO);
        CustomerDTO retrievedCustomer = userController.getAllCustomers().get(0);

// Kiểm tra tên và ID có khớp nhau không
        Assertions.assertEquals(savedCustomer.getName(), customerDTO.getName(), "Customer name should match the original name");
        Assertions.assertEquals(savedCustomer.getId(), retrievedCustomer.getId(), "Customer ID should match the retrieved customer ID");
        Assertions.assertTrue(retrievedCustomer.getId() > 0, "Retrieved customer ID should be greater than 0");

    }

    @Test
    public void testCreateEmployee(){
        EmployeeDTO employeeDTO = createEmployeeDTO();

        //Saved new employee
        EmployeeDTO savedEmployee = userController.saveEmployee(employeeDTO);

         //Get info employee by Id
        EmployeeDTO retrievedEmployee = userController.getEmployee(savedEmployee.getId());

         //Check skill and Id of employee
        Assertions.assertEquals(employeeDTO.getSkills(), savedEmployee.getSkills(), "Employee skills should match the original skills");
        Assertions.assertEquals(savedEmployee.getId(), retrievedEmployee.getId(), "Employee ID should match the retrieved employee ID");
        Assertions.assertTrue(retrievedEmployee.getId() > 0, "Retrieved employee ID should be greater than 0");
    }

    @Test
    public void testAddPetsToCustomer() {
        // Create and save a new customer.
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO savedCustomer = userController.saveCustomer(customerDTO);

        // Create and save a new pet, link the pet to the customer that was created
        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(savedCustomer.getId());
        PetDTO savedPet = petController.savePet(petDTO);

        // Ensure that the pet contains the customer's ID
        PetDTO retrievedPet = petController.getPet(savedPet.getId());
        Assertions.assertEquals(retrievedPet.getId(), savedPet.getId(), "Retrieved pet ID should match the saved pet ID");
        Assertions.assertEquals(retrievedPet.getOwnerId(), savedCustomer.getId(), "Pet owner ID should match the customer's ID");

        // Ensure that it's possible to retrieve the list of pets by the owner's ID
        List<PetDTO> petsByOwner = petController.getPetsByOwner(savedCustomer.getId());
        Assertions.assertEquals(savedPet.getId(), petsByOwner.get(0).getId(), "Pet ID should match the pet retrieved by owner");
        Assertions.assertEquals(savedPet.getName(), petsByOwner.get(0).getName(), "Pet name should match the name retrieved by owner");

        // Check to ensure that the current customer has pets
        CustomerDTO retrievedCustomer = userController.getAllCustomers().get(0);
        Assertions.assertTrue(retrievedCustomer.getPetIds() != null && !retrievedCustomer.getPetIds().isEmpty(), "Customer should have at least one pet");
        Assertions.assertEquals(retrievedCustomer.getPetIds().get(0), retrievedPet.getId(), "Customer's pet ID should match the retrieved pet's ID");

    }

    @Test
    public void testFindPetsByOwner() {
        // Create and Save new customer
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO savedCustomer = userController.saveCustomer(customerDTO);

       // Create and save pets with customer
        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(savedCustomer.getId());
        PetDTO savedPet1 = petController.savePet(petDTO);

        petDTO.setType(PetType.DOG);
        petDTO.setName("DogName");
        PetDTO savedPet2 = petController.savePet(petDTO);

        // Retrieve the list of all pets of the customer and check the quantity
        List<PetDTO> pets = petController.getPetsByOwner(savedCustomer.getId());
        Assertions.assertEquals(pets.size(), 2, "Customer should have 2 pets");

        // Check the information of each pet using a loop
        List<PetDTO> savedPets = Arrays.asList(savedPet1, savedPet2);
        for (int i = 0; i < pets.size(); i++) {
            PetDTO pet = pets.get(i);
            PetDTO savedPet = savedPets.get(i);
            Assertions.assertEquals(pet.getOwnerId(), savedCustomer.getId(), "Owner ID should match the customer's ID");
            Assertions.assertEquals(pet.getId(), savedPet.getId(), "Pet ID should match the saved pet's ID");
        }
    }

    @Test
    public void testFindOwnerByPet() {
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO savedCustomer = userController.saveCustomer(customerDTO);

        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(savedCustomer.getId());
        PetDTO savedPet = petController.savePet(petDTO);

        CustomerDTO owner = userController.getOwnerByPet(savedPet.getId());

        Assertions.assertEquals(owner.getId(), savedCustomer.getId(), "Owner ID should match the customer's ID");
        Assertions.assertTrue(owner.getPetIds().contains(savedPet.getId()), "Owner's pet list should contain the saved pet's ID");
    }

    @Test
    public void testChangeEmployeeAvailability() {
        EmployeeDTO employeeDTO = createEmployeeDTO();
        EmployeeDTO savedEmployee = userController.saveEmployee(employeeDTO);

        Assertions.assertNull(savedEmployee.getDaysAvailable(), "New employee should have no availability set");

        Set<DayOfWeek> availability = Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
        userController.setAvailability(availability, savedEmployee.getId());

        EmployeeDTO updatedEmployee = userController.getEmployee(savedEmployee.getId());
        Assertions.assertEquals(availability, updatedEmployee.getDaysAvailable(), "Employee availability should match the set availability");

    }

    @Test
    public void testFindEmployeesByServiceAndTime() {
        List<EmployeeDTO> employees = Arrays.asList(createEmployeeDTO(), createEmployeeDTO(), createEmployeeDTO());

        employees.get(0).setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        employees.get(1).setDaysAvailable(Sets.newHashSet(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        employees.get(2).setDaysAvailable(Sets.newHashSet(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

        employees.get(0).setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
        employees.get(1).setSkills(Sets.newHashSet(EmployeeSkill.PETTING, EmployeeSkill.WALKING));
        employees.get(2).setSkills(Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING));

        List<EmployeeDTO> savedEmployees = employees.stream()
                .map(userController::saveEmployee)
                .collect(Collectors.toList());

        List<EmployeeRequestDTO> requests = Arrays.asList(
                createEmployeeRequestDTO(LocalDate.of(2019, 12, 25), Sets.newHashSet(EmployeeSkill.PETTING)), // Matches emp1 and emp2
                createEmployeeRequestDTO(LocalDate.of(2019, 12, 27), Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING)) // Matches emp3
        );

        List<Set<Long>> expectedEmployeeIdsList = Arrays.asList(
                Sets.newHashSet(savedEmployees.get(0).getId(), savedEmployees.get(1).getId()), // emp1 and emp2
                Sets.newHashSet(savedEmployees.get(2).getId()) // emp3
        );

        for (int i = 0; i < requests.size(); i++) {
            Set<Long> actualEmployeeIds = userController.findEmployeesForService(requests.get(i)).stream()
                    .map(EmployeeDTO::getId)
                    .collect(Collectors.toSet());

            Assertions.assertEquals(actualEmployeeIds, expectedEmployeeIdsList.get(i), "Employee list for request " + (i + 1) + " should match expected");
        }
    }

    @Test
    public void testSchedulePetsForServiceWithEmployee() {
        EmployeeDTO employeeTemp = createEmployeeDTO();
        employeeTemp.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        EmployeeDTO savedEmployee = userController.saveEmployee(employeeTemp);

        CustomerDTO savedCustomer = userController.saveCustomer(createCustomerDTO());
        PetDTO petTemp = createPetDTO();
        petTemp.setOwnerId(savedCustomer.getId());
        PetDTO savedPet = petController.savePet(petTemp);

        LocalDate scheduleDate = LocalDate.of(2019, 12, 25);
        List<Long> petIds = Lists.newArrayList(savedPet.getId());
        List<Long> employeeIds = Lists.newArrayList(savedEmployee.getId());
        Set<EmployeeSkill> skillSet = Sets.newHashSet(EmployeeSkill.PETTING);

        scheduleController.createSchedule(createScheduleDTO(petIds, employeeIds, scheduleDate, skillSet));
        ScheduleDTO retrievedSchedule = scheduleController.getAllSchedules().get(0);

        List<Object> actualValues = Arrays.asList(
                retrievedSchedule.getActivities(),
                retrievedSchedule.getDate(),
                retrievedSchedule.getEmployeeIds(),
                retrievedSchedule.getPetIds()
        );

        List<Object> expectedValues = Arrays.asList(
                skillSet,
                scheduleDate,
                employeeIds,
                petIds
        );

        for (int i = 0; i < actualValues.size(); i++) {
            Assertions.assertEquals(actualValues.get(i), expectedValues.get(i), "Mismatch at index " + i);
        }
    }

    @Test
    public void testFindScheduleByEntities() {
        // Tạo các lịch trình với các nhân viên, thú cưng, và hoạt động khác nhau
        ScheduleDTO sched1 = populateSchedule(1, 2, LocalDate.of(2019, 12, 25), Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.WALKING));
        ScheduleDTO sched2 = populateSchedule(3, 1, LocalDate.of(2019, 12, 26), Sets.newHashSet(EmployeeSkill.PETTING));

// Tạo lịch trình thứ 3 chia sẻ nhân viên với sched1 và thú cưng với sched2
        ScheduleDTO sched3 = new ScheduleDTO();
        sched3.setEmployeeIds(sched1.getEmployeeIds());
        sched3.setPetIds(sched2.getPetIds());
        sched3.setActivities(Sets.newHashSet(EmployeeSkill.SHAVING, EmployeeSkill.PETTING));
        sched3.setDate(LocalDate.of(2020, 3, 23));
        scheduleController.createSchedule(sched3);

// Kiểm tra lịch trình của nhân viên và thú cưng
        checkSchedulesForEmployee(sched1.getEmployeeIds().get(0), sched1, sched3);
        checkSchedulesForEmployee(sched2.getEmployeeIds().get(0), sched2);

        checkSchedulesForPet(sched1.getPetIds().get(0), sched1);
        checkSchedulesForPet(sched2.getPetIds().get(0), sched2, sched3);

        checkSchedulesForCustomer(userController.getOwnerByPet(sched1.getPetIds().get(0)).getId(), sched1);
        checkSchedulesForCustomer(userController.getOwnerByPet(sched2.getPetIds().get(0)).getId(), sched2, sched3);
    }


    private static EmployeeDTO createEmployeeDTO() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName("TestEmployee");
        employeeDTO.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
        return employeeDTO;
    }
    private static CustomerDTO createCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("TestEmployee");
        customerDTO.setPhoneNumber("123-456-789");
        return customerDTO;
    }

    private static PetDTO createPetDTO() {
        PetDTO petDTO = new PetDTO();
        petDTO.setName("TestPet");
        petDTO.setType(PetType.CAT);
        return petDTO;
    }

    private static EmployeeRequestDTO createEmployeeRequestDTO() {
        EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setDate(LocalDate.of(2019, 12, 25));
        employeeRequestDTO.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.WALKING));
        return employeeRequestDTO;
    }

    private static ScheduleDTO createScheduleDTO(List<Long> petIds, List<Long> employeeIds, LocalDate date, Set<EmployeeSkill> activities) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setPetIds(petIds);
        scheduleDTO.setEmployeeIds(employeeIds);
        scheduleDTO.setDate(date);
        scheduleDTO.setActivities(activities);
        return scheduleDTO;
    }

    private ScheduleDTO populateSchedule(int numEmployees, int numPets, LocalDate date, Set<EmployeeSkill> activities) {
        List<Long> employeeIds = IntStream.range(0, numEmployees)
                .mapToObj(i -> createEmployeeDTO())
                .map(e -> {
                    e.setSkills(activities);
                    e.setDaysAvailable(Sets.newHashSet(date.getDayOfWeek()));
                    return userController.saveEmployee(e).getId();
                }).collect(Collectors.toList());
        CustomerDTO cust = userController.saveCustomer(createCustomerDTO());
        List<Long> petIds = IntStream.range(0, numPets)
                .mapToObj(i -> createPetDTO())
                .map(p -> {
                    p.setOwnerId(cust.getId());
                    return petController.savePet(p).getId();
                }).collect(Collectors.toList());
        return scheduleController.createSchedule(createScheduleDTO(petIds, employeeIds, date, activities));
    }

    private static void compareSchedules(ScheduleDTO sched1, ScheduleDTO sched2) {
        Assertions.assertEquals(sched1.getPetIds(), sched2.getPetIds());
        Assertions.assertEquals(sched1.getActivities(), sched2.getActivities());
        Assertions.assertEquals(sched1.getEmployeeIds(), sched2.getEmployeeIds());
        Assertions.assertEquals(sched1.getDate(), sched2.getDate());
    }

    private EmployeeRequestDTO createEmployeeRequestDTO(LocalDate date, Set<EmployeeSkill> skills) {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setDate(date);
        request.setSkills(skills);
        return request;
    }

    private void checkSchedulesForEmployee(Long employeeId, ScheduleDTO... expectedSchedules) {
        List<ScheduleDTO> retrievedSchedules = scheduleController.getScheduleForEmployee(employeeId);
        for (int i = 0; i < expectedSchedules.length; i++) {
            compareSchedules(expectedSchedules[i], retrievedSchedules.get(i));
        }
    }

    private void checkSchedulesForPet(Long petId, ScheduleDTO... expectedSchedules) {
        List<ScheduleDTO> retrievedSchedules = scheduleController.getScheduleForPet(petId);
        for (int i = 0; i < expectedSchedules.length; i++) {
            compareSchedules(expectedSchedules[i], retrievedSchedules.get(i));
        }
    }

    private void checkSchedulesForCustomer(Long customerId, ScheduleDTO... expectedSchedules) {
        List<ScheduleDTO> retrievedSchedules = scheduleController.getScheduleForCustomer(customerId);
        for (int i = 0; i < expectedSchedules.length; i++) {
            compareSchedules(expectedSchedules[i], retrievedSchedules.get(i));
        }
    }
}
