package com.ticketlite.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImple {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailServiceImple(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarCorreoRecuperacion(String destinatario, String nombreUsuario, String urlRecuperacion) throws MessagingException {
        try {
            // Prepara el contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("nombreUsuario", nombreUsuario);
            context.setVariable("urlRecuperacion", urlRecuperacion);

            // Genera el contenido HTML del correo
            String htmlContent = templateEngine.process("email-recuperacion", context);

            // Prepara el mensaje MIME
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject("Recuperación de Contraseña");
            helper.setText(htmlContent, true); // true indica que es HTML

            // Envía el correo
            mailSender.send(mensaje);
        } catch (MailException e) {
            throw new MessagingException("Error al enviar el correo de recuperación: " + e.getMessage(), e);
        }
    }

    public void sendConfirmationEmail(String toEmail, String userName) {
        try {
            // Prepara el contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("name", userName);

            // Genera el contenido HTML del correo
            String html = templateEngine.process("welcome-email.html", context);

            // Prepara el mensaje MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a Nuestra Plataforma");
            helper.setText(html, true);
            helper.setFrom("noreply@tusitio.com");

            // Envía el correo
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }

    }

    public void sendCancelationEmail(String toEmail, String userName, String nombreEvento) {
        try {
            // Prepara el contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("name", userName);
            context.setVariable("evento", nombreEvento);

            // Carga y procesa el template HTML
            String html = templateEngine.process("cancelacion-email.html", context);

            // Prepara el correo MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de Cancelación de Ticket");
            helper.setText(html, true);
            helper.setFrom("noreply@tusitio.com");

            // Enviar
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de cancelación", e);
        }
    }


    public void enviarTicketConQR(String emailDestino, String asunto, String cuerpo, byte[] imagenQR) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

            helper.setTo(emailDestino);
            helper.setSubject(asunto);
            helper.setText(cuerpo);

            // Adjuntar QR
            ByteArrayResource resource = new ByteArrayResource(imagenQR);
            helper.addAttachment("ticket-qr.png", resource);

            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}