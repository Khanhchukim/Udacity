package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.mappers.PetMapper;
import com.udacity.jdnd.course3.critter.services.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final PetMapper petMapper;

    public PetController(PetService petService, PetMapper petMapper) {
        this.petService = petService;
        this.petMapper = petMapper;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        return this.petMapper.convertPetEntityToDTO(
                this.petService.savePet(this.petMapper.convertPetDTOToEntity(petDTO))
        );
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        return this.petMapper.convertPetEntityToDTO(this.petService.getPetById(petId));
    }

    @GetMapping
    public List<PetDTO> getPets(){
        return this.petService.getAllPets()
                .stream()
                .map(this.petMapper::convertPetEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        return this.petService.getPetByOwnerId(ownerId)
                .stream()
                .map(this.petMapper::convertPetEntityToDTO)
                .collect(Collectors.toList());
    }
}
