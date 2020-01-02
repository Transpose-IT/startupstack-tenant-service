package dev.startupstack.tenantservice.models;

import java.util.LinkedList;

/**
 * WebResponseModel that represents a response sent back to the client
 */
public class WebResponseModel {

    private int statuscode;
    private Object responseObject;

    private LinkedList<String> response = new LinkedList<>();

    public WebResponseModel() {

    }

    public WebResponseModel(String message, int statuscode) {
        this.response.add(message);
        this.statuscode = statuscode;
    }

    public WebResponseModel(String message, int statuscode, Object object) {
        this.response.add(message);
        this.statuscode = statuscode;
        this.responseObject = object;

    }

    public WebResponseModel(LinkedList<String> messages, int statuscode) {
        this.response = messages;
        this.statuscode = statuscode;
    }
    
    public int getStatusCode() {
        return statuscode;
    }

    public void setStatusCode(int code) {
        this.statuscode = code;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public LinkedList<String> getResponse() {
        return this.response;
    }

    public void setResponse(LinkedList<String> messages) {
        this.response = messages;
    }
}