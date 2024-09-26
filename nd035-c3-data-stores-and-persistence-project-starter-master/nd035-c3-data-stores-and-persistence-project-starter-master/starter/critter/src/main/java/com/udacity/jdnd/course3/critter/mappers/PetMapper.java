package com.udacity.jdnd.course3.critter.mappers;

import com.udacity.jdnd.course3.critter.dto.PetDTO;
import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PetMapper {
    private final CustomerService customerService;

    public PetMapper(CustomerService customerService) {
        this.customerService = customerService;
    }

    public PetDTO convertPetEntityToDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(pet.getId());
        petDTO.setName(pet.getName());
        petDTO.setBirthDate(pet.getBirthDate());
        petDTO.setNotes(pet.getNotes());
        petDTO.setType(pet.getType());

        petDTO.setOwnerId(pet.getOwner() != null ? pet.getOwner().getId() : null);

        return petDTO;
    }


    public Pet convertPetDTOToEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        pet.setId(petDTO.getId());
        pet.setName(petDTO.getName());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setNotes(petDTO.getNotes());
        pet.setType(petDTO.getType());

        if (petDTO.getOwnerId() != null) {
            pet.setOwner(this.customerService.getCustomerById(petDTO.getOwnerId()));
        }

        return pet;
    }

}
