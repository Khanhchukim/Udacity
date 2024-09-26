package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.Customer;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.exceptions.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetNotFoundException;
import com.udacity.jdnd.course3.critter.exceptions.PetWithoutOwnerException;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;

    public PetService(PetRepository petRepository, CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(String.format("Pet with id %s is not found", id)));
    }

    public List<Pet> getPetByOwnerId(long ownerId) {
        return petRepository.findPetsByOwnerId(ownerId)
                .orElseThrow(() -> new PetNotFoundException(String.format("Customer with id %s does not own any pet.", ownerId)));
    }

    public Pet savePet(Pet pet) {
        if (pet.getOwner() == null) {
            throw new PetWithoutOwnerException("A pet MUST belong to a customer");
        }

        Long ownerId = pet.getOwner().getId();
        Customer customer = customerRepository.findById(ownerId)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer with id %s is not found.", ownerId)));

        Pet persistedPet = petRepository.save(pet);

        // Upate list pet
        Set<Pet> pets = customer.getPets();
        pets.add(persistedPet);
        customer.setPets(pets);

        // Save infor customer
        customerRepository.save(customer);

        return persistedPet;

    }
}
