package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.mappers.UserMapper;
import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Employee;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import com.udacity.jdnd.course3.critter.services.EmployeeService;
import com.udacity.jdnd.course3.critter.services.PetService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private final CustomerService customerService;
    private final PetService petService;
    private final UserMapper userMapper;
    private final EmployeeService employeeService;

    public UserController(CustomerService customerService, PetService petService, UserMapper userMapper, EmployeeService employeeService) {
        this.customerService = customerService;
        this.petService = petService;
        this.userMapper = userMapper;
        this.employeeService = employeeService;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        Customer persistedCustomerEntity = this.customerService.save(this.userMapper.convertCustomerDTOToEntity(customerDTO));
        return this.userMapper.convertCustomerEntityToDTO(persistedCustomerEntity);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        return this.customerService.getAllCustomers().stream().map(this.userMapper::convertCustomerEntityToDTO).collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Customer customerByPetId = this.customerService.getCustomerByPetId(petId);
        return this.userMapper.convertCustomerEntityToDTO(customerByPetId);
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return this.userMapper.convertEmployeeEntityToDTO(
                this.employeeService.save(this.userMapper.convertEmployeeDTOToEntity(employeeDTO))
        );
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return this.userMapper.convertEmployeeEntityToDTO(
                this.employeeService.getEmployeeById(employeeId)
        );
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        this.employeeService.setEmployeeAvailability(employeeId, daysAvailable);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return this.employeeService.getAvailableEmployeesBySkills(employeeRequestDTO)
                .stream()
                .map(this.userMapper::convertEmployeeEntityToDTO)
                .collect(Collectors.toList());
    }
}
