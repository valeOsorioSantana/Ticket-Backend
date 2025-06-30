package com.ticketlite.demo.service;

import com.ticketlite.demo.model.PaymentsEntity;
import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.model.repository.PaymentsRepository;
import com.ticketlite.demo.model.repository.RegistrationsRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentsService {

    //Atributos

    private PaymentsRepository paymentsRepository;
    private RegistrationsRepository registrationsRepository;
    //Importante para conectar el repository
    @Autowired
    //Constructor
    public PaymentsService(PaymentsRepository paymentsRepository, RegistrationsRepository registrationsRepository) {
        this.paymentsRepository = paymentsRepository;
        this.registrationsRepository = registrationsRepository;
    }

    //Metodos
    //GET BY ID
    //obtener pago por id
    public PaymentsEntity getPaymentById(Long paymentId) {
        return paymentsRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
    //obtener pago por id de registro (unico)
    public Optional<PaymentsEntity> getPaymentByRegistration(Long registrationId) {
        return paymentsRepository.findByRegistrationId(registrationId);
    }

    //POST
    //crear un nuevo pago
    public PaymentsEntity createPayment(Long registrationId, String method, BigDecimal amount) {
        try {
            boolean exists = paymentsRepository.existsByRegistrationId(registrationId);
            if (exists) {
                throw new IllegalStateException("Ya existe un pago para este registro");
            }

            RegistrationsEntity registration = registrationsRepository.findById(registrationId).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            PaymentsEntity payment = new PaymentsEntity();

            payment.setRegistration(registration);
            payment.setMethod(method);
            payment.setAmount(amount);
            payment.setStatus(PaymentsEntity.PaymentStatus.PENDIENTE);

            return paymentsRepository.save(payment);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el pago: " + e.getMessage(), e);
        }
    }

    //PUT
    //actualizar estado del pago
    public PaymentsEntity updatePaymentStatus(Long paymentId, PaymentsEntity.PaymentStatus status) {
        try {
            PaymentsEntity payment = paymentsRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Pago no encontrado"));

            payment.setStatus(status);

            return paymentsRepository.save(payment);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al actualizar el pago: " + e.getMessage(), e);
        }
    }

    //DELETE
    //eliminar pago
    public void deletePay (Long paymentId){
        if (paymentsRepository.existsById(paymentId)){
            paymentsRepository.deleteById(paymentId);
        }else {
            throw new RuntimeException("Pago no encontrado por ID: " + paymentId);
        }
    }

}
