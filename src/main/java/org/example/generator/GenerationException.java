package org.example.generator;

public class GenerationException extends RuntimeException {

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerationException(String message) {
        super(message);
    }
}
