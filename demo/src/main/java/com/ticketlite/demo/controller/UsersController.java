package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.security.CreateUserRequest;
import com.ticketlite.demo.service.UsersService;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    //Metodos

    //JWT
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UsersEntity user) {
        return ResponseEntity.ok(user);
    }

    //GET ALL
    @Operation(summary = "Obtener todos los usuarios", description = "Recupera una lista de todos los usuarios en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UsersEntity.class)))),
            @ApiResponse(responseCode = "204", description = "No hay usuarios disponibles")
    })
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @Operation(
            summary = "Guardar un Usuario",
            description = "Crea y guarda un nuevo usuario en la base de datos. El parámetro 'user' indica si es usuario común o no."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Se creó exitosamente el usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) // porque devuelves un mensaje, no un objeto
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Datos del usuario inválidos o en conflicto",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            )
    })
    @Parameters({
            @Parameter(name = "user", description = "Indica si el usuario es de tipo común (true) o no (false)", required = true)
    })
    //@PreAuthorize("hasRole('USER')")
    @PostMapping("/{user}")
    public ResponseEntity<?> saveUser(@Valid @RequestBody CreateUserRequest request, @PathVariable boolean user) {
        try {
            String result = usersService.saveUser(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el usuario");
        } catch (ConflictException e) {
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
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{userId}")
    public ResponseEntity<String>editUser(@PathVariable Long userId, @RequestBody UsersEntity edit){
        try {
            usersService.editUser(edit, userId);

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
    @PreAuthorize("hasRole('ADMIN')")
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
