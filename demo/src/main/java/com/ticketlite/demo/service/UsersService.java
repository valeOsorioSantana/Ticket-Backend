package com.ticketlite.demo.service;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.security.CreateUserRequest;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsersService {

    //Atributos

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    //Importante para conectar el repository
    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
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
    public String saveUser(CreateUserRequest request) throws ConflictException {
        try {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }

            if (usersRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Este Email ya ha sido registrado");
            }

            UsersEntity newUser = new UsersEntity();

            newUser.setEmail(request.getEmail());
            newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            newUser.setName(request.getName());
            newUser.setLastName(request.getLastName());
            newUser.setPhone(request.getPhone());
            newUser.setCity(request.getCity());
            newUser.setBio(request.getBio());
            newUser.setAvatarUrl(request.getAvatarUrl());

            usersRepository.save(newUser);
            return "Se creo correctamente el usuario: " + request.getName() + " " + request.getLastName();
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
            editUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            editUser.setName(user.getName());
            editUser.setLastName(user.getLastName());
            editUser.setPhone(user.getPhone());
            editUser.setCity(user.getCity());
            editUser.setBio(user.getBio());
            editUser.setAvatarUrl(user.getAvatarUrl());

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

    // AUTHENTICATE

    public Optional<UsersEntity> authenticateUser(String email, String password) {
        // Buscar el usuario por email
        Optional<UsersEntity> user = usersRepository.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return null; // Usuario no encontrado o contraseña incorrecta
        }

        return user;
    }
}