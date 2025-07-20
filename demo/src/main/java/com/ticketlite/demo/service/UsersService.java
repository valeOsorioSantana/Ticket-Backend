package com.ticketlite.demo.service;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.security.CreateUserRequest;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    //Atributos

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private EmailServiceImple emailServiceImple;

    //Importante para conectar el repository
    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, EmailServiceImple emailServiceImple) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailServiceImple = emailServiceImple;
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
    public String saveUser(CreateUserRequest request, boolean user) throws ConflictException {
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

            if (!user) {
                newUser.setRole("ADMIN");
            } else {
                newUser.setRole("USER");
            }

            newUser.setCity(request.getCity());
            newUser.setBio(request.getBio());
            newUser.setEstado(true);
            newUser.setAvatarUrl(request.getAvatarUrl());

            usersRepository.save(newUser);
            emailServiceImple.sendConfirmationEmail(newUser.getEmail(), newUser.getName());
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

        // Consulta al usuario
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        // Lo asigna a su entity respectiva
        UsersEntity user = userOpt.get();

        // Inhabilita al usuario
        user.setEstado(false);

        // Lo actualiza
        usersRepository.save(user);
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