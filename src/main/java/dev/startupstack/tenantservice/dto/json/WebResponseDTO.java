package dev.startupstack.tenantservice.dto.json;

import java.util.LinkedList;

/**
 * ResponseEntity
 */
public class WebResponseDTO {

    private String message;
    private int statuscode;
    private Object responseObject;

    private LinkedList<String> messages = new LinkedList<>();

    public WebResponseDTO() {

    }

    public WebResponseDTO(String message, int statuscode) {
        this.message = message;
        this.statuscode = statuscode;
    }

    public WebResponseDTO(String message, int statuscode, Object object) {
        this.message = message;
        this.statuscode = statuscode;
        this.responseObject = object;

    }

    public WebResponseDTO(LinkedList<String> messages, int statuscode) {
        this.messages = messages;
        this.statuscode = statuscode;
    }
    

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
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

    public LinkedList<String> getMessages() {
        return this.messages;
    }

    public void setMessages(LinkedList<String> messages) {
        this.messages = messages;
    }
}