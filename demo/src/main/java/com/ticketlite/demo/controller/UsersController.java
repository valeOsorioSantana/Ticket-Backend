package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.service.UsersService;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestion de Usuarios", description = "Operaciones Crud para la administracion de los usuarios")
public class UsersController {

    //Atributo

    private UsersService usersService;

    //Constructor
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //Metodos
    //GET ALL
    @Operation(summary = "Obtener todos los usuarios", description = "Recupera una lista de todos los usuarios en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UsersEntity.class)))),
            @ApiResponse(responseCode = "204", description = "No hay usuarios disponibles")
    })
    @GetMapping("/")
    public ResponseEntity<List<UsersEntity>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    //GET BY ID
    @Operation(summary = "Buscar un usuario por id", description = "Busca un usuario por id en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsersEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){
        try {
            Optional<UsersEntity> user = usersService.getById(userId);
            if (user.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(user.get());
            }else {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }

    //POST
    @Operation(summary = "Guardar un Usuario", description = "Crea y guarda un nuevo usuario en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente el usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsersEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos de eventos invalidos")
    })
    @PostMapping("/")
    public ResponseEntity<?> saveUser(@RequestBody UsersEntity user){
        try {
            String result = usersService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el usuario");
        }catch (ConflictException e){
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    //PUT
    @Operation(summary = "Actualizar un usuario", description = "Actualiza y guarda un usuario en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado con exito.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsersEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el usuario")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<String>editUser(@PathVariable Long userId, @RequestBody UsersEntity edit){
        try {
            UsersEntity result = usersService.editUser(edit, userId);

            return ResponseEntity.ok("Usuario actualizado con exito.");
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al actualizar el usuario");
        }
    }

    //DELETE
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado Correctamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsersEntity.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        try {
            usersService.deleteUser(userId);

            return ResponseEntity.ok("Usuario eliminado Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
