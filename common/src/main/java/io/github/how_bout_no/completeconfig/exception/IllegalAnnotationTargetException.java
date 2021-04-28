package io.github.how_bout_no.completeconfig.exception;

/**
 * Thrown to indicate that an annotation targets an illegal or inappropriate member.
 */
public class IllegalAnnotationTargetException extends RuntimeException {

    public IllegalAnnotationTargetException(String message) {
        super(message);
    }

}