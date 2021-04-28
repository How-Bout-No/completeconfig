package io.github.how_bout_no.completeconfig.exception;

/**
 * Thrown to indicate that an annotation has been passed an illegal or inappropriate parameter.
 */
public class IllegalAnnotationParameterException extends RuntimeException {

    public IllegalAnnotationParameterException(String message) {
        super(message);
    }

}