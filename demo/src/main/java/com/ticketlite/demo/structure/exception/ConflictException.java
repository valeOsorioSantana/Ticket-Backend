package com.ticketlite.demo.structure.exception;

public class ConflictException extends Throwable{
    private static final long serialVersionUID = 1;

    public ConflictException(String message) { super(message);}
}
