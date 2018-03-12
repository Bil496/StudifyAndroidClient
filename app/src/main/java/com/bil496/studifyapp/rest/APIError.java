package com.bil496.studifyapp.rest;

/**
 * Created by burak on 3/12/2018.
 */

public class APIError {

    private int statusCode;
    private String message;

    public APIError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}