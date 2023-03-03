package com.akraness.akranesswaitlist.exception;

import org.springframework.security.core.AuthenticationException;

public class ApplicationAuthenticationException extends AuthenticationException{
    public ApplicationAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ApplicationAuthenticationException(String msg) {
        super(msg);
    }
}
