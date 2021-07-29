package com.epam.esm.service.exception;

import lombok.Getter;

/**
 * Exception indicating problems with tags -- user input does not satisfy constraints or client request is invalid due
 * to current state of specified tag in database. User friendly specifics about exception can be retrieved using {@link
 * InvalidTagException#getReason()}
 */
public class InvalidTagException extends ServiceException {
    @Getter
    private final Reason reason;
    @Getter
    private String tagDescription;

    public InvalidTagException(String message, Reason reason) {
        super(message);
        this.reason = reason;
    }

    public InvalidTagException(String message, Reason reason, String tagDescription) {
        this(message, reason);
        this.tagDescription = tagDescription;
    }

    public InvalidTagException(String message, Throwable thr, Reason reason) {
        super(message, thr);
        this.reason = reason;
    }

    public InvalidTagException(String message, Throwable thr, Reason reason, String tagDescription) {
        this(message, thr, reason);
        this.tagDescription = tagDescription;
    }

    public enum Reason {
        NOT_FOUND, ALREADY_EXISTS
    }
}
