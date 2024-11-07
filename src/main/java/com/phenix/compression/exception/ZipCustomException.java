package com.phenix.compression.exception;

/**
 * Erreur avec un Zip.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class ZipCustomException extends Exception {

    /**
     * Construit une erreur {@code ZipCustomException} avec un message.
     *
     * @param message Le message
     */
    public ZipCustomException(String message) {
        super(message);
    }
}
