package com.udacity.vehicles.api;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarNotFoundException;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@ApiResponses(value = {
        @ApiResponse(code = 404, message = "Car not found"),
        @ApiResponse(code = 500, message = "Internal server error")
})
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Retrieves a list of all available vehicles.
     * @return list of vehicle resources
     */
    @GetMapping
    public Resources<Resource<Car>> list() {
        List<Resource<Car>> carResources = carService.list().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(carResources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Fetches details of a specific vehicle by its ID.
     * @param id the vehicle ID
     * @return detailed information about the vehicle
     */
    @GetMapping("/{id}")
    public Resource<Car> get(@PathVariable Long id) {
        Car car = carService.findById(id);
        return assembler.toResource(car);
    }

    /**
     * Adds a new vehicle to the system.
     * @param car the new vehicle data
     * @return response entity indicating the car creation status
     * @throws URISyntaxException in case of URI syntax issues
     */
    @PostMapping
    public ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        Car newCar = carService.save(car);
        Resource<Car> carResource = assembler.toResource(newCar);
        return ResponseEntity.created(new URI(carResource.getId().expand().getHref())).body(carResource);
    }

    /**
     * Updates an existing vehicle in the system by its ID.
     * @param id the vehicle ID
     * @param car the updated vehicle data
     * @return response entity confirming the update
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        car.setId(id);
        Car updatedCar = carService.save(car);
        Resource<Car> carResource = assembler.toResource(updatedCar);
        return ResponseEntity.ok(carResource);
    }

    /**
     * Deletes a vehicle by its ID from the system.
     * @param id the vehicle ID
     * @return response entity confirming the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content
    }
}


