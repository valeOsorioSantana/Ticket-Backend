package com.ticketlite.demo.structure.exception;

public class NotFoundException extends  RuntimeException {
    private static final long serialVersionUID = 1;

    public NotFoundException(String message) {super(message);}
}
