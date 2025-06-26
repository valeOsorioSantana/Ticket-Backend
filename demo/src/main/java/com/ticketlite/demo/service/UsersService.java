package com.ticketlite.demo.service;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    //Atributos

    private UsersRepository usersRepository;
    //Importante para conectar el repository
    @Autowired
    //Constructor
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    //Metodos
    //GET ALL
    public List<UsersEntity> getAllUsers(){
        return usersRepository.findAll();

    }

    //GET BY ID
    public Optional<UsersEntity> getById(Long UserId){
        return usersRepository.findById(UserId);
    }

    //POST
    public String saveUser(UsersEntity user) throws ConflictException {
        try {

            if (usersRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Este Email ya ha sido registrado");
            }

            UsersEntity newUser = new UsersEntity();

            newUser.setEmail(user.getEmail());
            newUser.setPasswordHash(user.getPasswordHash());
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setPhone(user.getPhone());
            newUser.setCity(user.getCity());
            newUser.setBio(user.getBio());
            newUser.setAvatarUrl(user.getAvatarUrl());
            newUser.setRole(user.isRole());

            usersRepository.save(newUser);
            return "Se creo correctamente el usuario: " + user.getFirstName() + " " + user.getLastName();
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage(), e);
        }
    }

    //PUT
    public UsersEntity editUser(UsersEntity user, Long userId) throws NotFoundException {
        try {

            UsersEntity editUser = usersRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

            editUser.setEmail(user.getEmail());
            editUser.setPasswordHash(user.getPasswordHash());
            editUser.setFirstName(user.getFirstName());
            editUser.setLastName(user.getLastName());
            editUser.setPhone(user.getPhone());
            editUser.setCity(user.getCity());
            editUser.setBio(user.getBio());
            editUser.setAvatarUrl(user.getAvatarUrl());
            editUser.setRole(user.isRole());

            usersRepository.save(editUser);
            return usersRepository.save(editUser);


        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage(), e);
        }
    }

    //DELETE
    public void deleteUser (Long userId){
        if (usersRepository.existsById(userId)){
            usersRepository.deleteById(userId);
        }else {
            throw new RuntimeException("Usuario no encontrado por ID: " + userId);
        }
    }
}
