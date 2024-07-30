package ru.sparural.file.exceptions;

public class RedirectException extends ApplicationException {
    private final String redirectUrl;

    public RedirectException(String redirectUrl) {
        super();
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
