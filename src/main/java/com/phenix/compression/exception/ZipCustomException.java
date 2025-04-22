package com.phenix.compression.exception;

import jakarta.validation.constraints.NotNull;

/**
 * Exception avec un Zip ({@link com.phenix.compression.ZipFiles ZipFiles}).
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class ZipCustomException extends Exception {

    /**
     * Construit une {@link ZipCustomException} avec un message.
     *
     * @param message Le message.
     */
    public ZipCustomException(String message) {
        super(message);
    }

    /**
     * Construit une {@link ZipCustomException} avec un message et une cause.
     *
     * @param message Le message.
     * @param cause La cause.
     */
    public ZipCustomException(String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
