package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.NotificationsEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.NotificationsRepository;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationsService {

    //Atributos

    private NotificationsRepository notificationsRepository;
    private UsersRepository usersRepository;
    private EventsRepository eventsRepository;

    //Importante para conectar el repository
    @Autowired
    //Constructor

    public NotificationsService(NotificationsRepository notificationsRepository, UsersRepository usersRepository, EventsRepository eventsRepository) {
        this.notificationsRepository = notificationsRepository;
        this.usersRepository = usersRepository;
        this.eventsRepository = eventsRepository;
    }

    //Metodos
    //GET ALL NOTIFICATIONS BY ID USER
    public List<NotificationsEntity> getNotificationsByUser(Long userId) {
        try {
            return notificationsRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener notificaciones del usuario: " + e.getMessage(), e);
        }
    }

    //POST
    //crear una nueva notificacion
    public NotificationsEntity crearNotifi(Long userId, Long eventId, String message, String type){
        try {

            UsersEntity user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            EventsEntity event = null;
            if (eventId != null){
                event = eventsRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Evento no encontrado"));
            }

            NotificationsEntity newNotification = new NotificationsEntity();

            newNotification.setUser(user);
            newNotification.setEvent(event);
            newNotification.setMessageContent(message);
            newNotification.setType(type);
            newNotification.setRead(false);

            return notificationsRepository.save(newNotification);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear la notificacion: " + e.getMessage(), e);
        }
    }

    //PUT
    //marcar una notificacion como leida
    public NotificationsEntity markRead(Long notificationId){
        try {
        NotificationsEntity readNotification = notificationsRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        readNotification.setRead(true);

        return notificationsRepository.save(readNotification);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al marcar notificaciones como leidas: " + e.getMessage(), e);
        }
    }

    //PUT
    //marcar notificaciones como leidas
    public void markAllNotiRead(Long userId){
        try {
            List<NotificationsEntity> readNotifications = notificationsRepository.findByUserIdAndIsReadFalse(userId);

            if (readNotifications.isEmpty()) {
                throw new NotFoundException("No hay notificaciones no leídas para el usuario con ID: " + userId);
            }

            for (NotificationsEntity notification : readNotifications) {
                notification.setRead(true);
            }

            notificationsRepository.saveAll(readNotifications);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al marcar notificaciones como leidas: " + e.getMessage(), e);
        }
    }

    //DELETE
    //eliminar una notificacion
    public void deleteNoti(Long notificationId) {
        try {
            notificationsRepository.deleteById(notificationId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la notificación: " + e.getMessage(), e);
        }
    }
}
